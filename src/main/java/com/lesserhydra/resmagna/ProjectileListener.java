package com.lesserhydra.resmagna;

import com.lesserhydra.resmagna.configuration.GrandAbilityHandler;
import com.lesserhydra.resmagna.function.Functor;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;

class ProjectileListener implements Listener {
	
	private final ResMagna plugin;
	
	ProjectileListener(ResMagna plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileHitEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile)) return;
		Projectile projectile = (Projectile) event.getDamager();
		
		runHitAbility(projectile, event.getEntity());
		if (projectile.hasMetadata("PG_ArrowRemoveOnHit")) projectile.remove();
		
		if (!projectile.hasMetadata("PG_ArrowKeepHit")) projectile.removeMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnHit", plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileEnd(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		
		runEndAbility(projectile);
		if (projectile.hasMetadata("PG_ArrowRemoveOnEnd")) projectile.remove();
		
		if (!projectile.hasMetadata("PG_ArrowKeepEnd")) projectile.removeMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnEnd", plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion projectile = event.getPotion();
		Functor onSplashAbility = getGrandAbilityFromMeta(projectile, "OnPotionSplash");
		if (onSplashAbility == null) return;
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		
		for (LivingEntity hitEntity : event.getAffectedEntities()) {
			onSplashAbility.run(Target.make(holder, Target.from(hitEntity), Target.from(projectile.getLocation())));
		}
	}
	
	//Remove projectiles that have been unloaded
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileUnload(ChunkUnloadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {
			if (!(entity instanceof Projectile)) continue;
			if (entity.hasMetadata("PG_Projectile")) entity.remove();
		}
	}
	
	//Remove projectiles on plugin disable
	void removeAbilityProjectiles() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (!(entity instanceof Projectile)) continue;
				if (entity.hasMetadata("PG_Projectile")) entity.remove();
			}
		}
	}
	
	private void runHitAbility(Projectile projectile, Entity damagee) {
		if (!(damagee instanceof LivingEntity)) return;
		LivingEntity livingDamagee = (LivingEntity) damagee;
		
		Functor onHitAbility = getGrandAbilityFromMeta(projectile, "OnHit");
		if (onHitAbility == null) return;
		
		Player holder = getHolderFromMeta(projectile);
		
		LivingEntity sourceEntity = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof LivingEntity) sourceEntity = (LivingEntity) source;
		
		onHitAbility.run(Target.make(holder, Target.from(livingDamagee), Target.from(sourceEntity)));
	}
	
	private void runEndAbility(Projectile projectile) {
		Functor onEndAbility = getGrandAbilityFromMeta(projectile, "OnEnd");
		if (onEndAbility == null) return;
		
		Player holder = getHolderFromMeta(projectile);
		
		LivingEntity sourceEntity = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof LivingEntity) sourceEntity = (LivingEntity) source;
		
		onEndAbility.run(Target.make(holder, Target.from(projectile.getLocation()), Target.from(sourceEntity)));
	}

	private Functor getGrandAbilityFromMeta(Projectile entity, String key) {
		for (MetadataValue md : entity.getMetadata(ResMagna.META_GRANDABILITY_PREFIX + key)) {
			if (md.getOwningPlugin() == ResMagna.plugin) {
				return GrandAbilityHandler.getInstance().requestFunction(md.asString().toLowerCase());
			}
		}
		return null;
	}
	
	private Player getHolderFromMeta(Projectile projectile) {
		for (MetadataValue md : projectile.getMetadata(ResMagna.META_HOLDER)) {
			if (md.getOwningPlugin() == ResMagna.plugin) return (Player) md.value();
		}
		throw new IllegalStateException("Projectile did not have \"holder\" metadata");
	}
	
}
