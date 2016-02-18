package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Target
{
	private final TargetEntity currentTarget;
	private final Player holder;
	private final TargetEntity activatorTarget;
	private final Map<String, TargetEntity> savedTargets;
	
	/**
	 * Construct a fresh Target with a new save map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 */
	public Target(LivingEntity currentTarget, Player holder, LivingEntity activatorTarget)
	{
		this.currentTarget = new TargetEntity(currentTarget);
		this.holder = holder;
		this.activatorTarget = new TargetEntity(activatorTarget);
		
		this.savedTargets = new HashMap<>();
	}
	
	/**
	 * Construct a fresh Target with a new save map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 */
	public Target(Location currentTarget, Player holder, LivingEntity activatorTarget)
	{
		this.currentTarget = new TargetEntity(currentTarget);
		this.holder = holder;
		this.activatorTarget = new TargetEntity(activatorTarget);
		
		this.savedTargets = new HashMap<>();
	}
	
	/**
	 * Construct a fresh Target with a new save map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 */
	public Target(LivingEntity currentTarget, Player holder, Location activatorTarget)
	{
		this.currentTarget = new TargetEntity(currentTarget);
		this.holder = holder;
		this.activatorTarget = new TargetEntity(activatorTarget);
		
		this.savedTargets = new HashMap<>();
	}
	
	/**
	 * Construct a fresh Target with a new save map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 */
	public Target(Location currentTarget, Player holder, Location activatorTarget)
	{
		this.currentTarget = new TargetEntity(currentTarget);
		this.holder = holder;
		this.activatorTarget = new TargetEntity(activatorTarget);
		
		this.savedTargets = new HashMap<>();
	}
	
	/**
	 * Construct a new Target sharing a saved target map
	 * @param currentTarget
	 * @param holder
	 * @param activatorTarget
	 * @param savedTargets Custom save map to share
	 */
	private Target(TargetEntity currentTarget, Player holder, TargetEntity activatorTarget, Map<String, TargetEntity> savedTargets)
	{
		this.currentTarget = currentTarget;
		this.holder = holder;
		this.activatorTarget = activatorTarget;
		this.savedTargets = savedTargets;
	}
	
	/**
	 * Save the currently targeted entity to all Targets sharing a saved target map
	 * @param saveName Save name
	 */
	public void save(String saveName) {
		savedTargets.put(saveName, currentTarget);
	}
	
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
	public Location getLocation() {
		return currentTarget.getLocation();
	}
	
	/**
	 * Checks to see if nothing is currently targeted
	 * @return True if there is no current target
	 */
	public boolean isNull(){
		return currentTarget.isNull();
	}
	
	/**
	 * Get the holder player
	 * @return Holder player
	 */
	public Player getHolder() {
		return holder;
	}
	
	/**
	 * Get the entity targeted by whatever originally constructed the Target
	 * @return Activator entity
	 */
	public LivingEntity getActivator() {
		return activatorTarget.getEntity();
	}
	
	/**
	 * Set currently targeted entity to an arbitrary entity
	 * @param newTarget LivingEntity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target target(LivingEntity newTarget) {
		return new Target(new TargetEntity(newTarget), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to an arbitrary location
	 * @param newTarget Location to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target target(Location newTarget) {
		return new Target(new TargetEntity(newTarget), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to a saved entity from this Target's shared save map
	 * @param saveName Save name of saved entity to target
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetSaved(String saveName) {
		return new Target(savedTargets.get(saveName), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to the holder
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetHolder() {
		return new Target(new TargetEntity(holder), holder, activatorTarget, savedTargets);
	}
	
	/**
	 * Set currently targeted entity to the activator
	 * @return Newly constructed Target sharing saved target map
	 */
	public Target targetActivator() {
		return new Target(activatorTarget, holder, activatorTarget, savedTargets);
	}
	
}
