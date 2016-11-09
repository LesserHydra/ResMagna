package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeters;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

class DamageAbility implements Ability {
	
	private final double damageAmount;
	private final Targeter damagerTargeter;
	private final DamageCause cause;
	
	DamageAbility(BlockArguments args) {
		damageAmount = args.getDouble(true, 0D,					        "amount", "a", null);
		damagerTargeter = args.getTargeter(false, Targeters.HOLDER,     "damager", "dmgr", "source");
		cause = args.getEnum(false, DamageCause.CUSTOM,				    "cause");
	}
	
	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		//Get damagerTarget
		Target damagerTarget = damagerTargeter.getRandomTarget(target);
		
		//Get damager
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
