package com.lesserhydra.resmagna.function;

import com.lesserhydra.resmagna.conditions.Condition;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;

class JumpIfLine extends FunctionLine implements Jump {
	
	private final Condition condition;
	private final Targeter targeter;
	private Functor jumpLine = null;
	
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
	public void linkJump(Functor jumpLine) { this.jumpLine = jumpLine; }
	
}
