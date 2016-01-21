package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.AbilityArguments;

public class PushAbility extends Ability
{
	final private double forceAmount;
	
	public PushAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, AbilityArguments args) {
		super(slotType, activator, targeter);
		forceAmount = args.getDouble("force", 0D, true);
	}

	@Override
	protected void execute(Target target)
	{
		LivingEntity targetEntity = target.get();
		//Old velocity plus new force vector
		targetEntity.setVelocity(targetEntity.getVelocity().add(
				//Force vector pointing away from holder...
				targetEntity.getLocation().toVector().subtract(
						target.getHolder().getLocation().toVector())
				//...with length of forceAmount
				.normalize().multiply(forceAmount)));
	}

}
