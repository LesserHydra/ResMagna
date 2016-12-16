package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.arguments.ProjectileType;
import com.lesserhydra.resmagna.nms.NMSEntity;
import com.lesserhydra.resmagna.targeters.Target;
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

//TODO: Use functors instead of function names
class ProjectileAbility implements Ability {
	
	private final ProjectileType projectileType;
	private final Evaluators.ForDouble velocity;
	private final Evaluators.ForDouble randomSpread;
	private final Evaluators.ForLocation targetLocation;
	private final Evaluators.ForString onHitName;
	private final Evaluators.ForString onEndName;
	private final Evaluators.ForBoolean gravity;
	private final Evaluators.ForBoolean flaming;
	private final Evaluators.ForBoolean bounce;
	
	private final Evaluators.ForFloat fireballYield;
	private final Evaluators.ForBoolean fireballFire;
	private final Evaluators.ForBoolean skullCharged;
	
	private final Evaluators.ForDouble arrowDamage;
	private final Evaluators.ForBoolean arrowCritical;
	private final Evaluators.ForInt arrowKnockback;
	private final Evaluators.ForBoolean arrowKeepHit;
	private final Evaluators.ForBoolean arrowKeepEnd;
	private final Evaluators.ForBoolean arrowRemove;
	private final Evaluators.ForBoolean arrowPickup;
	
	private final Evaluators.ForString onPotionSplash;
	
	private final Evaluators.ForEntity bulletTarget;
	
	ProjectileAbility(ArgumentBlock args) {
		this.projectileType = args.getEnum(true, ProjectileType.NONE,	"projectileType", "projectile", "type", "proj");
		
		this.velocity = args.getDouble(false, 1D,		"velocity", "vel", "v");
		this.randomSpread = args.getDouble(false, 0D,	"randomSpread", "spread");
		
		this.bounce = args.getBoolean(false, false,		"bounce");
		this.flaming = args.getBoolean(false, false,	"flaming");
		this.gravity = args.getBoolean(false, true,     "gravity");
		this.targetLocation = args.getLocation(false, GrandLocation.buildFromString("F+1"),	"targetLocation", "target");
		this.onHitName = args.getString(false, (String)null, 	"onHit", "hit");
		this.onEndName = args.getString(false, (String)null,	"onEnd", "end");
		
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
		
		this.onPotionSplash = args.getString(false, (String)null,	"onPotionSplash", "onPotSplash", "onSplash", "splash");
		
		this.bulletTarget = args.getEntity(false, Targeters.NONE, "shulkerBulletTarget", "bulletTarget");
	}

	@Override
	public void run(Target target) {
		if (!target.isEntity()) return;
		LivingEntity targetEntity = target.asEntity();
		
		if (!validateCommonParams(target)) return;
		
		Vector randomVector = new Vector(ResMagna.RANDOM_GENERATOR.nextDouble() - 0.5,
				ResMagna.RANDOM_GENERATOR.nextDouble() - 0.5, ResMagna.RANDOM_GENERATOR.nextDouble() - 0.5);
		randomVector.multiply(randomSpread.get());
		Vector projectileVelocity = calculateVelocity(targetLocation.get(), target.asLocation()).add(randomVector);
		
		Projectile projectile;
		if (projectileType == ProjectileType.SHULKER_BULLET) {
			projectile = (Projectile) targetEntity.getWorld().spawnEntity(targetEntity.getEyeLocation(), EntityType.SHULKER_BULLET);
			projectile.setShooter(targetEntity);
			
			if (bulletTarget.evaluate(target, false)) ((ShulkerBullet)projectile).setTarget(bulletTarget.get());
		}
		else projectile = targetEntity.launchProjectile(projectileType.getProjectileClass(), projectileVelocity);
		
		projectile.setGravity(gravity.get());
		projectile.setBounce(bounce.get());
		if (flaming.get()) projectile.setFireTicks(Integer.MAX_VALUE);
		projectile.setMetadata("PG_Projectile", new FixedMetadataValue(ResMagna.plugin, true));
		
		projectile.setMetadata(ResMagna.META_HOLDER, new FixedMetadataValue(ResMagna.plugin, target.getHolder()));
		if (onHitName.evaluate(target, false)) projectile.setMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnHit", new FixedMetadataValue(ResMagna.plugin, onHitName.get()));
		if (onEndName.evaluate(target, false)) projectile.setMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnEnd", new FixedMetadataValue(ResMagna.plugin, onEndName.get()));
		
		if (projectile instanceof Fireball && validateFireballParams(target)) setFireballProperties((Fireball) projectile);
		if (projectile instanceof Arrow && validateArrowParams(target)) setArrowProperties((Arrow) projectile);
		if (projectile instanceof ThrownPotion && validatePotionParams(target)) setPotionProperties((ThrownPotion) projectile);
	}
	
	private boolean validateCommonParams(Target target) {
		return velocity.evaluate(target)
				&& randomSpread.evaluate(target)
				&& targetLocation.evaluate(target)
				&& gravity.evaluate(target)
				&& flaming.evaluate(target)
				&& bounce.evaluate(target);
	}
	
	private boolean validateFireballParams(Target target) {
		return fireballYield.evaluate(target)
				&& fireballFire.evaluate(target)
				&& skullCharged.evaluate(target);
	}
	
	private boolean validateArrowParams(Target target) {
		return arrowDamage.evaluate(target)
				&& arrowCritical.evaluate(target)
				&& arrowKnockback.evaluate(target)
				&& arrowKeepHit.evaluate(target)
				&& arrowKeepEnd.evaluate(target)
				&& arrowRemove.evaluate(target)
				&& arrowPickup.evaluate(target);
	}
	
	private boolean validatePotionParams(Target target) {
		return onPotionSplash.evaluate(target);
	}
	
	private void setFireballProperties(Fireball fireball) {
		fireball.setYield(fireballYield.get());
		fireball.setIsIncendiary(fireballFire.get());
		if (fireball instanceof WitherSkull) ((WitherSkull) fireball).setCharged(skullCharged.get());
	}
	
	private void setArrowProperties(Arrow arrow) {
		arrow.spigot().setDamage(arrowDamage.get());
		arrow.setCritical(arrowCritical.get());
		arrow.setKnockbackStrength(arrowKnockback.get());
		NMSEntity.setArrowPickup(arrow, arrowPickup.get());
		if (arrowKeepHit.get()) arrow.setMetadata("PG_ArrowKeepHit", new FixedMetadataValue(ResMagna.plugin, true));
		if (arrowKeepEnd.get()) arrow.setMetadata("PG_ArrowKeepEnd", new FixedMetadataValue(ResMagna.plugin, true));
		if (arrowRemove.get()) arrow.setMetadata("PG_ArrowRemoveOnEnd", new FixedMetadataValue(ResMagna.plugin, true));
	}
	
	private void setPotionProperties(ThrownPotion potion) {
		potion.setMetadata(ResMagna.META_GRANDABILITY_PREFIX + "OnPotionSplash", new FixedMetadataValue(ResMagna.plugin, onPotionSplash.get()));
	}
	
	private Vector calculateVelocity(Location calculatedLocation, Location targetLocation) {
		//Force vector pointing to targetLocation...
		return calculatedLocation.toVector().subtract(targetLocation.toVector())
			//...with length of forceAmount
			.normalize().multiply(velocity.get());
	}

}
