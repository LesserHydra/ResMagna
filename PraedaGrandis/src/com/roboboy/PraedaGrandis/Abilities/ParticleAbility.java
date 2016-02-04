package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import com.darkblade12.particleeffect.ParticleEffect;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class ParticleAbility extends Ability
{
	private final ParticleEffect particleEffect;
	private final float offsetX;
	private final float offsetY;
	private final float offsetZ;
	private final float speed;
	private final int amount;
	private final GrandLocation centerLocation;
	private final double range;

	public ParticleAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args)
	{
		super(slotType, activator, targeter);
		
		//TODO: Verify name
		particleEffect = ParticleEffect.fromName(args.get("name", "", true));
		amount = args.getInteger("amount", 1, true);
		
		centerLocation = args.getLocation("location", new GrandLocation(), false);
		offsetX = args.getFloat("spreadx", 0F, false);
		offsetY = args.getFloat("spready", 0F, false);
		offsetZ = args.getFloat("spreadz", 0F, false);
		speed = args.getFloat("speed", 0F, false);
		range = args.getDouble("range", 60D, false);
	}

	@Override
	protected void execute(Target target) {
		Location calculatedLocation = centerLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		particleEffect.display(offsetX, offsetY, offsetZ, speed, amount, calculatedLocation, range);
	}

}
