package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.HolderTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class DamageAbility extends Ability
{
	private final double damageAmount;
	private final Targeter damagerTargeter;
	private final DamageCause cause;
	
	public DamageAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		damageAmount = args.getDouble("amount", 0D, true);
		damagerTargeter = args.getTargeter("damager", new HolderTargeter(), false);
		//TODO: Verify valid cause
		cause = DamageCause.valueOf(args.get("cause", "CUSTOM", false).toUpperCase());
	}
	
	@Override
	protected void execute(Target target) {
		//Get damagerTarget
		Target damagerTarget = damagerTargeter.getRandomTarget(target);
		if (damagerTarget == null) return;
		
		//Get damager & damagee
		LivingEntity targetEntity = target.getEntity();
		LivingEntity damagerEntity = damagerTarget.getEntity();
		
		//Create and call event
		EntityDamageEvent event = createDamageEvent(targetEntity, damagerEntity);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		//If it wasn't canceled, damage the target
		if (!event.isCancelled()) {
			damageTarget(targetEntity, damagerEntity, event.getFinalDamage());
			targetEntity.setLastDamageCause(event);
		}
	}
	
	@SuppressWarnings("deprecation")
	private EntityDamageEvent createDamageEvent(LivingEntity damagee, LivingEntity damager) {
		if (damager != null) return new EntityDamageByEntityEvent(damager, damagee, cause, damageAmount);
		return new EntityDamageEvent(damagee, cause, damageAmount);
	}
	
	private void damageTarget(LivingEntity damagee, LivingEntity damager, double amount) {
		if (damager != null) {
			damagee.damage(amount, damager);
			return;
		}
		damagee.damage(amount);
	}
}
