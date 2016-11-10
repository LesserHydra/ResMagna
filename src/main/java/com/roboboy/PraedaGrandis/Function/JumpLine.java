package com.roboboy.PraedaGrandis.Function;

class JumpLine extends FunctionLine implements Jump {
	
	@Override
	public void linkJump(Functor jumpLine) { this.nextLine = jumpLine; }
	
}
