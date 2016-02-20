package com.roboboy.PraedaGrandis;

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
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;
import com.roboboy.PraedaGrandis.Configuration.ItemHandler;

public class ActivatorListener implements Listener
{
	PraedaGrandis plugin;
	
	public ActivatorListener(PraedaGrandis plugin) {
		this.plugin = plugin;
	}
	
	
	/*----------CLICK----------*/
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerClick(PlayerInteractEvent e) {
		ActivatorType clickType = getClickActivator(e.getAction());
		if (e.getClickedBlock() != null && !e.getPlayer().isSneaking()) {
			if (PraedaGrandis.CLICK_STEALERS.contains(e.getClickedBlock().getType())) return;
		}
		if (!clickType.isNull()) activate(clickType, e.getPlayer(), (LivingEntity)null);
	}
	
	
	/*----------Interact----------*/
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEntityEvent e)
	{
		if (!(e.getRightClicked() instanceof LivingEntity)) return;
		
		LivingEntity target = (LivingEntity) e.getRightClicked();
		ActivatorType interactType = getInteractActivator(target);
		
		if (!interactType.isNull()) activate(interactType, e.getPlayer(), target);
	}
	
	/*----------Break----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemBreak(PlayerItemBreakEvent e) {
		GrandItem gItem = ItemHandler.getInstance().matchItem(e.getBrokenItem());
		if (gItem == null) return;
		
		activate(ActivatorType.BREAK, e.getPlayer(), (LivingEntity)null);
	}
	
	
	/*----------MOVE----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		
		//Look
		if (!to.getDirection().equals(from.getDirection())) {
			activate(ActivatorType.LOOK, e.getPlayer(), e.getFrom());
		}
		
		//Move
		if (!to.getBlock().equals(from.getBlock())) {
			ActivatorType moveType = getMoveActivator(from, to);
			activate(moveType, p, e.getFrom());
		}
	}
	
	
	/*----------Hurt & Attack----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		activate(ActivatorType.HURTOTHER, (Player) e.getEntity(), (LivingEntity)null);
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
	
	
	/*----------TELEPORT----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		activate(ActivatorType.TELEPORT, e.getPlayer(), e.getFrom());
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPortal(PlayerPortalEvent e) {
		activate(ActivatorType.PORTAL, e.getPlayer(), e.getFrom());
	}
	
	/**
	 * Sends an activator on the next tick
	 * @param type Type of activator
	 * @param holder Player involved in event
	 * @param activatorTarget Target for the activator, or null
	 */
	private void activate(final ActivatorType type, final Player holder, final LivingEntity activatorTarget) {
		//Run one tick later, to avoid the infinite hunger bug
		new BukkitRunnable() { @Override public void run() {
			GrandInventory pInv = InventoryHandler.getInstance().getItemsFromPlayer(holder);
			for (GrandInventory.InventoryElement element : pInv.getItems()) {
				element.grandItem.activateAbilities(type, element.slotType, new Target(holder, holder, activatorTarget));
			}
		}}.runTaskLater(plugin, 1L);
	}
	
	/**
	 * Sends an activator on the next tick
	 * @param type Type of activator
	 * @param holder Player involved in event
	 * @param activatorTarget Target for the activator, or null
	 */
	private void activate(final ActivatorType type, final Player holder, final Location activatorTarget) {
		//Run one tick later, to avoid the infinite hunger bug
		new BukkitRunnable() { @Override public void run() {
			GrandInventory pInv = InventoryHandler.getInstance().getItemsFromPlayer(holder);
			for (GrandInventory.InventoryElement element : pInv.getItems()) {
				element.grandItem.activateAbilities(type, element.slotType, new Target(holder, holder, activatorTarget));
			}
		}}.runTaskLater(plugin, 1L);
	}
	
	/**
	 * Calculates the appropriate click activator
	 * @param action Player action type
	 * @return Click activator type
	 */
	private ActivatorType getClickActivator(Action action) {
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) return ActivatorType.CLICKLEFT;
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) return ActivatorType.CLICKRIGHT;
		return ActivatorType.NONE;
	}
	
	/**
	 * Calculates the appropriate interact activator
	 * @param target LivingEntity that was interacted with
	 * @return Interact activator type.
	 */
	private ActivatorType getInteractActivator(LivingEntity target) {
		if (target instanceof Player) return ActivatorType.INTERACTPLAYER;
		return ActivatorType.INTERACTMOB;
	}
	
	/**
	 * Calculates the appropriate move activator
	 * @param from From location
	 * @param to To location
	 * @return Move activator type.
	 */
	private ActivatorType getMoveActivator(Location from, Location to) {
		if (from.getY() < to.getY()) return ActivatorType.MOVEUP;
		if (from.getY() > to.getY()) return ActivatorType.MOVEDOWN;
		return ActivatorType.MOVEWALK;
	}
	
	/**
	 * Calculates the appropriate hurt and attack activators.
	 * @param hurt The entity that was damaged
	 * @param attacker The damaging entity, or null
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
	 * @param died The entity that died
	 * @param killer The entity that killed, or null
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
		if (!(died.getLastDamageCause() instanceof EntityDamageByEntityEvent)) return null;
		return getAttacker( ((EntityDamageByEntityEvent)died.getLastDamageCause()).getDamager() );
	}
}
