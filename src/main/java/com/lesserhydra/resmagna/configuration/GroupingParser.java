package com.lesserhydra.resmagna.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;

public class GroupingParser
{
	//[\(\{\[](.*)[\)\}\]]
	private static final Pattern bracketedStringPattern = Pattern.compile("[\\(\\{\\[](.*)[\\)\\}\\]]");
	
	static private final List<Character> OPENING_CHARS = Arrays.asList('(', '{', '[');
	static private final List<Character> CLOSING_CHARS = Arrays.asList(')', '}', ']');
	
	private final String simplifiedString;
	private final List<String> groupingList = new ArrayList<String>();
	
	public GroupingParser(String string) {
		String result = "";
		int copyStartIndex = 0;
		for (int i = 0; i < string.length(); i++) {
			int bracketKind = OPENING_CHARS.indexOf(string.charAt(i));
			if (bracketKind < 0) continue;
			
			int closingIndex = findClosingChar(string, i, bracketKind);
			if (closingIndex == -1) {
				GrandLogger.log("Unclosed bracket at index " + i + ":", LogType.CONFIG_ERRORS);
				GrandLogger.log("  " + string, LogType.CONFIG_ERRORS);
				continue;
			}
			
			result = result.concat(string.substring(copyStartIndex, i)).concat("($" + groupingList.size() + ")");
			groupingList.add(string.substring(i+1, closingIndex));
			copyStartIndex = closingIndex + 1;
			i = closingIndex;
		}
		
		result = result.concat(string.substring(copyStartIndex, string.length()));
		simplifiedString = result;
	}
	
	private int findClosingChar(String string, int startIndex, int bracketKind) {
		char openingChar = OPENING_CHARS.get(bracketKind);
		char closingChar = CLOSING_CHARS.get(bracketKind);
		int open = 1;
		for (int i = startIndex+1; i < string.length(); i++) {
			if (string.charAt(i) == openingChar) open++;
			if (string.charAt(i) == closingChar) open--;
			if (open == 0) return i;
		}
		return -1;
	}
	
	public String getSimplifiedString() {
		return simplifiedString;
	}
	
	public String getGrouping(String identifier) {
		if (identifier == null || !identifier.startsWith("$")) return null;
		int index = Integer.parseInt(identifier.substring(1));
		return groupingList.get(index);
	}
	
	public String readdGrouping(String string, String identifier) {
		String grouping = getGrouping(identifier);
		if (grouping == null) return string;
		return string.replace(identifier, grouping);
	}
	
	public String readdAllGroupings(String string) {
		//TODO: Implement better
		if (string == null) return null;
		String result = string;
		for (int i = 0; i < groupingList.size(); ++i) {
			result = result.replace("$" + i, groupingList.get(i));
		}
		return result;
	}

	public static String removeBrackets(String originalString) {
		Matcher matcher = bracketedStringPattern.matcher(originalString);
		if (matcher.matches()) return matcher.group(1);
		return originalString;
	}
	
}
