package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

public class DamageAbility extends Ability
{
	final private double damageAmount;
	final private boolean fromHolder;
	final private DamageCause cause;
	
	public DamageAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		damageAmount = args.getDouble("amount", 0D, true);
		fromHolder = args.getBoolean("fromHolder", true, false);
		//TODO: Verify valid cause
		cause = DamageCause.valueOf(args.get("cause", "", true).toUpperCase());
	}
	
	@Override
	protected void execute(Target target) {
		EntityDamageEvent event = createDamageEvent(target);
		
		Bukkit.getServer().getPluginManager().callEvent(event);
		target.get().setLastDamageCause(event);
		
		if (!event.isCancelled()) damageTarget(target, event.getFinalDamage());
	}
	
	@SuppressWarnings("deprecation")
	private EntityDamageEvent createDamageEvent(Target target) {
		if (fromHolder) return new EntityDamageByEntityEvent(target.getHolder(), target.get(), cause, damageAmount);
		return new EntityDamageEvent(target.get(), cause, damageAmount);
	}
	
	private void damageTarget(Target target, double amount) {
		if (fromHolder) {
			target.get().damage(amount, target.getHolder());
			return;
		}
		target.get().damage(amount);
	}
}
