package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class PotionCloudAbility implements Ability {
	
	private final PotionEffect potion;
	private final GrandLocation centerLocation;
	
	private final Particle particle;
	private final Color color;
	
	private final int duration;
	private final int durationUseDecay;
	
	private final float radius;
	private final float radiusUseDecay;
	private final float radiusTickDecay;
	
	private final int waitTime;
	private final int immuneDelay;
	
	
	PotionCloudAbility(ArgumentBlock args) {
		//TODO: type = args.getPotionEffectType("name", PotionEffectType.ABSORPTION, true);
		PotionEffectType potType = PotionEffectType.getByName(args.getString(false, "",	"potiontype", "potion", "pottype", "potname"));
		int potDuration = args.getInteger(false, 600,			"potionduration", "potticks", "potdur");
		int potAmplifier = args.getInteger(false, 0,			"potionamplifier", "potlevel", "potamp");
		boolean potAmbient = args.getBoolean(false, false,		"potionisambient", "potionambient", "potamb");
		boolean potParticles = args.getBoolean(false, false,	"potionshowparticles", "potionparticles", "potpart");
		this.potion = (potType != null ? new PotionEffect(potType, potDuration, potAmplifier, potAmbient, potParticles) : null);
		
		this.centerLocation = args.getLocation(false, new GrandLocation(),	"location", "loc", "l");
		
		this.particle = args.getEnum(false, Particle.class,		"particletype", "particle", "part");
		this.color = args.getColor(false, null,		"particlecolor", "color");
		
		this.duration = args.getInteger(true, 0,			"duration", "dur", "ticks");
		this.durationUseDecay = args.getInteger(false, 0,	"durationusedecay", "durdecay");
		
		this.radius = args.getFloat(true, 0F,			"radius", "aoe", "rad", "r");
		this.radiusUseDecay = args.getFloat(false, 0F,	"radiususedecay", "raddecay", "aoedecay");
		this.radiusTickDecay = args.getFloat(false, 0F,	"radiustickdecay", "radtickdecay", "radtdecay", "aoetickdecay", "aoetdecay");
		
		this.waitTime = args.getInteger(false, 0,		"effectwaittime", "waittime", "wait");
		this.immuneDelay = args.getInteger(false, 0,	"effectimmunedelay", "immunedelay", "immdelay");
	}
	
	@Override
	public void run(Target target) {
		Location calculatedLocation = centerLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		AreaEffectCloud cloud = calculatedLocation.getWorld().spawn(calculatedLocation, AreaEffectCloud.class);
		if (potion != null) cloud.addCustomEffect(potion, true); 
		
		if (particle != null) cloud.setParticle(particle);
		if (color != null) cloud.setColor(color);
		
		cloud.setDuration(duration);
		cloud.setDurationOnUse(durationUseDecay);
		
		cloud.setRadius(radius);
		cloud.setRadiusOnUse(radiusUseDecay);
		cloud.setRadiusPerTick(radiusTickDecay);
		
		cloud.setWaitTime(waitTime);
		cloud.setReapplicationDelay(immuneDelay);
	}
	
}
