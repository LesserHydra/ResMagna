package com.roboboy.PraedaGrandis.Configuration;

public enum VariableConditional
{
	EQUAL("==") {
		@Override public boolean check(int a, int b) {return a == b;}
	},
	LESS("<") {
		@Override public boolean check(int a, int b) {return a < b;}
	},
	GREATER(">") {
		@Override public boolean check(int a, int b) {return a > b;}
	},
	LESS_EQUAL("<=") {
		@Override public boolean check(int a, int b) {return a <= b;}
	},
	GREATER_EQUAL(">=") {
		@Override public boolean check(int a, int b) {return a >= b;}
	};
	
	final private String symbol;
	
	private VariableConditional(String symbol) {
		this.symbol = symbol;
	}
	
	public abstract boolean check(int a, int b);

	public static VariableConditional fromSymbol(String symbol)
	{
		for (VariableConditional op : values()) {
			if (op.symbol.equals(symbol)) return op;
		}
		return null;
	}
}
