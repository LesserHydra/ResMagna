package com.lesserhydra.praedagrandis.function;

class JumpLine extends FunctionLine implements Jump {
	
	@Override
	public void linkJump(Functor jumpLine) { this.nextLine = jumpLine; }
	
}
