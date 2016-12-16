package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;

class SoundAbility implements Ability {
	
	private final Evaluators.ForString sound;
	private final Evaluators.ForFloat volume;
	private final Evaluators.ForFloat pitch;

	SoundAbility(ArgumentBlock args) {
		sound = args.getString(true, "",	"soundname", "sound", "name", "s", "n", null);
		volume = args.getFloat(false, 1F,	"volume", "v");
		pitch = args.getFloat(false, 1F,	"pitch", "p");
	}

	@Override
	public void run(Target target) {
		if (!target.isLocation()) return;
		
		if (!(sound.evaluate(target)
				&& volume.evaluate(target)
				&& pitch.evaluate(target)))
			
		target.asLocation().getWorld().playSound(target.asLocation(), sound.get(), volume.get(), pitch.get());
	}

}
