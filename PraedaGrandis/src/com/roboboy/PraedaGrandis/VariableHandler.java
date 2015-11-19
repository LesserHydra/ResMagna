package com.roboboy.PraedaGrandis;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Configuration.VariableConditional;
import com.roboboy.PraedaGrandis.Configuration.VariableOperator;

public class VariableHandler
{
	final static private Map<Player, Map<String, Integer>> variables = new HashMap<Player, Map<String, Integer>>();
	
	static public void registerPlayer(Player p) {
		if (variables.containsKey(p)) return;
		variables.put(p, new HashMap<String, Integer>());
	}
	
	static public int operate(Player p, String varName, VariableOperator operation, int number) {
		Map<String, Integer> playerVars = variables.get(p);
		Integer value = playerVars.get(varName);
		if (value == null) value = 0;
		
		value = operation.apply(value, number);
		
		playerVars.put(varName, value);
		return value;
	}
	
	static public int operate(Player p, String var1Name, VariableOperator operation, String var2Name) {
		return operate(p, var1Name, operation, get(p, var2Name));
	}
	
	static public boolean checkCondition(Player p, String varName, VariableConditional conditional, int number) {
		Integer value = variables.get(p).get(varName);
		if (value == null) value = 0;
		
		return conditional.check(value, number);
	}
	
	static public boolean checkCondition(Player p, String var1Name, VariableConditional conditional, String var2Name) {
		return checkCondition(p, var1Name, conditional, get(p, var2Name));
	}
	
	static public int get(Player p, String varName) {
		//TODO: if (!variables.containsKey(p)) throw ;
		Integer value = variables.get(p).get(varName);
		return (value == null ? 0 : value);
	}
}
