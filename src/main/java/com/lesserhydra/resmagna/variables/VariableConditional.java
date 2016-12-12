package com.lesserhydra.resmagna.variables;

public enum VariableConditional {
	
	EQUAL("==") {
		@Override public <T extends Comparable<T>> boolean check(T a, T b) {return a.compareTo(b) == 0;}
	},
	LESS("<") {
		@Override public <T extends Comparable<T>> boolean check(T a, T b) {return a.compareTo(b) < 0;}
	},
	GREATER(">") {
		@Override public <T extends Comparable<T>> boolean check(T a, T b) {return a.compareTo(b) > 0;}
	},
	LESS_EQUAL("<=") {
		@Override public <T extends Comparable<T>> boolean check(T a, T b) {return a.compareTo(b) <= 0;}
	},
	GREATER_EQUAL(">=") {
		@Override public <T extends Comparable<T>> boolean check(T a, T b) {return a.compareTo(b) >= 0;}
	};
	
	final private String symbol;
	
	VariableConditional(String symbol) { this.symbol = symbol; }
	
	public abstract <T extends Comparable<T>> boolean check(T a, T b);

	public static VariableConditional fromSymbol(String symbol) {
		for (VariableConditional op : values()) {
			if (op.symbol.equals(symbol)) return op;
		}
		throw new IllegalArgumentException("Invalid variable conditional: " + symbol);
	}
	
}
