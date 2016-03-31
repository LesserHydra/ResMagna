package com.roboboy.PraedaGrandis;

import java.util.EnumSet;
import java.util.regex.Pattern;

public class Tools {
	private static Pattern enumSeperaterPattern = Pattern.compile("[_\\s]");

	public static <T extends Enum<T>> T parseEnum(String lookupName, Class<T> enumClass) {
		//Find enum from value
		lookupName = enumSeperaterPattern.matcher(lookupName.toUpperCase()).replaceAll("");
		for (T type : EnumSet.allOf(enumClass)) {
			String enumName = enumSeperaterPattern.matcher(type.name()).replaceAll("");
			String enumString = enumSeperaterPattern.matcher(type.toString().toUpperCase()).replaceAll("");
			if (lookupName.equals(enumName) || lookupName.equals(enumString)) return type;
		}
		//Not found
		return null;
	}
	
}
