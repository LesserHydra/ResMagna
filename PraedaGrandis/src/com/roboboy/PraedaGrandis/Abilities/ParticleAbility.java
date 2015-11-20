package com.roboboy.PraedaGrandis.Abilities;

import com.darkblade12.particleeffect.ParticleEffect;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class ParticleAbility extends Ability
{
	final private ParticleEffect particleEffect;
	final private float offsetX;
	final private float offsetY;
	final private float offsetZ;
	final private float speed;
	final private int amount;
	final private GrandLocation centerLocation;
	final private double range;

	public ParticleAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args)
	{
		super(slotType, activator, targeter);
		
		particleEffect = ParticleEffect.fromName(args.get(1));
		offsetX = Float.parseFloat(args.get(2));
		offsetY = Float.parseFloat(args.get(3));
		offsetZ = Float.parseFloat(args.get(4));
		speed = Float.parseFloat(args.get(5));
		amount = Integer.parseInt(args.get(6));
		centerLocation = new GrandLocation(args.get(7));
		range = Double.parseDouble(args.get(8));
	}

	@Override
	protected void execute(Target target)
	{
		particleEffect.display(offsetX, offsetY, offsetZ, speed, amount, centerLocation.calculate(target.get().getLocation()), range);
	}

}
