package com.roboboy.PraedaGrandis;

import org.bukkit.entity.LivingEntity;
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

public class ProjectileListener implements Listener
{
	private final PraedaGrandis plugin;
	
	public ProjectileListener(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		GrandAbility onHitAbility = getGrandAbilityFromMeta(projectile, "OnHit");
		if (onHitAbility == null) return;
		
		Player holder = null;
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) holder = (Player) source;
		LivingEntity marker = MarkerBuilder.buildMarker(projectile.getLocation());
		onHitAbility.run(new Target(marker, holder, marker));
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
