package com.roboboy.PraedaGrandis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargetEntity;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargetLocation;
import com.roboboy.PraedaGrandis.Configuration.FunctionRunner;

public class ProjectileListener implements Listener
{
	private final PraedaGrandis plugin;
	
	public ProjectileListener(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onArrowPickup(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (item.getItemStack().getType() != Material.ARROW) return;
		if (item.hasMetadata("PG_ArrowStopPickup")) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileHitEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Projectile)) return;
		Projectile projectile = (Projectile) event.getDamager();
		
		if (projectile.hasMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit")) runHitAbility(projectile, event.getEntity());
		if (projectile.hasMetadata("PG_ArrowRemoveOnHit")) projectile.remove();
		
		if (!projectile.hasMetadata("PG_ArrowKeepHit")) projectile.removeMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit", plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onProjectileEnd(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		
		if (projectile.hasMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnEnd")) runEndAbility(projectile);
		if (projectile.hasMetadata("PG_ArrowRemoveOnEnd")) projectile.remove();
		
		if (!projectile.hasMetadata("PG_ArrowKeepEnd")) projectile.removeMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnEnd", plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion projectile = event.getPotion();
		FunctionRunner onSplashAbility = getGrandAbilityFromMeta(projectile, "OnPotionSplash");
		if (onSplashAbility == null) return;
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		
		for (LivingEntity hitEntity : event.getAffectedEntities()) {
			onSplashAbility.run(new Target(new TargetEntity(hitEntity), holder, new TargetLocation(projectile.getLocation())));
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
	public void removeAbilityProjectiles() {
		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (!(entity instanceof Projectile)) continue;
				if (entity.hasMetadata("PG_Projectile")) entity.remove();
			}
		}
	}
	
	private void runHitAbility(Projectile projectile, Entity damagee)
	{
		if (!(damagee instanceof LivingEntity)) return;
		LivingEntity livingDamagee = (LivingEntity) damagee;
		
		FunctionRunner onHitAbility = getGrandAbilityFromMeta(projectile, "OnHit");
		if (onHitAbility == null) return;
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		onHitAbility.run(new Target(new TargetEntity(livingDamagee), holder, new TargetEntity(livingDamagee)));
	}
	
	private void runEndAbility(Projectile projectile)
	{
		FunctionRunner onEndAbility = getGrandAbilityFromMeta(projectile, "OnEnd");
		if (onEndAbility == null) return;
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		onEndAbility.run(new Target(new TargetLocation(projectile.getLocation()), holder, new TargetLocation(projectile.getLocation())));
	}

	private FunctionRunner getGrandAbilityFromMeta(Projectile entity, String key) {
		MetadataValue gMeta = null;
		for (MetadataValue md : entity.getMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + key)) {
			if (md.getOwningPlugin() == PraedaGrandis.plugin) {
				gMeta = md;
				break;
			}
		}
		if (gMeta == null) return null;
		return new FunctionRunner(gMeta.asString().toLowerCase());
	}
}
