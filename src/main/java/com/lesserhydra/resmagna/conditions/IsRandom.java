package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;

import java.util.Random;

class IsRandom implements Condition {
	
	private static final Random random = new Random();
	private final Evaluators.ForDouble chance;
	
	IsRandom(ArgumentBlock args) {
		chance = args.getDouble(true, 0,    "chance", "probability", "prob", "p", null);
	}
	
	@Override
	public boolean test(Target target) {
		if (!chance.evaluate(target)) return false;
		return random.nextDouble() < chance.get();
	}
	
}
