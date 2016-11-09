package com.roboboy.PraedaGrandis.Abilities.Targeters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collections;
import java.util.List;

/**
 * Serves as a place to define trivial Targeters
 */
public class Targeters {
	
	public static Targeter NONE = t -> Collections.singletonList(t.targetNone());
	public static Targeter CURRENT = Collections::singletonList;
	public static Targeter HOLDER = t -> Collections.singletonList(t.targetHolder());
	public static Targeter ACTIVATOR = t -> Collections.singletonList(t.targetActivator());
	public static Targeter MOUNT = Targeters::mountTargeter;
	public static Targeter RIDER = Targeters::riderTargeter;
	public static Targeter ONLINE_PLAYERS =  t -> t.multiTarget(Bukkit.getOnlinePlayers());

	private static List<Target> mountTargeter(Target currentTarget) {
		if (!currentTarget.isEntity()) return Collections.emptyList();
		Entity mount = currentTarget.getEntity().getVehicle();
		return Collections.singletonList(currentTarget.target(mount instanceof LivingEntity ? (LivingEntity) mount : null));
	}
	
	private static List<Target> riderTargeter(Target currentTarget) {
		if (!currentTarget.isEntity()) return Collections.emptyList();
		Entity rider = currentTarget.getEntity().getPassenger();
		return Collections.singletonList(currentTarget.target(rider instanceof LivingEntity ? (LivingEntity) rider : null));
	}
	
}
