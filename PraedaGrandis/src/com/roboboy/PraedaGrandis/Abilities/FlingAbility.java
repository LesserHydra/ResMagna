package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class FlingAbility extends Ability
{
	final private double forceAmount;
	final private GrandLocation targetLocation;

	public FlingAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		forceAmount = Double.parseDouble(args.get(1));
		targetLocation = new GrandLocation(args.get(2));
	}

	@Override
	protected void execute(Target target)
	{
		LivingEntity targetEntity = target.get();
		Location currentLocation = targetEntity.getLocation();
		//Old velocity plus new force vector
		targetEntity.setVelocity(targetEntity.getVelocity().add(
				//Force vector pointing to targetLocation...
				targetLocation.calculate(currentLocation).toVector().subtract(
						currentLocation.toVector())
				//...with length of forceAmount
				.normalize().multiply(forceAmount)));
	}

}
