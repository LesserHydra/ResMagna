package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.PraedaGrandis.Configuration.ProjectileType;

public class ProjectileAbility extends Ability
{
	private final ProjectileType projectileType;
	private final double velocity;
	private final GrandLocation targetLocation;
	private final String onHitGrandAbilityName;
	private final boolean bounce;
	
	private final float fireballYield;
	private final boolean fireballFire;
	private final boolean skullCharged;
	
	private final double arrowDamage;
	private final boolean arrowCritical;
	private final int arrowKnockback;
	
	public ProjectileAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		projectileType = ProjectileType.fromString(args.get("type", "", true));
		velocity = args.getDouble("velocity", 1D, false);
		bounce = args.getBoolean("bounce", false, false);
		targetLocation = args.getLocation("targetlocation", null, false);
		onHitGrandAbilityName = args.get("onhit", null, false);
		
		fireballYield = args.getFloat("fireballyield", 0F, false);
		fireballFire = args.getBoolean("fireballfire", false, false);
		skullCharged = args.getBoolean("skullcharged", false, false);
		
		arrowDamage = args.getDouble("arrowdamage", 0D, false);
		arrowCritical = args.getBoolean("arrowcritical", false, false);
		arrowKnockback = args.getInteger("arrowknockback", 0, false);
	}

	@Override
	protected void execute(Target target) {
		Projectile projectile = target.get().launchProjectile(projectileType.getProjectileClass(), calculateVelocity(target.get().getLocation()));
		projectile.setBounce(bounce);
		//setProjectileVelocity(projectile, calculateVelocity(target.get().getLocation()));
		
		projectile.setMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit", new FixedMetadataValue(PraedaGrandis.plugin, onHitGrandAbilityName));
		
		setFireballProperties(projectile);
		setArrowProperties(projectile);
	}

	private void setFireballProperties(Projectile projectile) {
		if (!(projectile instanceof Fireball)) return;
		Fireball fireball = (Fireball) projectile;
		fireball.setYield(fireballYield);
		fireball.setIsIncendiary(fireballFire);
		if (fireball instanceof WitherSkull) ((WitherSkull) fireball).setCharged(skullCharged);
	}
	
	private void setArrowProperties(Projectile projectile) {
		if (!(projectile instanceof Arrow)) return;
		Arrow arrow = (Arrow) projectile;
		arrow.spigot().setDamage(arrowDamage);
		arrow.setCritical(arrowCritical);
		arrow.setKnockbackStrength(arrowKnockback);
	}

	private Vector calculateVelocity(Location currentLocation) {
		if (targetLocation != null) {
			//Force vector pointing to targetLocation...
			return targetLocation.calculate(currentLocation).toVector().subtract(currentLocation.toVector())
				//...with length of forceAmount
				.normalize().multiply(velocity);
		}
		
		//Entity facing direction...
		return currentLocation.getDirection()
			//...with length of forceAmount
			.multiply(velocity);
	}
	
	/*private void setProjectileVelocity(Projectile projectile, Vector velocityVector){
		//Fireball type projectiles use direction instead of velocity
		if (projectile instanceof Fireball) {
			((Fireball) projectile).setDirection(velocityVector);
			return;
		}
		//All other projectiles
		projectile.setVelocity(velocityVector);
	}*/

}
