package com.lesserhydra.resmagna;

import com.lesserhydra.resmagna.variables.VariableConstruct;
import com.lesserhydra.resmagna.variables.VariableOperator;
import com.lesserhydra.resmagna.variables.Variable;
import com.lesserhydra.resmagna.variables.Variables;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VariableHandler {
	
	final static private Map<String, Map<String, Variable>> variables = new HashMap<>();
	
	public static void registerPlayer(Player p) {
		if (variables.containsKey(p.getName())) return;
		variables.put(p.getName(), new HashMap<>());
	}
	
	@NotNull
	public static Variable operate(Player p, String varName, VariableOperator operation, VariableConstruct operand) {
		Map<String, Variable> playerVars = variables.get(p.getName());
		Variable value = playerVars.get(varName);
		if (value == null) value = Variables.NONE;
		
		value = operation.apply(value, operand.get(p));
		
		playerVars.put(varName, value);
		return value;
	}
	
	@NotNull
	public static Variable get(Player p, String varName) {
		Variable value = variables.get(p.getName()).get(varName);
		return value == null ? Variables.NONE : value;
	}
	
	@NotNull
	public static VariableConstruct linkConstruct(String varName) {
		return p -> get(p, varName);
	}
	
}
