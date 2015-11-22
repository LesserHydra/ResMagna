package com.roboboy.PraedaGrandis.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConfigString implements Iterable<String>
{
	private final String originalString;
	private final List<String> elements = new ArrayList<String>();
	
	public ConfigString(String s)
	{
		originalString = s;
		
		//Normalize
		s = s.toLowerCase().trim(); //"custom solidair"
		
		//Parse entire string into sections
		for (int i = 0, open = 0, startI = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			
			if (c == '(') { //Open bracket
				open++;
			}
			else if (open > 0) { //Searching for the close bracket
				if (c == ')') open--;
			}
			else if (c == ' ') { //Spaces otherwise
				if (startI != i) elements.add(s.substring(startI, i)); //Don't add blank spaces
				startI = i + 1;
			}
			
			if (i == s.length() - 1) { //Final character
				elements.add(s.substring(startI, s.length()));
			}
		}
	}
	
	public String get(int index) {
		return elements.get(index);
	}
	
	public String getOriginalString() {
		return originalString;
	}
	
	public int size() {
		return elements.size();
	}

	@Override
	public Iterator<String> iterator() {
		return elements.iterator();
	}
	
	/**
	 * Finds the first instance of an argument beginning with the given
	 * identifier string.
	 * 
	 * @param identifier Identifier string to look for
	 * @return Returns the index of the argument found, -1 if not found.
	 */
	public int contains(String identifier)
	{
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).startsWith(identifier)) return i;
		}
		
		return -1;
	}

	/*private String parseBrackets(String s, char openChar, char closeChar)
	{
		int open = 0;
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) == openChar) open++;
			if (s.charAt(i) == closeChar)
			{
				open--;
				if (open == 0) return s.substring(s.indexOf(openChar), i);
				if (open < 0) break;
			}
		}
		
		return null;
	}*/
	
}
