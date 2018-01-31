package com.lesserhydra.resmagna.conditions;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;

import java.util.Random;

class IsRandom implements Condition {
	
	private static final Random random = new Random();
	private final double chance;
	
	IsRandom(ArgumentBlock args) {
		chance = args.getDouble(true, 0,    "chance", "probability", "prob", "p", null);
	}
	
	@Override
	public boolean test(Target target) { return random.nextDouble() < chance; }
	
}
