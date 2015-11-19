package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.GrandAbility;

public class CustomAbility extends Ability
{
	final private GrandAbility grandAbility;
	
	public CustomAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args)
	{
		super(slotType, activator, targeter);
		//TODO: Error handling
		grandAbility = PraedaGrandis.plugin.abilityHandler.customAbilities.get(args.get(1));
		if (grandAbility == null) {
			PraedaGrandis.plugin.getLogger().severe("Grand ability not found: " + args.get(1));
			PraedaGrandis.plugin.getLogger().severe("Registered abilities:");
			for (String s : PraedaGrandis.plugin.abilityHandler.customAbilities.keySet()) {
				PraedaGrandis.plugin.getLogger().severe("    - " + s);
			}
		}
	}
	
	@Override
	protected void execute(Target target) {
		grandAbility.run(target);
	}

}
