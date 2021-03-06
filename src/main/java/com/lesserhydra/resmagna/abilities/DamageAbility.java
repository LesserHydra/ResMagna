package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

class DamageAbility implements Ability {
	
	private final double damageAmount;
	private final Targeter damagerTargeter;
	private final DamageCause cause;
	
	DamageAbility(ArgumentBlock args) {
		damageAmount = args.getDouble(true, 0D,             "amount", "a", null);
		damagerTargeter = args.getTargeter(false, Targeters.HOLDER, "damager", "dmgr", "source");
		cause = args.getEnum(false, DamageCause.CUSTOM,             "cause");
	}
	
	@Override
	public void run(Target target) {
		if (!target.isEntity()) return;
		LivingEntity targetEntity = target.asEntity();
		
		//Get damagerTarget
		Target damagerTarget = damagerTargeter.getRandomTarget(target);
		
		//Create and call event
		damageTarget(targetEntity, damagerTarget, damageAmount);
		/*EntityDamageEvent event = createDamageEvent(targetEntity, damagerEntity);
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		//If it wasn't canceled, damage the target
		if (!event.isCancelled()) {
			damageTarget(targetEntity, damagerEntity, event.getFinalDamage());
			targetEntity.setLastDamageCause(event);
		}*/
	}

	@SuppressWarnings("deprecation")
	private EntityDamageEvent createDamageEvent(LivingEntity damagee, Target damager) {
		if (damager.isEntity()) return new EntityDamageByEntityEvent(damager.asEntity(), damagee, cause, damageAmount);
		return new EntityDamageEvent(damagee, cause, damageAmount);
	}
	
	private void damageTarget(LivingEntity damagee, Target damager, double amount) {
		if (damager.isEntity()) damagee.damage(amount, damager.asEntity());
		else damagee.damage(amount);
	}
	
}
