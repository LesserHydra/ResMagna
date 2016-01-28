package com.roboboy.PraedaGrandis;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.GrandAbility;

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
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		
		if (projectile.hasMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit")) runGrandAbility(projectile);
		if (projectile.hasMetadata("PG_ArrowRemoveOnHit")) projectile.remove();
	}

	private void runGrandAbility(Projectile projectile)
	{
		GrandAbility onHitAbility = getGrandAbilityFromMeta(projectile, "OnHit");
		if (onHitAbility == null) return;
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		LivingEntity marker = MarkerBuilder.buildMarker(projectile.getLocation());
		onHitAbility.run(new Target(marker, holder, marker));
		
		if (!projectile.hasMetadata("PG_ArrowKeepHitAbility")) projectile.removeMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit", plugin);
	}

	private GrandAbility getGrandAbilityFromMeta(Projectile entity, String key) {
		MetadataValue gMeta = null;
		for (MetadataValue md : entity.getMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + key)) {
			if (md.getOwningPlugin() == PraedaGrandis.plugin) {
				gMeta = md;
				break;
			}
		}
		if (gMeta == null) return null;
		return plugin.abilityHandler.customAbilities.get(gMeta.asString().toLowerCase());
	}
}
