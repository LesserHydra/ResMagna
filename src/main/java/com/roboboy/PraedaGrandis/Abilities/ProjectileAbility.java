package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;
import com.roboboy.PraedaGrandis.Arguments.ProjectileType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
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
	
	ProjectileAbility(ArgumentBlock args) {
		projectileType = args.getEnum(true, ProjectileType.NONE,	"projectiletype", "projectile", "type", "proj");
		
		velocity = args.getDouble(false, 1D,		"velocity", "vel", "v");
		randomSpread = args.getDouble(false, 0D,	"randomspread", "spread");
		
		bounce = args.getBoolean(false, false,		"bounce");
		flaming = args.getBoolean(false, false,		"flaming");
		targetLocation = args.getLocation(false, GrandLocation.buildFromString("F+1"),	"targetlocation", "target");
		onHitName = args.getString(false, null, 	"onhit", "hit");
		onEndName = args.getString(false, null,		"onend", "end");
		
		fireballYield = args.getFloat(false, 0F,		"fireballyield", "fireyield", "fbyield");
		fireballFire = args.getBoolean(false, false,	"fireballfire", "firefire", "fbfire");
		skullCharged = args.getBoolean(false, false,	"skullcharged", "skullcharge", "fbcharge");
		
		arrowDamage = args.getDouble(false, 0D,			"arrowdamage", "arrowd", "arrd");
		arrowCritical = args.getBoolean(false, false,	"arrowcritical", "arrowcrit", "arrcrit");
		arrowKnockback = args.getInteger(false, 0,		"arrowknockback", "arrowknock", "arrknock", "arrkb");
		arrowKeepHit = args.getBoolean(false, false,	"arrowkeephit", "arrowkhit", "arrkhit");
		arrowKeepEnd = args.getBoolean(false, false,	"arrowkeepend", "arrowkend", "arrkend");
		arrowRemove = args.getBoolean(false, false,		"arrowremoveonhit", "arrowremove", "arrowrem", "arrrem");
		arrowPickup = args.getBoolean(false, false,		"arrowcanbepickedup", "arrowpickup", "arrpickup", "arrpick");
		
		onPotionSplash = args.getString(false, null,	"onpotionsplash", "onpotsplash", "onsplash", "splash");
	}

	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		Location calculatedLocation = targetLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		Vector randomVector = new Vector(PraedaGrandis.RANDOM_GENERATOR.nextDouble() - 0.5,
				PraedaGrandis.RANDOM_GENERATOR.nextDouble() - 0.5,PraedaGrandis.RANDOM_GENERATOR.nextDouble() - 0.5);
		randomVector.multiply(randomSpread);
		Vector projectileVelocity = calculateVelocity(calculatedLocation, target.asLocation()).add(randomVector);
		
		Projectile projectile = targetEntity.launchProjectile(projectileType.getProjectileClass(), projectileVelocity);
		projectile.setBounce(bounce);
		if (flaming) projectile.setFireTicks(Integer.MAX_VALUE);
		projectile.setMetadata("PG_Projectile", new FixedMetadataValue(PraedaGrandis.plugin, true));
		
		projectile.setMetadata(PraedaGrandis.META_HOLDER, new FixedMetadataValue(PraedaGrandis.plugin, target.getHolder()));
		if (onHitName != null) projectile.setMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnHit", new FixedMetadataValue(PraedaGrandis.plugin, onHitName));
		if (onEndName != null) projectile.setMetadata(PraedaGrandis.META_GRANDABILITY_PREFIX + "OnEnd", new FixedMetadataValue(PraedaGrandis.plugin, onEndName));
		
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
