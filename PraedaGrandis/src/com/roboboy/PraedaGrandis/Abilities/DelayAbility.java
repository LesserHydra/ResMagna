package com.roboboy.PraedaGrandis.Abilities;

import java.util.ArrayList;
import java.util.List;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;

public class DelayAbility extends Ability
{
	final private long delayAmount;
	final private List<Ability> delayedAbilities = new ArrayList<>();
	
	public DelayAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		delayAmount = Long.parseLong(args.get(1));
	}
	
	public void addAbility(Ability a) {
		delayedAbilities.add(a);
	}

	@Override
	protected void execute(final Target target) {
		PraedaGrandis.plugin.getServer().getScheduler().scheduleSyncDelayedTask(PraedaGrandis.plugin, new Runnable() {
			@Override
			public void run() {
				for (Ability a : delayedAbilities) {
					a.activate(ItemSlotType.ANY, target);
				}
			}
		}, delayAmount);
	}

}
