package com.lesserhydra.resmagna;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.variables.Value;
import com.lesserhydra.resmagna.variables.ValueConstruct;
import com.lesserhydra.resmagna.variables.ValueConstructs;
import com.lesserhydra.resmagna.variables.ValueOperator;
import com.lesserhydra.resmagna.variables.Values;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VariableHandler {
	
	final static private Map<String, Map<String, Value>> variables = new HashMap<>();
	
	public static void registerPlayer(Player p) {
		if (variables.containsKey(p.getName())) return;
		variables.put(p.getName(), new HashMap<>());
	}
	
	@NotNull
	public static Value operate(Target target, String varName, ValueOperator operation, ValueConstruct operand) {
		if (!target.isPlayer()) return Values.NONE;
		
		Map<String, Value> playerVars = variables.get(target.asPlayer().getName());
		Value value = playerVars.get(varName);
		if (value == null) value = Values.NONE;
		
		value = operation.apply(value, operand.get(target));
		
		playerVars.put(varName, value);
		return value;
	}
	
	@NotNull
	public static Value get(Player p, String varName) {
		Value value = variables.get(p.getName()).get(varName);
		return value == null ? Values.wrap(0) : value;
	}
	
	public static void set(Player p, String varName, Value value) {
		Map<String, Value> playerVars = variables.get(p.getName());
		playerVars.put(varName, value);
	}
	
	@NotNull
	public static ValueConstruct linkConstruct(String varName) {
		return ValueConstructs.makeSettable(t -> {
			if (!t.isPlayer()) {
				GrandLogger.log("Tried to access a global variable with non-player target.", LogType.RUNTIME_ERRORS);
				return Values.NONE;
			}
			//DEBUG: GrandLogger.log("Get var " + varName + ": " + get(t.asPlayer(), varName).asDouble(), LogType.DEBUG);
			return get(t.asPlayer(), varName);
		}, (t, v) -> {
			if (t.isPlayer()) {
				GrandLogger.log("Tried to access a global variable with non-player target.", LogType.RUNTIME_ERRORS);
				return;
			}
			//DEBUG: GrandLogger.log("Set var " + varName + ": " + v.asDouble(), LogType.DEBUG);
			set(t.asPlayer(), varName, v);
		});
	}
	
}
