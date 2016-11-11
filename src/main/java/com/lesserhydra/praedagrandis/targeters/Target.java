package com.lesserhydra.praedagrandis.targeters;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Target {
	
	/**
	 * Make a new, empty targeter
	 * @param holder Holder
	 * @return Newly constructed targeter
	 */
	public static Target makeEmpty(@NotNull Player holder) {
		return new Target(NONE, holder, NONE);
	}
	
	/**
	 * Make a new targeter
	 * @param holder Holder
	 * @param target Target construct
	 * @param activator Activator construct
	 * @return Newly constructed targeter
	 */
	public static Target make(@NotNull Player holder, @NotNull TargetConstruct target, @NotNull TargetConstruct activator) {
		return new Target(target, holder, activator);
	}
	
	/**
	 * Returns an empty target construct
	 * @return Empty target construct
	 */
	public static TargetConstruct none() { return NONE; }
	
	/**
	 * Constructs a target construct from given entity
	 * @param target Living entity
	 * @return Target construct
	 */
	public static TargetConstruct from(LivingEntity target) { return target == null ? NONE : new TargetEntity(target); }
	
	/**
	 * Constructs a target construct from given location
	 * @param target Location
	 * @return Target construct
	 */
	public static TargetConstruct from(Location target) { return target == null ? NONE : new TargetLocation(target.clone()); }
	
	
	private static TargetConstruct NONE = new TargetNone();
	
	private final TargetConstruct currentTarget;
	private final Player holder;
	private final TargetConstruct activatorTarget;
	private final Map<String, TargetConstruct> savedTargets;
	
	/**
	 * Returns the currently targeted construct
	 * @return The currently targeted construct
	 */
	public TargetConstruct current() { return currentTarget; }
	
	/**
	 * Returns the activator construct
	 * @return The activator construct
	 */
	public TargetConstruct activator() { return activatorTarget; }
	
	/**
	 * Get the holder player
	 * @return Holder player
	 */
	public Player getHolder() { return holder; }
	
	/**
	 * Save the currently targeted entity to all Targets sharing a saved target map
	 * @param saveName Save name
	 */
	public void save(String saveName) { savedTargets.put(saveName, currentTarget); }
	
	/**
	 * Gets the current target as an entity
	 * @return Current target entity, or null
	 */
	public LivingEntity asEntity() { return currentTarget.getEntity(); }
	
	/**
	 * Gets the current target as a location
	 * @return Current target location, or null
	 */
	public Location asLocation() { return currentTarget.getLocation(); }
	
	/**
	 * Gets the current target entity as a player
	 * @return Current target player, or null
	 * @throws ClassCastException If target cannot be expressed as a player
	 */
	public Player asPlayer() { return (Player) currentTarget.getEntity(); }
	
	/**
	 * Gets the current target entity as a given class
	 * @param clazz Class to attempt casting to
	 * @param <T> Class type
	 * @return Current target, or null
	 * @throws ClassCastException If target cannot be expressed as the given class
	 */
	public <T> T as(Class<T> clazz) { return clazz.cast(currentTarget.getEntity()); }
	
	/**
	 * Checks to see if nothing is currently targeted
	 * @return True if there is no current target
	 */
	public boolean isNull() { return currentTarget.isNull(); }
	
	/**
	 * Checks whether or not an entity is currently targeted
	 * @return True is the current target is an entity
	 */
	public boolean isEntity() { return currentTarget.getEntity() != null; }
	
	/**
	 * Checks whether or not a location is currently targeted
	 * @return True is the current target is a location
	 */
	public boolean isLocation() { return !currentTarget.isNull(); }
	
	/**
	 * Checks whether or not a player is currently targeted
	 * @return True is the current target is a player
	 */
	public boolean isPlayer() { return currentTarget.getEntity() instanceof Player; }
	
	/**
	 * Checks whether or not an entity of a given class is currently targeted
	 * @param clazz Class to check
	 * @param <T> Class type
	 * @return True is the current target is an entity of the given class
	 */
	public <T> boolean is(Class<T> clazz) { return clazz.isInstance(currentTarget.getEntity()); }
	
	/**
	 * Set current target to nothing
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetNone() { return new Target(NONE, holder, activatorTarget, savedTargets); }
	
	/**
	 * Set currently targeted entity to a saved entity from this Target's shared save map
	 * @param saveName Save name of saved entity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetSaved(String saveName) {
		return new Target(savedTargets.getOrDefault(saveName, NONE), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set current target to construct
	 * @param newTarget TargetConstruct to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target target(@NotNull TargetConstruct newTarget) {
		return new Target(newTarget, holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Sets both the current target and the activator target
	 * @param target Target construct
	 * @param activator Activator construct
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target set(@NotNull TargetConstruct target, @NotNull TargetConstruct activator) {
		return new Target(target, holder, activator, savedTargets);
	}
	
	
	/*
	 * Construct a fresh Target from a new save map
	 */
	private Target(TargetConstruct currentTarget, Player holder, TargetConstruct activatorTarget) {
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		this.savedTargets = new HashMap<>();
	}
	
	/*
	 * Construct a new Target sharing a saved target map
	 */
	private Target(TargetConstruct currentTarget, Player holder, TargetConstruct activatorTarget,
	               Map<String, TargetConstruct> savedTargets) {
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		this.savedTargets = savedTargets;
	}
	
	private interface TargetConstruct {
		Location getLocation();
		LivingEntity getEntity();
		boolean isNull();
	}
	
	private static class TargetNone implements TargetConstruct {
		@Override
		public Location getLocation() { return null; }
		
		@Override
		public LivingEntity getEntity() { return null; }
		
		@Override
		public boolean isNull() { return true; }
	}
	
	private static class TargetEntity implements TargetConstruct {
		private final LivingEntity targetEntity;
		
		TargetEntity(LivingEntity targetEntity) { this.targetEntity = targetEntity; }
		
		@Override
		public Location getLocation() { return targetEntity == null ? null : targetEntity.getLocation(); }
		
		@Override
		public LivingEntity getEntity() { return targetEntity; }
		
		@Override
		public boolean isNull() { return targetEntity == null; }
	}
	
	private static class TargetLocation implements TargetConstruct {
		private final Location targetLocation;
		
		TargetLocation(Location targetEntity) { this.targetLocation = targetEntity; }
		
		@Override
		public Location getLocation() { return targetLocation.clone(); }
		
		@Override
		public LivingEntity getEntity() { return null; }
		
		@Override
		public boolean isNull() { return targetLocation == null; }
	}
	
}
