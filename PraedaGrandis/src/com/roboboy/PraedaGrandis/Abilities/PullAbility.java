package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class PullAbility extends Ability
{
	final private double forceAmount;
	
	public PullAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		forceAmount = Double.parseDouble(args.get(1));
	}

	@Override
	protected void execute(Target target)
	{
		LivingEntity targetEntity = target.get();
		//Old velocity plus new force vector
		targetEntity.setVelocity(targetEntity.getVelocity().add(
				//Force vector pointing to holder...
				target.getHolder().getLocation().toVector().subtract(
						targetEntity.getLocation().toVector())
				//...with length of forceAmount
				.normalize().multiply(forceAmount)));
	}

}
