package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class PotionAbility implements Ability {
	
	private final PotionEffectType type;
	private final int duration;
	private final int amplifier;
	private final boolean ambient;
	private final boolean particles;
	private final boolean force;
	
	public PotionAbility(BlockArguments args) {
		//TODO: type = args.getPotionEffectType("name", PotionEffectType.ABSORPTION, true);
		type = PotionEffectType.getByName(args.getString(true, "ABSORPTION",	"potiontype", "potion", "type", "name", "t", null));
		
		duration = args.getInteger(false, 600,		"duration", "ticks", "d");
		amplifier = args.getInteger(false, 0,		"amplifier", "level", "amp", "a");
		ambient = args.getBoolean(false, true,		"isambient", "ambient", "amb");
		particles = args.getBoolean(false, true,	"showparticles", "particles", "part");
		
		force = args.getBoolean(false, true,		"removeconflicting", "force");
	}
	
	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		targetEntity.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles), force);
	}
	
}
