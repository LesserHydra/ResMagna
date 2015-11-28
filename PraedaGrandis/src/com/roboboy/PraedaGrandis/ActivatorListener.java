package com.roboboy.PraedaGrandis;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
import com.roboboy.PraedaGrandis.Abilities.ActivatorType;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

public class ActivatorListener implements Listener
{
	PraedaGrandis plugin;
	
	public ActivatorListener(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	
	/*----------CLICK----------*/
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerClick(PlayerInteractEvent e) {
		if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			activate(ActivatorType.CLICKLEFT, e.getPlayer(), null);
		}
		else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			activate(ActivatorType.CLICKRIGHT, e.getPlayer(), null);
		}
	}
	
	
	/*----------Interact----------*/
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEntityEvent e)
	{
		if (!(e.getRightClicked() instanceof LivingEntity)) return;
		
		LivingEntity target = (LivingEntity) e.getRightClicked();
		if (target instanceof Player) {
			activate(ActivatorType.INTERACTPLAYER, e.getPlayer(), target);
		}
		else {
			activate(ActivatorType.INTERACTMOB, e.getPlayer(), target);
		}
	}
	
	
	/*----------Hurt & Attack----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		activate(ActivatorType.HURTOTHER, (Player) e.getEntity(), null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
	{
		//Skip if a non living entity was damaged (painting, minecart, ect)
		if (!(e.getEntity() instanceof LivingEntity)) return;
		
		//Get hurt and attacker
		LivingEntity hurt = (LivingEntity) e.getEntity();
		LivingEntity attacker = getAttacker(e.getDamager());
		
		//Calculate activators
		Pair<ActivatorType, ActivatorType> actPair = getAttackActivators(hurt, attacker);
		ActivatorType hurtType = actPair.getLeft();
		ActivatorType attackType = actPair.getRight();
		
		//Activation
		if (!hurtType.isNull())	activate(hurtType, (Player) hurt, attacker);
		if (!attackType.isNull())	activate(attackType, (Player) attacker, hurt);
	}
	
	
	/*----------Kill & Death----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent e)
	{
		LivingEntity killed = e.getEntity();
		LivingEntity killer = e.getEntity().getKiller(); //Only returns players
		
		if (killer == null) killer = getKiller(killed);
		
		Pair<ActivatorType, ActivatorType> actPair = getDeathActivators(killed, killer);
		ActivatorType deathType = actPair.getLeft();
		ActivatorType killType = actPair.getRight();
		
		//Activation
		if (!deathType.isNull()) activate(deathType, (Player) killed, killer);
		if (!killType.isNull()) activate(killType, (Player) killer, killed);
	}
	
	
	/*----------MOVE----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		
		if (!to.getDirection().equals(from.getDirection())) { //Look
			activate(ActivatorType.LOOK, e.getPlayer(), null);
		}
		
		if (!to.getBlock().equals(from.getBlock())) { 	//Move
			ActivatorType moveType;
			if (from.getY() < to.getY()) {				//Up
				moveType = ActivatorType.MOVEUP;
			}
			else if (from.getY() > to.getY()) {			//Down
				moveType = ActivatorType.MOVEDOWN;
			}
			else {										//Horizontal Movement
				moveType = ActivatorType.MOVEWALK;
			}
			
			activate(moveType, p, null);
		}
	}
	
	
	/*----------TELEPORT----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		activate(ActivatorType.TELEPORT, e.getPlayer(), null);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPortal(PlayerPortalEvent e) {
		activate(ActivatorType.PORTAL, e.getPlayer(), null);
	}
	
	
	/**
	 * Sends an activator on the next tick
	 * @param type Type of activator
	 * @param holder Player involved in event
	 * @param activatorTarget Target for the activator, or null
	 */
	private void activate(final ActivatorType type, final Player holder, final LivingEntity activatorTarget)
	{
		//Run one tick later, to avoid the infinite hunger bug
		PraedaGrandis.plugin.getServer().getScheduler().scheduleSyncDelayedTask(PraedaGrandis.plugin, new Runnable() {
			@Override
			public void run() {
				//holder.sendMessage(ChatColor.BLUE + "Activated by " + type.toString() + " activator");
				Map<GrandItem, ItemSlotType> items = plugin.itemHandler.getItemsFromPlayer(holder);
				for (Entry<GrandItem, ItemSlotType> entry : items.entrySet()) {
					//holder.sendMessage(ChatColor.BLUE + "Found GrandItem in slot " + entry.getValue());
					entry.getKey().activateAbilities(type, entry.getValue(), new Target(holder, holder, activatorTarget));
				}
			}
		}, 1L);
	}
	
	/**
	 * Gets an attacking LivingEntity from the damager Entity. Looks for the
	 * projectile source if damager is a projectile.
	 * @param damager Damager from EntityDamagedByEntityEvent
	 * @return The living entity that caused the damage event, or null
	 */
	private LivingEntity getAttacker(Entity damager) {
		//Damager is living
		if (damager instanceof LivingEntity) return (LivingEntity) damager;
		
		//Damager is projectile
		if (damager instanceof Projectile) {
			ProjectileSource source = ((Projectile) damager).getShooter();
			if (source instanceof LivingEntity) return (LivingEntity) source;
		}
		
		//No living attacker found
		return null;
	}
	
	/**
	 * Gets a living entity's killer from last damage event.
	 * @param died The entity that died
	 * @return The living entity that caused the last damage event, or null
	 */
	private LivingEntity getKiller(LivingEntity died) {
		if (died.getLastDamageCause() instanceof EntityDamageByEntityEvent) return null;
		return getAttacker( ((EntityDamageByEntityEvent)died.getLastDamageCause()).getDamager() );
	}
	
	/**
	 * Calculates the appropriate hurt and attack activators.
	 * @param hurt The entity that was damaged; May not be null
	 * @param attacker The damaging entity
	 * @return A pair containing the hurt activator in the left, and the
	 * attack activator on the right. Either can be NONE.
	 */
	private Pair<ActivatorType, ActivatorType> getAttackActivators(LivingEntity hurt, LivingEntity attacker)
	{		
		boolean hurtIsPlayer = (hurt instanceof Player);
		boolean attackerIsPlayer = (attacker instanceof Player);
		
		//If neither is a player, otherwise at least one is a player
		if (!hurtIsPlayer && !attackerIsPlayer)		return Pair.of(ActivatorType.NONE, ActivatorType.NONE);
		
		//If both are equal, otherwise different
		if (hurt.equals(attacker))					return Pair.of(ActivatorType.HURTSELF, ActivatorType.ATTACKSELF);
		
		//If both are players, otherwise only one is a player
		if (hurtIsPlayer && attackerIsPlayer)		return Pair.of(ActivatorType.HURTPLAYER, ActivatorType.ATTACKPLAYER);
		
		//If attacker is player, otherwise hurt is player
		if (attackerIsPlayer)						return Pair.of(ActivatorType.NONE, ActivatorType.ATTACKMOB);
		
		//If attacker is not null, otherwise attacker is null
		if (attacker != null)						return Pair.of(ActivatorType.HURTMOB, ActivatorType.NONE);
		
		//Hurt is player and attacker is null
		return Pair.of(ActivatorType.HURTOTHER, ActivatorType.NONE);
	}
	
	/**
	 * Calculates the appropriate death and kill activators.
	 * @param died The entity that died; May not be null
	 * @param killer The entity that killed
	 * @return A pair containing the death activator in the left, and the
	 * kill activator on the right. Either can be NONE.
	 */
	private Pair<ActivatorType, ActivatorType> getDeathActivators(LivingEntity died, LivingEntity killer)
	{		
		boolean diedIsPlayer = (died instanceof Player);
		boolean killerIsPlayer = (killer instanceof Player);
		
		//If neither is a player, otherwise at least one is a player
		if (!diedIsPlayer && !killerIsPlayer)	return Pair.of(ActivatorType.NONE, ActivatorType.NONE);
		
		//If both are equal, otherwise different
		if (died.equals(killer))				return Pair.of(ActivatorType.DEATHSELF, ActivatorType.KILLSELF);
		
		//If both are players, otherwise only one is a player
		if (diedIsPlayer && killerIsPlayer)		return Pair.of(ActivatorType.DEATHPLAYER, ActivatorType.KILLPLAYER);
		
		//If killer is player, otherwise died is player
		if (killerIsPlayer)						return Pair.of(ActivatorType.NONE, ActivatorType.KILLMOB);
		
		//If killer is not null, otherwise killer is null
		if (killer != null)						return Pair.of(ActivatorType.DEATHMOB, ActivatorType.NONE);
		
		//Died is player and killer is null
		return Pair.of(ActivatorType.DEATHOTHER, ActivatorType.NONE);
	}
}
