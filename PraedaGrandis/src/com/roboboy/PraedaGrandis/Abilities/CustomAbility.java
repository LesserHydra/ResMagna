package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.GrandAbility;

public class CustomAbility extends Ability
{
	final private String name;
	private GrandAbility grandAbility;
	
	public CustomAbility(String name, ItemSlotType slotType, ActivatorType activator, Targeter targeter)
	{
		super(slotType, activator, targeter);
		this.name = name;
		findGrandAbility(false);
	}
	
	public void findGrandAbility(boolean checkForFail) {
		grandAbility = PraedaGrandis.plugin.abilityHandler.customAbilities.get(name);
		if (grandAbility == null) {
			if (checkForFail) {
				PraedaGrandis.plugin.getLogger().severe("Grand ability not found: " + name);
				PraedaGrandis.plugin.getLogger().severe("Registered abilities:");
				for (String s : PraedaGrandis.plugin.abilityHandler.customAbilities.keySet()) {
					PraedaGrandis.plugin.getLogger().severe("    - " + s);
				}
			}
			else {
				PraedaGrandis.plugin.abilityHandler.toBeUpdated.add(this);
			}
		}
	}
	
	@Override
	protected void execute(Target target) {
		grandAbility.run(target);
	}

}
