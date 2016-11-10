package com.roboboy.PraedaGrandis.Arguments;

public enum VariableOperator {
	
	SET("=") {
		@Override public int apply(int a, int b) {return b;}
	},
	ADD("+=") {
		@Override public int apply(int a, int b) {return a + b;}
	},
	SUBTRACT("-=") {
		@Override public int apply(int a, int b) {return a - b;}
	},
	MULTIPLY("*=") {
		@Override public int apply(int a, int b) {return a * b;}
	},
	DIVIDE("/=") {
		@Override public int apply(int a, int b) {return a / b;}
	},
	MODULO("%=") {
		@Override public int apply(int a, int b) {return a % b;}
	};
	
	final private String symbol;
	
	VariableOperator(String symbol) { this.symbol = symbol; }
	
	public abstract int apply(int a, int b);

	public static VariableOperator fromSymbol(String symbol) {
		for (VariableOperator op : values()) {
			if (op.symbol.equals(symbol)) return op;
		}
		throw new IllegalArgumentException("Invalid variable operator: " + symbol);
	}
	
}
