package com.lesserhydra.resmagna.arguments;

import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.variables.Variable;

public enum VariableOperator {
	
	SET("=") {
		@Override public Variable apply(Variable a, Variable b) {
			if (!b.hasNumber() && !b.hasLocation()) {
				GrandLogger.log("Tried to set global variable to a dependant type (ex. entity)", LogType.RUNTIME_ERRORS);
				return a;
			}
			return b;
		}
	},
	ADD("+=") {
		@Override public Variable apply(Variable a, Variable b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to add non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.add(b);
		}
	},
	SUBTRACT("-=") {
		@Override public Variable apply(Variable a, Variable b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to subtract non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.subtract(b);
		}
	},
	MULTIPLY("*=") {
		@Override public Variable apply(Variable a, Variable b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to multiply non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.multiply(b);
		}
	},
	DIVIDE("/=") {
		@Override public Variable apply(Variable a, Variable b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to divide non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.divide(b);
		}
	},
	MODULO("%=") {
		@Override public Variable apply(Variable a, Variable b) {
			if (!a.hasNumber() || !b.hasNumber()) {
				GrandLogger.log("Tried to mod non-numerical values.", LogType.RUNTIME_ERRORS);
				return a;
			}
			return a.modulus(b);
		}
	};
	
	final private String symbol;
	
	VariableOperator(String symbol) { this.symbol = symbol; }
	
	public abstract Variable apply(Variable a, Variable b);

	public static VariableOperator fromSymbol(String symbol) {
		for (VariableOperator op : values()) {
			if (op.symbol.equals(symbol)) return op;
		}
		throw new IllegalArgumentException("Invalid variable operator: " + symbol);
	}
	
}
