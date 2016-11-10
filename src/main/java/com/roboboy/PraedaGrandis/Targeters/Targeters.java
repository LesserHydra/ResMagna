package com.roboboy.PraedaGrandis.Targeters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.stream.Collectors;

/**
 * Serves as a place to define trivial Targeters
 */
public class Targeters {
	
	public static Targeter.Singleton NONE = t -> t.target(Target.none());
	public static Targeter.Singleton CURRENT = t -> t;
	public static Targeter.Singleton HOLDER = t -> t.target(Target.from(t.getHolder()));
	public static Targeter.Singleton ACTIVATOR = t -> t.target(t.activator());
	public static Targeter.Singleton MOUNT = Targeters::mountTargeter;
	public static Targeter.Singleton RIDER = Targeters::riderTargeter;
	
	public static Targeter ONLINE_PLAYERS =  t -> Bukkit.getOnlinePlayers().stream()
														.map(p -> t.target(Target.from(p)))
														.collect(Collectors.toList());

	private static Target mountTargeter(Target currentTarget) {
		if (!currentTarget.isEntity()) return currentTarget.targetNone();
		Entity mount = currentTarget.asEntity().getVehicle();
		return currentTarget.target(mount instanceof LivingEntity ? Target.from((LivingEntity) mount) : Target.none());
	}
	
	private static Target riderTargeter(Target currentTarget) {
		if (!currentTarget.isEntity()) return currentTarget.targetNone();
		Entity rider = currentTarget.asEntity().getPassenger();
		return currentTarget.target(rider instanceof LivingEntity ? Target.from((LivingEntity) rider) : Target.none());
	}
	
}
