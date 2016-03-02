package com.roboboy.PraedaGrandis.Configuration;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WitherSkull;

public enum ProjectileType
{
	NONE			(null),
	
	ARROW			(Arrow.class),
	EGG				(Egg.class),
	ENDERPEARL		(EnderPearl.class),
	LARGE_FIREBALL	(LargeFireball.class),
	SMALL_FIREBALL	(SmallFireball.class),
	SNOWBALL		(Snowball.class),
	EXP_BOTTLE		(ThrownExpBottle.class),
	POTION			(ThrownPotion.class),
	WITHER_SKULL	(WitherSkull.class);
	
	private Class<? extends Projectile> projectileClass;
	
	private ProjectileType(Class<? extends Projectile> projectileClass) {
		this.projectileClass = projectileClass;
	}
	
	public boolean isNull() {
		return (this == NONE);
	}
	
	public Class<? extends Projectile> getProjectileClass() {
		return projectileClass;
	}
	
	static public ProjectileType fromString(String projectileName) {
		for (ProjectileType type : values()) {
			if (type.toString().equalsIgnoreCase(projectileName)) return type;
		}
		return NONE;
	}
}
