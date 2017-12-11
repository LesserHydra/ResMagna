package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.arguments.ProjectileType;
import com.lesserhydra.resmagna.nms.NMSEntity;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.targeters.Targeter;
import com.lesserhydra.resmagna.targeters.Targeters;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

class ProjectileAbility implements Ability {
	
	private final ProjectileType projectileType;
	private final double velocity;
	private final double randomSpread;
	private final GrandLocation targetLocation;
	private final String onHitName;
	private final String onEndName;
	private final boolean gravity;
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
	
	private final Targeter shulkerBulletTargeter;
	
	ProjectileAbility(ArgumentBlock args) {
		this.projectileType = args.getEnum(true, ProjectileType.NONE,	"projectileType", "projectile", "type", "proj");
		
		this.velocity = args.getDouble(false, 1D,		"velocity", "vel", "v");
		this.randomSpread = args.getDouble(false, 0D,	"randomSpread", "spread");
		
		this.bounce = args.getBoolean(false, false,		"bounce");
		this.flaming = args.getBoolean(false, false,	"flaming");
		this.gravity = args.getBoolean(false, true,     "gravity");
		this.targetLocation = args.getLocation(false, GrandLocation.buildFromString("F+1"),	"targetLocation", "target");
		this.onHitName = args.getString(false, null, 	"onHit", "hit");
		this.onEndName = args.getString(false, null,	"onEnd", "end");
		
		this.fireballYield = args.getFloat(false, 0F,		"fireballYield", "fireYield", "fbYield");
		this.fireballFire = args.getBoolean(false, false,	"fireballFire", "fireFire", "fbFire");
		this.skullCharged = args.getBoolean(false, false,	"skullCharged", "skullCharge", "fbCharge");
		
		this.arrowDamage = args.getDouble(false, 0D,		"arrowDamage", "arrowD", "arrD");
		this.arrowCritical = args.getBoolean(false, false,	"arrowCritical", "arrowCrit", "arrCrit");
		this.arrowKnockback = args.getInteger(false, 0,		"arrowKnockback", "arrowKnock", "arrKnock", "arrKb");
		this.arrowKeepHit = args.getBoolean(false, false,	"arrowKeepHit", "arrowKHit", "arrkHit");
		this.arrowKeepEnd = args.getBoolean(false, false,	"arrowKeepEnd", "arrowKEnd", "arrKEnd");
		this.arrowRemove = args.getBoolean(false, false,	"arrowRemoveOnHit", "arrowRemove", "arrowRem", "arrRem");
		this.arrowPickup = args.getBoolean(false, false,	"arrowCanBePickedUp", "arrowPickup", "arrPickup", "arrPick");
		
		this.onPotionSplash = args.getString(false, null,	"onPotionSplash", "onPotSplash", "onSplash", "splash");
		
		this.shulkerBulletTargeter = args.getTargeter(false, Targeters.NONE, "shulkerBulletTarget", "bulletTarget");
	}

	@Override
	public void run(Target target) {
		if (!target.isEntity()) return;
		LivingEntity targetEntity = target.asEntity();
		
		Location calculatedLocation = targetLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		Vector randomVector = new Vector(ResMagna.RANDOM_GENERATOR.nextDouble() - 0.5,
				ResMagna.RANDOM_GENERATOR.nextDouble() - 0.5, ResMagna.RANDOM_GENERATOR.nextDouble() - 0.5);
		randomVector.multiply(randomSpread);
		Vector projectileVelocity = calculateVelocity(calculatedLocation, target.asLocation()).add(randomVector);
		
		Projectile projectile;
		if (projectileType == ProjectileType.SHULKER_BULLET) {
			projectile = (Projectile) targetEntity.getWorld().spawnEntity(targetEntity.getEyeLocation(), EntityType.SHULKER_BULLET);
			projectile.setShooter(targetEntity);
			projectile.setVelocity(projectileVelocity);
			
			Target bulletTarget = shulkerBulletTargeter.getRandomTarget(target);
			if (bulletTarget.isEntity()) ((ShulkerBullet)projectile).setTarget(bulletTarget.asEntity());
		}
		else projectile = targetEntity.launchProjectile(projectileType.getProjectileClass(), projectileVelocity);
		
		projectile.setGravity(gravity);
		projectile.setBounce(bounce);
		if (flaming) projectile.setFireTicks(Integer.MAX_VALUE);
		projectile.setMetadata("PG_Projectile", new FixedMetadataValue(ResMagna.plugin, true));
		
		projectile.setMetadata(ResMagna.META_HOLDER, new FixedMetadataValue(ResMagna.plugin, target.getHolder()));
		if (onHitName != null) projectile.setMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnHit", new FixedMetadataValue(ResMagna.plugin, onHitName));
		if (onEndName != null) projectile.setMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnEnd", new FixedMetadataValue(ResMagna.plugin, onEndName));
		
		if (projectile instanceof Fireball) setFireballProperties((Fireball) projectile);
		if (projectile instanceof Arrow) setArrowProperties((Arrow) projectile);
		if (projectile instanceof ThrownPotion) setPotionProperties((ThrownPotion) projectile);
	}

	private void setFireballProperties(Fireball fireball) {
		fireball.setYield(fireballYield);
		fireball.setIsIncendiary(fireballFire);
		if (fireball instanceof WitherSkull) ((WitherSkull) fireball).setCharged(skullCharged);
	}
	
	private void setArrowProperties(Arrow arrow) {
		arrow.spigot().setDamage(arrowDamage);
		arrow.setCritical(arrowCritical);
		arrow.setKnockbackStrength(arrowKnockback);
		NMSEntity.setArrowPickup(arrow, arrowPickup);
		if (arrowKeepHit) arrow.setMetadata("PG_ArrowKeepHit", new FixedMetadataValue(ResMagna.plugin, true));
		if (arrowKeepEnd) arrow.setMetadata("PG_ArrowKeepEnd", new FixedMetadataValue(ResMagna.plugin, true));
		if (arrowRemove) arrow.setMetadata("PG_ArrowRemoveOnEnd", new FixedMetadataValue(ResMagna.plugin, true));
	}
	
	private void setPotionProperties(ThrownPotion potion) {
		potion.setMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnPotionSplash", new FixedMetadataValue(ResMagna.plugin, onPotionSplash));
	}
	
	private Vector calculateVelocity(Location calculatedLocation, Location targetLocation) {
		//Force vector pointing to targetLocation...
		return calculatedLocation.toVector().subtract(targetLocation.toVector())
			//...with length of forceAmount
			.normalize().multiply(velocity);
	}

}
