package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.BlockPattern;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.configuration.GroupingParser;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.TargeterFactory;
import com.lesserhydra.resmagna.targeters.Targeters;
import com.lesserhydra.util.StringTools;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstructFactory {
	
	//(\w+)?\s*\((\$\d+)\)
	private static final Pattern groupedPattern = Pattern.compile("(\\w+)?\\s*\\((\\$\\d+)\\)");
	
	public static ValueConstruct makeLiteral(Value value) {
		return new ValueConstruct() {
			@Override public boolean mayHave(ValueType type) { return value.getType().has(type); }
			@Override public Value evaluate(Target target) { return value; }
		};
	}
	
	public static ValueConstruct make(Targeter targeter) {
		if (targeter == Targeters.NONE) return Constructs.NONE;
		return new ValueConstruct() {
			@Override public boolean mayHave(ValueType type) { return true; }
			@Override public Value evaluate(Target target) { return targeter.getRandomTarget(target).current(); }
		};
	}
	
	public static ValueConstruct make(GrandLocation grandLocation) {
		return new ValueConstruct() {
			@Override public boolean mayHave(ValueType type) { return ValueType.LOCATION.has(type); }
			@Override public Value evaluate(Target target) { return Values.wrap(grandLocation.calculate(target)); }
		};
	}
	
	/**
	 * Parse construct string.
	 * @param string Construct string
	 * @return Resulting construct
	 */
	@NotNull
	public static ValueConstruct parse(String string) {
		return parse(string, ValueType.UNKNOWN);
	}
	
	@NotNull
	public static ValueConstruct parse(String string, ValueType expected) {
		char firstChar = string.charAt(0);
		
		//String
		if (firstChar == '"') return parseString(string);
		
		//Targeter
		else if (firstChar == '@') return parseTargeter(string);
		
		//Boolean
		else if (StringTools.isExtendedBoolean(string)) return makeLiteral(Values.wrap(StringTools.parseExtendedBoolean(string)));
		
		//Integral value
		else if (StringTools.isInteger(string)) return makeLiteral(Values.wrap(Integer.parseInt(string)));
		
		//Floating point value
		else if (StringTools.isFloat(string)) return makeLiteral(Values.wrap(Double.parseDouble(string)));
		
		//Grouped (location, block masks and patterns, item lists
		ValueConstruct grouped = parseGrouped(string, expected);
		if (grouped != null) return grouped;
		
		//Special or variable
		return parseSpecial(string);
	}
	
	@NotNull
	private static ValueConstruct parseString(String string) {
		//TODO: Proper
		return makeLiteral(Values.wrap(string.substring(1, string.length()-1)));
	}
	
	@NotNull
	private static ValueConstruct parseTargeter(String string) {
		Targeter result = TargeterFactory.build(string);
		if (result == null) return Constructs.NONE;
		return new ValueConstruct() {
			@Override public boolean mayHave(ValueType type) { return true; }
			@Override public Value evaluate(Target target) { return result.getRandomTarget(target).current(); }
		};
	}
	
	private static ValueConstruct parseGrouped(String string, ValueType expected) {
		//"identifier(grouping)", ex. "location[Y+1.8 F+1]" or mask[air, !>air, !>huge mushroom 1]
		GroupingParser groupingParser = new GroupingParser(string);
		String simplified = groupingParser.getSimplifiedString();
		Matcher matcher = groupedPattern.matcher(simplified);
		
		if (!matcher.matches()) return null;
		
		String identifier = matcher.group(1);
		String groupingId = matcher.group(2);
		
		if (identifier == null) return parseUnidentifiedGroup(groupingParser.getGrouping(groupingId), expected);
		return parseIdentifiedGroup(identifier, groupingParser.getGrouping(groupingId));
	}
	
	private static ValueConstruct parseIdentifiedGroup(String identifier, String grouping) {
		switch (identifier) {
			case "location": return parseGrandLocation(grouping);
			case "mask": return parseBlockMask(grouping);
			case "pattern": return parseBlockPattern(grouping);
			
			default:
				GrandLogger.log("Unknown identifier: " + identifier, LogType.CONFIG_ERRORS);
				return Constructs.NONE;
		}
	}
	
	private static ValueConstruct parseUnidentifiedGroup(String grouping, ValueType expected) {
		switch (expected) {
			case LOCATION: return parseGrandLocation(grouping);
			case BLOCK_MASK: return parseBlockMask(grouping);
			case BLOCK_PATTERN: return parseBlockPattern(grouping);
			
			case UNKNOWN:
				GrandLogger.log("Cannot determine grouping type by context: " + grouping, LogType.CONFIG_ERRORS);
				GrandLogger.log("  Use identifier (ex. location[Y+10 FH+1]", LogType.CONFIG_ERRORS);
				return Constructs.NONE;
			
			default:
				GrandLogger.log("Grouping does not fit expected type " + expected.name() + ": " + grouping, LogType.CONFIG_ERRORS);
				return Constructs.NONE;
		}
	}
	
	private static ValueConstruct parseGrandLocation(String locString) {
		GrandLocation result = GrandLocation.buildFromString(locString);
		if (result == null) return Constructs.NONE;
		return make(result);
	}
	
	private static ValueConstruct parseBlockMask(String maskString) {
		BlockMask result = BlockMask.buildFromString(maskString);
		if (result == null) return Constructs.NONE;
		return makeLiteral(Values.wrap(result));
	}
	
	private static ValueConstruct parseBlockPattern(String patternString) {
		BlockPattern result = BlockPattern.buildFromString(patternString);
		if (result == null) return Constructs.NONE;
		return makeLiteral(Values.wrap(result));
	}
	
	private static ValueConstruct parseSpecial(String string) {
		switch (string.toLowerCase()) {
			case "health": return Constructs.HEALTH;
			case "fireticks": return Constructs.FIRE_TICKS;
			case "hunger": return Constructs.HUNGER;
			case "food": return Constructs.FOOD_LEVEL;
			case "saturation": return Constructs.SATURATION;
			case "exhaustion": return Constructs.EXHAUSTION;
			case "totalexp": return Constructs.TOTAL_EXP;
			case "exp": return Constructs.EXP;
			case "levels": return Constructs.LEVELS;
			
			default:
				GrandLogger.log("Global Variable: " + string, LogType.CONFIG_PARSING);
				return VariableHandler.linkConstruct(string);
		}
	}
	
}
