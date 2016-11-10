package com.roboboy.PraedaGrandis.Configuration.Function;

import com.roboboy.PraedaGrandis.Conditions.Condition;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Targeters.Targeter;

class JumpIfLine extends FunctionLine implements Jump {
	
	private final Condition condition;
	private final Targeter targeter;
	private GrandFunction jumpLine = null;
	
	JumpIfLine(Condition condition, Targeter targeter) {
		this.condition = condition;
		this.targeter = targeter;
	}
	
	@Override
	public void run(Target target) {
		if (jumpLine != null && targeter.match(target, condition)) jumpLine.run(target);
		else nextLine.run(target);
	}
	
	@Override
	public void linkJump(GrandFunction jumpLine) { this.jumpLine = jumpLine; }
	
}
