package com.lesserhydra.resmagna.variables;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;

public enum VariableOperator {
	
	SET("=") {
		@Override public Value apply(Value a, Value b) {
			if (!b.hasNumber() && !b.hasLocation()) {
				GrandLogger.log("Tried to set global variable to a dependant type (ex. entity)", LogType.RUNTIME_ERRORS);
				return a;
			}
			return b;
		}
	},
	ADD("+=") {
		@Override public Value apply(Value a, Value b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to add non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.add(b);
		}
	},
	SUBTRACT("-=") {
		@Override public Value apply(Value a, Value b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to subtract non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.subtract(b);
		}
	},
	MULTIPLY("*=") {
		@Override public Value apply(Value a, Value b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to multiply non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.multiply(b);
		}
	},
	DIVIDE("/=") {
		@Override public Value apply(Value a, Value b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to divide non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.divide(b);
		}
	},
	MODULO("%=") {
		@Override public Value apply(Value a, Value b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to mod non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.modulus(b);
		}
	};
	
	final private String symbol;
	
	VariableOperator(String symbol) { this.symbol = symbol; }
	
	public abstract Value apply(Value a, Value b);

	public static VariableOperator fromSymbol(String symbol) {
		for (VariableOperator op : values()) {
			if (op.symbol.equals(symbol)) return op;
		}
		throw new IllegalArgumentException("Invalid variable operator: " + symbol);
	}
	
}
