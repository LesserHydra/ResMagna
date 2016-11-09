package com.roboboy.PraedaGrandis.Targeters;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Target {
	
	private final TargetConstruct currentTarget;
	private final Player holder;
	private final TargetConstruct activatorTarget;
	private final Map<String, TargetConstruct> savedTargets;
	
	/**
	 * Construct a fresh Target with a new save map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 */
	public Target(TargetConstruct currentTarget, Player holder, TargetConstruct activatorTarget) {
		if (currentTarget == null || activatorTarget == null) throw new IllegalArgumentException("Cannot target null.");
		
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		
		this.savedTargets = new HashMap<>();
	}
	
	/**
	 * Construct a new Target sharing a saved target map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 * @param savedTargets Custom save map to share
	 */
	private Target(TargetConstruct currentTarget, Player holder, TargetConstruct activatorTarget,
	               Map<String, TargetConstruct> savedTargets) {
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		this.savedTargets = savedTargets;
	}
	
	/**
	 * Save the currently targeted entity to all Targets sharing a saved target map
	 * @param saveName Save name
	 */
	public void save(String saveName) { savedTargets.put(saveName, currentTarget); }
	
	/**
	 * Get the currently targeted entity
	 * @return Current target entity
	 */
	public LivingEntity getEntity() {
		return currentTarget.getEntity();
	}
	
	/**
	 * Get the currently targeted location
	 * @return Current target location
	 */
	public Location getLocation() { return currentTarget.getLocation(); }
	
	public Player asPlayer() { return (Player) currentTarget.getEntity(); }
	
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
	
	public boolean isPlayer() { return currentTarget.getEntity() instanceof Player; }
	
	public <T> boolean is(Class<T> clazz) { return clazz.isInstance(currentTarget.getEntity()); }
	
	public TargetConstruct getCurrent() { return currentTarget; }
	
	/**
	 * Get the holder player
	 * @return Holder player
	 */
	public Player getHolder() { return holder; }
	
	/**
	 * Get the entity targeted by whatever originally constructed the Target
	 * @return Activator entity
	 */
	public TargetConstruct getActivator() { return activatorTarget; }
	
	/**
	 * Set current target to nothing
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetNone() {
		return new Target(new TargetNone(), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set current target to a LivingEntity
	 * @param newTarget Entity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target target(LivingEntity newTarget) {
		//if (newTarget == null) throw new IllegalArgumentException("Cannot target null.");
		return new Target(new TargetEntity(newTarget), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set current target to a Location
	 * @param newTarget Location to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target target(Location newTarget) {
		//if (newTarget == null) throw new IllegalArgumentException("Cannot target null.");
		return new Target(new TargetLocation(newTarget), holder, activatorTarget, savedTargets);
	}
	
	public List<Target> multiTarget(Collection<? extends LivingEntity> newTargets) {
		return newTargets.stream()
				.map(this::target)
				.collect(Collectors.toList());
	}
	
	/**
	 * Set currently targeted entity to a saved entity from this Target's shared save map
	 * @param saveName Save name of saved entity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetSaved(String saveName) {
		TargetConstruct saved = savedTargets.get(saveName);
		if (saved == null) saved = new TargetEntity(null);
		return new Target(saved, holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to the holder
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetHolder() { return new Target(new TargetEntity(holder), holder, activatorTarget, savedTargets); }
	
	/**
	 * Set currently targeted entity to the activator
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetActivator() { return new Target(activatorTarget, holder, activatorTarget, savedTargets); }
	
}
