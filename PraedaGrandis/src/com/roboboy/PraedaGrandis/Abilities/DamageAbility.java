package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class DamageAbility extends Ability
{
	final private double damageAmount;
	
	public DamageAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		damageAmount = Double.parseDouble(args.get(1));
	}

	@Override
	protected void execute(Target target) {
		target.get().damage(damageAmount);
	}

}
