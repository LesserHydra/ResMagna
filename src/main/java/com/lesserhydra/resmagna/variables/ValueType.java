package com.lesserhydra.resmagna.variables;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum ValueType {
	
	NONE,
	STRING,
	BOOLEAN         (STRING),
	NUMBER          (STRING),
	LOCATION        (STRING),
	ENTITY          (STRING, LOCATION),
	LIVING          (STRING, LOCATION, ENTITY),
	
	BLOCK_MASK      (STRING),
	BLOCK_PATTERN   (STRING),
	
	UNKNOWN         (STRING, BOOLEAN, NUMBER, LOCATION, ENTITY);
	
	
	private Set<ValueType> hasTypes;
	
	ValueType(ValueType... hasTypes) {
		this.hasTypes = new HashSet<>(Arrays.asList(hasTypes));
	}
	
	static {
		for (ValueType type : values()) {
			type.hasTypes.add(type);
			type.hasTypes = EnumSet.copyOf(type.hasTypes);
		}
	}
	
	public boolean has(ValueType type) {
		return hasTypes.contains(type);
	}
	
}
