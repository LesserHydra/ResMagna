package com.lesserhydra.praedagrandis.configuration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.comphenix.attribute.Attributes.Attribute;
import com.comphenix.attribute.Attributes.AttributeType;
import com.comphenix.attribute.Attributes.Operation;
import com.lesserhydra.praedagrandis.logging.GrandLogger;
import com.lesserhydra.praedagrandis.logging.LogType;
import com.lesserhydra.util.StringTools;

public class GrandAttribute
{
	//(\w+)\s+([+\-*/])\s+([\d\.]+)(%)?
	static private final Pattern LINE_PATTERN = Pattern.compile("(\\w+)\\s+([+\\-*/])\\s+([\\d\\.]+)(%)?");
	
	private final String 		name;
	private final AttributeType type;
	private final Operation 	operation;
	private final double 		operand;
	
	private GrandAttribute(String name, AttributeType type, Operation operation, double operand) {
		this.name = name;
		this.type = type;
		this.operation = operation;
		this.operand = operand;
	}
	
	static public GrandAttribute fromString(String attributeLine) {
		//Match line
		Matcher lineMatcher = LINE_PATTERN.matcher(attributeLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid attribute line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Name
		String nameString = lineMatcher.group(1).toLowerCase();
		
		//AttributeType
		AttributeType attributeType = getTypeFromName(nameString);
		if (attributeType == null) {
			GrandLogger.log("Invalid attribute type: " + nameString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Operation
		String operationString = lineMatcher.group(2);
		boolean isPercentage = "%".equals(lineMatcher.group(4));
		Operation operationType = getOperation(operationString, isPercentage);
		if (operationType == null) {
			GrandLogger.log("Invalid attribute operation: " + operationString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Operand
		String operandString = lineMatcher.group(3);
		double operandValue = getOperand(operandString, operationString, isPercentage);
		
		return new GrandAttribute(nameString, attributeType, operationType, operandValue);
	}

	static private AttributeType getTypeFromName(String nameString)
	{
		switch (nameString) {
		case "attackdamage":		return AttributeType.GENERIC_ATTACK_DAMAGE;
		case "followrange":			return AttributeType.GENERIC_FOLLOW_RANGE;
		case "knockbackresistance":	return AttributeType.GENERIC_KNOCKBACK_RESISTANCE;
		case "maxhealth":			return AttributeType.GENERIC_MAX_HEALTH;
		case "movementspeed":		return AttributeType.GENERIC_MOVEMENT_SPEED;
		
		default:					return null;
		}
	}
	
	private static Operation getOperation(String operationString, boolean operandIsPercentage) {
		switch (operationString) {
		case "+": case "-":
			if (operandIsPercentage) return Operation.ADD_PERCENTAGE;
			return Operation.ADD_NUMBER;
		case "*": case "/": return Operation.MULTIPLY_PERCENTAGE;
		
		default: return null;
		}
	}
	
	private static double getOperand(String operandString, String operationString, boolean operandIsPercentage) {
		if (!StringTools.isFloat(operandString)) {
			GrandLogger.log("Invalid attribute line operand: " + operandString, LogType.CONFIG_ERRORS);
			GrandLogger.log("Expected floating point value.", LogType.CONFIG_ERRORS);
			return 0D;
		}
		
		double result = Double.parseDouble(operandString);
		if (operandIsPercentage) result /= 100D;
		
		switch (operationString) {
		case "+": return result;
		case "-": return -result;
		case "*": return result - 1;
		case "/": return (1/result) - 1;
		
		default: throw new IllegalStateException();
		}
	}

	public Attribute build() {
		return Attribute.newBuilder().name(name).type(type).operation(operation).amount(operand).build();
	}
}
