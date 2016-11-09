package com.roboboy.PraedaGrandis.Configuration.Function;

import com.roboboy.PraedaGrandis.Abilities.Conditions.Condition;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

class JumpIfLine extends FunctionLine implements Jump {
	
	private final Condition condition;
	private GrandFunction jumpLine = null;
	
	JumpIfLine(Condition condition) { this.condition = condition; }
	
	@Override
	public void run(Target target) {
		if (jumpLine != null && condition.check(target)) jumpLine.run(target);
		else nextLine.run(target);
	}
	
	@Override
	public void linkJump(GrandFunction jumpLine) { this.jumpLine = jumpLine; }
	
}
