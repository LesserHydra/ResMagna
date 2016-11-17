package com.lesserhydra.resmagna;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import com.lesserhydra.resmagna.arguments.VariableOperator;

public class VariableHandler
{
	final static private Map<String, Map<String, Integer>> variables = new HashMap<>();
	
	static public void registerPlayer(Player p) {
		if (variables.containsKey(p.getName())) return;
		variables.put(p.getName(), new HashMap<String, Integer>());
	}
	
	static public int operate(Player p, String varName, VariableOperator operation, int number) {
		Map<String, Integer> playerVars = variables.get(p.getName());
		Integer value = playerVars.get(varName);
		if (value == null) value = 0;
		
		value = operation.apply(value, number);
		
		playerVars.put(varName, value);
		return value;
	}
	
	static public int operate(Player p, String var1Name, VariableOperator operation, String var2Name) {
		return operate(p, var1Name, operation, get(p, var2Name));
	}
	
	static public int get(Player p, String varName) {
		Integer value = variables.get(p.getName()).get(varName);
		return (value == null ? 0 : value);
	}
}
