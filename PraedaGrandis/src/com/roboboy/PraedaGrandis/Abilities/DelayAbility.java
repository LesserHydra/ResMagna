package com.roboboy.PraedaGrandis.Abilities;

import java.util.ArrayList;
import java.util.List;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class DelayAbility extends Ability
{
	private final long delayAmount;
	private final List<Ability> delayedAbilities = new ArrayList<>();
	
	public DelayAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		delayAmount = args.getLong(0L, true,	"delayamount", "delay", "ticks", "t", null);
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
					a.activate(target);
				}
			}
		}, delayAmount);
	}

}
