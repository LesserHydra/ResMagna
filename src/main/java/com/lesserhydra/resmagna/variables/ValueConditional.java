package com.lesserhydra.resmagna.variables;

public enum ValueConditional {
	
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
	
	ValueConditional(String symbol) { this.symbol = symbol; }
	
	public abstract <T extends Comparable<T>> boolean check(T a, T b);

	public static ValueConditional fromSymbol(String symbol) {
		for (ValueConditional op : values()) {
			if (op.symbol.equals(symbol)) return op;
		}
		throw new IllegalArgumentException("Invalid variable conditional: " + symbol);
	}
	
}
