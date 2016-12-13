package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class PotionAbility implements Ability.ForEntity {
	
	private final PotionEffectType type;
	private final int duration;
	private final int amplifier;
	private final boolean ambient;
	private final boolean particles;
	private final boolean force;
	
	PotionAbility(ArgumentBlock args) {
		this.type = args.getPotionEffectType(true, PotionEffectType.ABSORPTION, "PotionType", "Potion", "Type", "Name", "T", null);
		
		this.duration = args.getInteger(false, 600,		"Duration", "Ticks", "D");
		this.amplifier = args.getInteger(false, 0,		"Amplifier", "Level", "Amp", "A");
		this.ambient = args.getBoolean(false, true,		"IsAmbient", "Ambient", "Amb");
		this.particles = args.getBoolean(false, true,	"ShowParticles", "Particles", "Part");
		
		this.force = args.getBoolean(false, true,		"RemoveConflicting", "Force");
	}
	
	@Override
	public void run(LivingEntity target) {
		target.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles), force);
	}
	
}
