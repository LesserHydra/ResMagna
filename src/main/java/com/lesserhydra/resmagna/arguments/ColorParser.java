package com.lesserhydra.resmagna.arguments;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lesserhydra.resmagna.configuration.GroupingParser;
import org.bukkit.Color;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;

class ColorParser {
	
	//\s*(\d+)\s*[,;.\s]\s*(\d+)\s*[,;.\s]\s*(\d+)\s*
	static private final Pattern colorPattern = Pattern.compile("\\s*(\\d+)\\s*[,;.\\s]\\s*(\\d+)\\s*[,;.\\s]\\s*(\\d+)\\s*");
	
	public static Color build(String colorString) {
		//Remove groupings
		String withoutBrackets = GroupingParser.removeBrackets(colorString);
		
		//Improper format
		Matcher colorMatcher = colorPattern.matcher(withoutBrackets);
		if (!colorMatcher.matches()) {
			GrandLogger.log("Invalid rgb format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + colorString, LogType.CONFIG_ERRORS);
			return null;
		}
		
		int red = Integer.parseInt(colorMatcher.group(1));
		int green = Integer.parseInt(colorMatcher.group(2));
		int blue = Integer.parseInt(colorMatcher.group(3));
		
		return Color.fromRGB(red, green, blue);
	}

}
