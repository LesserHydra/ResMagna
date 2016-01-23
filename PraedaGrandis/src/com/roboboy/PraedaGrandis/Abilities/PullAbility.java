package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class PullAbility extends Ability
{
	final private double forceAmount;
	
	public PullAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		forceAmount = args.getDouble("force", 0D, true);
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
