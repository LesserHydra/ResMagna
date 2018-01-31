package com.lesserhydra.resmagna.configuration;

import com.lesserhydra.bukkitutil.Attributes.Attribute;
import com.lesserhydra.bukkitutil.Attributes.AttributeType;
import com.lesserhydra.bukkitutil.Attributes.Operation;
import com.lesserhydra.bukkitutil.Attributes.Slot;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.util.StringTools;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

class GrandAttribute {
	
	//(\w+)\s+([\w.]+)\s*([^\w\s.]+)\s*([\d.]+)\s*(%)?
	static private final Pattern LINE_PATTERN = Pattern.compile("(\\w+)\\s+([\\w.]+)\\s*([^\\w\\s.]+)\\s*([\\d.]+)\\s*(%)?");
	
	private final AttributeType type;
	private final Slot          slot;
	private final Operation 	operation;
	private final double 		operand;
	
	private GrandAttribute(AttributeType type, Slot slot, Operation operation, double operand) {
		this.type = type;
		this.slot = slot;
		this.operation = operation;
		this.operand = operand;
	}
	
	@Nullable
	static GrandAttribute fromString(String attributeLine) {
		//Match line
		Matcher lineMatcher = LINE_PATTERN.matcher(attributeLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid attribute line format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Parse slot
		String slotString = lineMatcher.group(1);
		Slot slot = (slotString == null ? Slot.ALL : StringTools.parseEnum(slotString, Slot.class));
		if (slot == null) {
			GrandLogger.log("Invalid attribute slot: " + slotString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
		}
		
		//AttributeType
		String nameString = lineMatcher.group(2).toLowerCase();
		AttributeType attributeType = parseType(nameString);
		if (attributeType == null) {
			GrandLogger.log("Unknown attribute type: " + nameString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Operation
		String operationString = lineMatcher.group(3);
		boolean isPercentage = "%".equals(lineMatcher.group(5));
		Operation operationType = parseOperation(operationString, isPercentage);
		if (operationType == null) {
			GrandLogger.log("Invalid attribute line operator: " + operationString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Valid operators are: '+', '-', '*', and '/'.)", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Operand
		String operandString = lineMatcher.group(4);
		if (!StringTools.isFloat(operandString)) {
			GrandLogger.log("Invalid attribute line operand: " + operandString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Expected floating point value.", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + attributeLine, LogType.CONFIG_ERRORS);
			return null;
		}
		double operandValue = parseOperand(operandString, operationString, isPercentage);
		
		//Result
		return new GrandAttribute(attributeType, slot, operationType, operandValue);
	}

	private static AttributeType parseType(String lookup) {
		return StreamSupport.stream(AttributeType.values().spliterator(), false)
				.filter(type -> lookup.equalsIgnoreCase(type.getMinecraftId()))
				.findAny().orElse(null);
	}
	
	private static Operation parseOperation(String operationString, boolean operandIsPercentage) {
		switch (operationString) {
		case "+": case "-":
			if (operandIsPercentage) return Operation.ADD_PERCENTAGE;
			return Operation.ADD_NUMBER;
		case "*": case "/":
			return Operation.MULTIPLY_PERCENTAGE;
		
		default: return null;
		}
	}
	
	private static double parseOperand(String operandString, String operationString, boolean operandIsPercentage) {
		double result = Double.parseDouble(operandString);
		if (operandIsPercentage) result /= 100;
		
		switch (operationString) {
		case "+": return result;
		case "-": return -result;
		case "*": return result - 1;
		case "/": return (1/result) - 1;
		default: throw new IllegalStateException("Invalid operation string");
		}
	}

	public Attribute build() {
		return Attribute.newBuilder()
				.name("ResMagna attribute")
				.type(type)
				.slot(slot)
				.operation(operation)
				.amount(operand)
				.build();
	}
	
	public AttributeType getType()  { return type; }
	public Slot getSlot()           { return slot; }
	public Operation getOperation() { return operation; }
	public double getOperand()      { return operand; }
	
}
