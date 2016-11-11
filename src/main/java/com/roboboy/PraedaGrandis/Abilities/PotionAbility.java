package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
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
		//TODO: type = args.getPotionEffectType("name", PotionEffectType.ABSORPTION, true);
		this.type = PotionEffectType.getByName(args.getString(true, "ABSORPTION",	"PotionType", "Potion", "Type", "Name", "T", null));
		
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
