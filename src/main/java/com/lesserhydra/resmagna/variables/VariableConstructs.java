package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.VariableHandler;
import com.lesserhydra.util.StringTools;

public class VariableConstructs {
	public static final VariableConstruct NONE = p -> Variables.NONE;
	
	public static VariableConstruct construct(String string) {
		if (StringTools.isInteger(string)) return p -> Variables.wrap(Integer.parseInt(string));
		else if (StringTools.isFloat(string)) return p -> Variables.wrap(Double.parseDouble(string));
		else return VariableHandler.linkConstruct(string);
	}
}
