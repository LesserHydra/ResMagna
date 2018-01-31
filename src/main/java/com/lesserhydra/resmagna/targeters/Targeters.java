package com.lesserhydra.resmagna.targeters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serves as a place to define trivial Targeters
 */
public class Targeters {
	
	public static Targeter.Singleton NONE = t -> t.target(Target.none());
	public static Targeter.Singleton CURRENT = t -> t;
	public static Targeter.Singleton HOLDER = t -> t.target(Target.from(t.getHolder()));
	public static Targeter.Singleton ACTIVATOR = t -> t.target(t.activator());
	
	public static Targeter.Singleton MOUNT = currentTarget -> {
		if (!currentTarget.isEntity()) return currentTarget.targetNone();
		Entity mount = currentTarget.asEntity().getVehicle();
		return currentTarget.target(mount instanceof LivingEntity ? Target.from((LivingEntity) mount) : Target.none());
	};
	
	public static Targeter RIDER = currentTarget -> {
		if (!currentTarget.isEntity()) return Collections.singletonList(currentTarget.targetNone());
		return currentTarget.asEntity().getPassengers().stream()
				.filter(entity -> entity instanceof LivingEntity)
				.map(rider -> currentTarget.target(Target.from((LivingEntity) rider)))
				.collect(Collectors.toList());
		
	};
	
	public static Targeter ONLINE_PLAYERS =  t -> Bukkit.getOnlinePlayers().stream()
														.map(p -> t.target(Target.from(p)))
														.collect(Collectors.toList());
	
}
