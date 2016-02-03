package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.PraedaGrandis.Configuration.ProjectileType;

class ProjectileAbility extends Ability
{
	private final ProjectileType projectileType;
	private final double velocity;
	private final double randomSpread;
	private final GrandLocation targetLocation;
	private final String onHitName;
	private final String onEndName;
	private final boolean flaming;
	private final boolean bounce;
	
	private final float fireballYield;
	private final boolean fireballFire;
	private final boolean skullCharged;
	
	private final double arrowDamage;
	private final boolean arrowCritical;
	private final int arrowKnockback;
	private final boolean arrowKeepHit;
	private final boolean arrowKeepEnd;
	private final boolean arrowRemove;
	private final boolean arrowPickup;
	
	private final String onPotionSplash;
	
	public ProjectileAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		projectileType = ProjectileType.fromString(args.get("type", "", true));
		velocity = args.getDouble("velocity", 1D, false);
		randomSpread = args.getDouble("randomspread", 0D, false);
		
		bounce = args.getBoolean("bounce", false, false);
		flaming = args.getBoolean("flaming", false, false);
		targetLocation = args.getLocation("targetlocation", new GrandLocation("F+1"), false);
		onHitName = args.get("onhit", null, false);
		onEndName = args.get("onend", null, false);
		
		fireballYield = args.getFloat("fireballyield", 0F, false);
		fireballFire = args.getBoolean("fireballfire", false, false);
		skullCharged = args.getBoolean("skullcharged", false, false);
		
		arrowDamage = args.getDouble("arrowdamage", 0D, false);
		arrowCritical = args.getBoolean("arrowcritical", false, false);
		arrowKnockback = args.getInteger("arrowknockback", 0, false);
		arrowKeepHit = args.getBoolean("arrowkeephit", false, false);
		arrowKeepEnd = args.getBoolean("arrowkeepend", false, false);
		arrowRemove = args.getBoolean("arrowremove", false, false);
		arrowPickup = args.getBoolean("arrowpickup", false, false);
		
		onPotionSplash = args.get("onsplash", null, false);
	}

	@Override
	protected void execute(Target target) {
		Location calculatedLocation = targetLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		Vector randomVector = new Vector(PraedaGrandis.RANDOM_GENERATOR.nextDouble() - 0.5,
				PraedaGrandis.RANDOM_GENERATOR.nextDouble() - 0.5,PraedaGrandis.RANDOM_GENERATOR.nextDouble() - 0.5);
		randomVector.multiply(randomSpread);
		Vector projectileVelocity = calculateVelocity(calculatedLocation, target.get().getLocation()).add(randomVector);
		
		Projectile projectile = target.get().launchProjectile(projectileType.getProjectileClass(), projectileVelocity);
		projectile.setBounce(bounce);
		if (flaming) projectile.setFireTicks(Integer.MAX_VALUE);
		projectile.setMetadata("PG_Projectile", new FixedMetadataValue(PraedaGrandis.plugin, true));
		
		projectile.setMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit", new FixedMetadataValue(PraedaGrandis.plugin, onHitName));
		projectile.setMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnEnd", new FixedMetadataValue(PraedaGrandis.plugin, onEndName));
		
		setFireballProperties(projectile);
		setArrowProperties(projectile);
		setPotionProperties(projectile);
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
		if (arrowKeepHit) arrow.setMetadata("PG_ArrowKeepHit", new FixedMetadataValue(PraedaGrandis.plugin, true));
		if (arrowKeepEnd) arrow.setMetadata("PG_ArrowKeepEnd", new FixedMetadataValue(PraedaGrandis.plugin, true));
		if (arrowRemove) arrow.setMetadata("PG_ArrowRemoveOnEnd", new FixedMetadataValue(PraedaGrandis.plugin, true));
		if (!arrowPickup) arrow.setMetadata("PG_ArrowStopPickup", new FixedMetadataValue(PraedaGrandis.plugin, true));
	}
	
	private void setPotionProperties(Projectile projectile) {
		if (!(projectile instanceof ThrownPotion)) return;
		projectile.setMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnPotionSplash", new FixedMetadataValue(PraedaGrandis.plugin, onPotionSplash));
	}

	private Vector calculateVelocity(Location calculatedLocation, Location targetLocation) {
		//Force vector pointing to targetLocation...
		return calculatedLocation.toVector().subtract(targetLocation.toVector())
			//...with length of forceAmount
			.normalize().multiply(velocity);
	}

}
