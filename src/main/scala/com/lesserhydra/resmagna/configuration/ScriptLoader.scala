package com.lesserhydra.resmagna.configuration

import java.io.{File, FileReader}

import com.lesserhydra.resmagna.function.Functor
import com.lesserhydra.resmagna.logging.{GrandLogger, LogType}
import com.lesserhydra.resmagna.targeters.Target

import scala.collection.mutable
import scala.tools.nsc.interpreter.Scripted

//Static and singleton all in one!
object ScriptLoader extends ScriptLoader {
	private[ScriptLoader] lazy val engine = Scripted()
	
	def getInstance: ScriptLoader = this
	
	private class FunctionRequester extends Functor {
		private var function = Functor.NONE
		
		def run(target: Target): Unit = function.run(target)
		
		private[ScriptLoader] def returnRequest(function: Functor) {
			this.function = function
		}
	}
}

class ScriptLoader private() {
	
	private val customAbilities = new mutable.OpenHashMap[String, Functor]
	private val requestList = new mutable.Queue[(ScriptLoader.FunctionRequester, String)]
	private var fullyLoaded = false
	
	def requestFunction(requestName: String): Functor = {
		val l_requestName = requestName.toLowerCase
		if (!fullyLoaded) {
			val result = new ScriptLoader.FunctionRequester
			requestList += ((result, l_requestName))
			return result
		}
		
		val result = customAbilities.get(requestName)
		if (result.isEmpty) GrandLogger.log("Requested custom ability not found: " + requestName, LogType.CONFIG_ERRORS)
		result.getOrElse(Functor.NONE)
	}
	
	/**
	  * Reloads the ability configuration files.<br>
	  * <br>
	  * <strong>The ConfigManager must be reloaded first.</strong>
	  */
	def reload() {
		fullyLoaded = false
		customAbilities.clear()
		loadAll(ConfigManager.getInstance.getAbilityFolder)
		//Finish custom abilities after all GrandAbilities are constructed
		fullyLoaded = true
		handleRequests()
	}
	
	private def loadAll(folder: File) {
		val files = Option(folder.listFiles(file => file.getName.endsWith(".scala")))
		if (files.isEmpty) {
			GrandLogger.log("Error reading files from \"" + folder.getName + "\".", LogType.CONFIG_ERRORS)
			return
		}
		
		files.get.foreach(loadConfig)
	}
	
	/**
	  * Loads GrandAbilities from a single configuration file
	  */
	private def loadConfig(file: File) {
		val compiled = ScriptLoader.engine.compile(new FileReader(file))
		customAbilities.put(file.getName.toLowerCase, target => compiled.eval) //TODO: Setup, bind things, put methods separately
	}
	
	private def handleRequests() {
		for (request <- requestList) {
			//Map tuple values
			val (requester, requestName) = request
			//Get requested function
			val result = customAbilities.get(requestName)
			//Log error if not found
			if (result.isEmpty) GrandLogger.log("Requested custom ability not found: " + requestName, LogType.CONFIG_ERRORS)
			//Return request
			requester.returnRequest(result.getOrElse(Functor.NONE))
		}
		
		//Clear queue
		requestList.clear()
	}
	
}
