package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class HealAbility extends Ability
{
	final private double healAmount;
	
	public HealAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		healAmount = Double.parseDouble(args.get(1));
	}

	@Override
	protected void execute(Target target)
	{
		LivingEntity e = target.get();
		double newHealth = e.getHealth() + healAmount;
		
		if (newHealth <= e.getMaxHealth()) {
			e.setHealth(newHealth);
		}
	}

}
