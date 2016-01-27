package com.roboboy.PraedaGrandis;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.GrandAbility;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class ProjectileListener implements Listener
{
	private final PraedaGrandis plugin;
	
	public ProjectileListener(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onProjectileHit(ProjectileHitEvent event) {
		plugin.logger.log("onHit", LogType.DEBUG);
		
		Projectile projectile = event.getEntity();
		GrandAbility onHitAbility = getGrandAbilityFromMeta(projectile, "OnHit");
		if (onHitAbility == null) return;
		plugin.logger.log("Got ability", LogType.DEBUG);
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		ArmorStand marker = projectile.getWorld().spawn(projectile.getLocation(), ArmorStand.class);
		marker.setMarker(true);
		marker.setVisible(false);
		
		onHitAbility.run(new Target(marker, holder, marker));
		
		marker.remove();
	}

	private GrandAbility getGrandAbilityFromMeta(Projectile entity, String key) {
		plugin.logger.log("Entity: " + entity.getType().toString(), LogType.DEBUG);
		
		MetadataValue gMeta = null;
		for (MetadataValue md : entity.getMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + key)) {
			plugin.logger.log("Found data", LogType.DEBUG);
			if (md.getOwningPlugin() == PraedaGrandis.plugin) {
				plugin.logger.log("Plugin is owner", LogType.DEBUG);
				gMeta = md;
				break;
			}
		}
		if (gMeta == null) return null;
		plugin.logger.log("Found: " + gMeta.asString(), LogType.DEBUG);
		return plugin.abilityHandler.customAbilities.get(gMeta.asString().toLowerCase());
	}
}
