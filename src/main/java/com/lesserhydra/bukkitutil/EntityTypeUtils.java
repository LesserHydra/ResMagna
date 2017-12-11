package com.lesserhydra.bukkitutil;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class EntityTypeUtils {
	
	private static Set<EntityType> undeadTypes = SetBuilder.init(() -> EnumSet.noneOf(EntityType.class))
			.add(EntityType.ZOMBIE)
			.add(EntityType.ZOMBIE_VILLAGER)
			.add(EntityType.HUSK)
			.add(EntityType.PIG_ZOMBIE)
			.add(EntityType.GIANT)
			.add(EntityType.ZOMBIE_HORSE)
			.add(EntityType.SKELETON)
			.add(EntityType.STRAY)
			.add(EntityType.WITHER_SKELETON)
			.add(EntityType.WITHER)
			.add(EntityType.SKELETON_HORSE)
			.buildImmutable();
	private static Set<EntityType> arthropodTypes = SetBuilder.init(() -> EnumSet.noneOf(EntityType.class))
			.add(EntityType.SPIDER)
			.add(EntityType.CAVE_SPIDER)
			.add(EntityType.SILVERFISH)
			.add(EntityType.ENDERMITE)
			.buildImmutable();
	
	public static boolean isUndead(Entity entity) {
		return undeadTypes.contains(entity.getType());
	}
	
	public static boolean isArthropod(Entity entity) {
		return arthropodTypes.contains(entity.getType());
	}
	
}
