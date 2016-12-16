package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class PotionCloudAbility implements Ability {
	
	private final PotionEffectType potType;
	private final Evaluators.ForInt potDuration;
	private final Evaluators.ForInt potAmplifier;
	private final Evaluators.ForBoolean potAmbient;
	private final Evaluators.ForBoolean potParticles;
	
	private final Evaluators.ForLocation centerLocation;
	
	private final Particle particle;
	private final Color color;
	
	private final Evaluators.ForInt duration;
	private final Evaluators.ForInt durationUseDecay;
	
	private final Evaluators.ForFloat radius;
	private final Evaluators.ForFloat radiusUseDecay;
	private final Evaluators.ForFloat radiusTickDecay;
	
	private final Evaluators.ForInt waitTime;
	private final Evaluators.ForInt immuneDelay;
	
	
	PotionCloudAbility(ArgumentBlock args) {
		this.potType = args.getPotionEffectType(false, null,
				"PotionType", "Potion", "PotType", "PotName");
		this.potDuration = args.getInteger(false, 600,			"potionduration", "potticks", "potdur");
		this.potAmplifier = args.getInteger(false, 0,			"potionamplifier", "potlevel", "potamp");
		this.potAmbient = args.getBoolean(false, false,		"potionisambient", "potionambient", "potamb");
		this.potParticles = args.getBoolean(false, false,	"potionshowparticles", "potionparticles", "potpart");
		
		this.centerLocation = args.getLocation(false, GrandLocation.CURRENT,	"location", "loc", "l");
		
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
		if (!evaluateParams(target)) return;
		
		AreaEffectCloud cloud = centerLocation.get().getWorld().spawn(centerLocation.get(), AreaEffectCloud.class);
		if (potType != null) {
			cloud.addCustomEffect(
					new PotionEffect(potType, potDuration.get(), potAmplifier.get(), potAmbient.get(), potParticles.get()),
					true);
		}
		
		if (particle != null) cloud.setParticle(particle);
		if (color != null) cloud.setColor(color);
		
		cloud.setDuration(duration.get());
		cloud.setDurationOnUse(durationUseDecay.get());
		
		cloud.setRadius(radius.get());
		cloud.setRadiusOnUse(radiusUseDecay.get());
		cloud.setRadiusPerTick(radiusTickDecay.get());
		
		cloud.setWaitTime(waitTime.get());
		cloud.setReapplicationDelay(immuneDelay.get());
	}
	
	private boolean evaluateParams(Target target) {
		return potDuration.evaluate(target)
				&& potAmplifier.evaluate(target)
				&& potAmbient.evaluate(target)
				&& potParticles.evaluate(target)
				&& centerLocation.evaluate(target)
				&& duration.evaluate(target)
				&& durationUseDecay.evaluate(target)
				&& radius.evaluate(target)
				&& radiusUseDecay.evaluate(target)
				&& radiusTickDecay.evaluate(target)
				&& waitTime.evaluate(target)
				&& immuneDelay.evaluate(target);
	}
	
}
