package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class PotionAbility implements Ability {
	
	private final PotionEffectType type;
	private final Evaluators.ForInt duration;
	private final Evaluators.ForInt amplifier;
	private final Evaluators.ForBoolean ambient;
	private final Evaluators.ForBoolean particles;
	private final Evaluators.ForBoolean force;
	
	PotionAbility(ArgumentBlock args) {
		this.type = args.getPotionEffectType(true, PotionEffectType.ABSORPTION, "PotionType", "Potion", "Type", "Name", "T", null);
		
		this.duration = args.getInteger(false, 600,		"Duration", "Ticks", "D");
		this.amplifier = args.getInteger(false, 0,		"Amplifier", "Level", "Amp", "A");
		this.ambient = args.getBoolean(false, true,		"IsAmbient", "Ambient", "Amb");
		this.particles = args.getBoolean(false, true,	"ShowParticles", "Particles", "Part");
		
		this.force = args.getBoolean(false, true,		"RemoveConflicting", "Force");
	}
	
	@Override
	public void run(Target target) {
		//Verify target
		if (!target.isEntity()) return;
		
		//Verify parameters
		if (!(duration.evaluate(target)
				&& amplifier.evaluate(target)
				&& ambient.evaluate(target)
				&& particles.evaluate(target)
				&& force.evaluate(target))) return;
		
		target.asEntity().addPotionEffect(
				new PotionEffect(type, duration.get(), amplifier.get(), ambient.get(), particles.get()),
				force.get());
	}
	
}
