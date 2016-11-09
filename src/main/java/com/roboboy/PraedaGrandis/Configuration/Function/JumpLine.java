package com.roboboy.PraedaGrandis.Configuration.Function;

class JumpLine extends FunctionLine implements Jump {
	
	@Override
	public void linkJump(GrandFunction jumpLine) { this.nextLine = jumpLine; }
	
}
