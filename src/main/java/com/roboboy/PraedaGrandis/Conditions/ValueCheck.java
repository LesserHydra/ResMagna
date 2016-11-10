package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Arguments.VariableConditional;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.util.StringTools;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ValueCheck<T extends Comparable<T>> implements Condition.ForPlayer {
	
	//([=<>]+)\s*(\w+)
	private static final Pattern isLinePattern = Pattern.compile("([=<>]+)\\s*(\\w+)");
	
	@Nullable
	static ValueCheck<Integer> makeInt(String argLine, Function<Player, Integer> getValue) {
		//Match
		Matcher lineMatcher = isLinePattern.matcher(argLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid value condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get operator
		VariableConditional conditional = VariableConditional.fromSymbol(lineMatcher.group(1));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(2);
		if (!StringTools.isInteger(operand)) {
			GrandLogger.log("Invalid value condition operand:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		int number = Integer.parseInt(operand);
		
		return new ValueCheck<>(getValue, conditional, number);
	}
	
	@Nullable
	static ValueCheck<Double> makeDouble(String argLine, Function<Player, Double> getValue) {
		//Match
		Matcher lineMatcher = isLinePattern.matcher(argLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid value condition line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get operator
		VariableConditional conditional = VariableConditional.fromSymbol(lineMatcher.group(1));
		
		//Operand may be an integer or the name of a variable
		String operand = lineMatcher.group(2);
		if (!StringTools.isInteger(operand)) {
			GrandLogger.log("Invalid value condition operand:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + argLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		double number = Double.parseDouble(operand);
		
		return new ValueCheck<>(getValue, conditional, number);
	}
	
	private final Function<Player, T>       getValue;
	private final VariableConditional       conditional;
	private final T					        number;
	
	private ValueCheck(Function<Player, T> getValue, VariableConditional conditional, T number) {
		this.getValue = getValue;
		this.conditional = conditional;
		this.number = number;
	}
	
	@Override
	public boolean test(Player target) {
		return conditional.check(getValue.apply(target), number);
	}
	
}
