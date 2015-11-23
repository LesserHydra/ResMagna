package com.roboboy.PraedaGrandis;

import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
	
	/*----------Hurt & Attack----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent e)
	{
		ActivatorType hurtType = null;
		ActivatorType attackType = null;
		LivingEntity hurt = null;
		LivingEntity attacker = null;
		
		//Skip if a non living entity was damaged (painting, minecart, ect)
		if (e.getEntity() instanceof LivingEntity)
		{
			hurt = (LivingEntity) e.getEntity();
			
			//Get attacker, if exists
			if (hurt.getLastDamageCause() instanceof EntityDamageByEntityEvent) //By mob
			{
				EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) hurt.getLastDamageCause();
				Entity damager = damageEvent.getDamager();
				if (damager instanceof LivingEntity) {
					attacker = (LivingEntity) damager;
				}
				else if (damager instanceof Arrow) {
					ProjectileSource source = ((Arrow) damager).getShooter();
					if (source instanceof LivingEntity) attacker = (LivingEntity) source;
				}
			}
			
			//Player was hurt
			if (hurt instanceof Player) //TODO: Refactor. Use one "(attacker instanceof Player)"
			{
				if (attacker instanceof Player) //By player
				{
					if (((Player) hurt).equals((Player) attacker)) {
						hurtType = ActivatorType.HURTSELF;
						attackType = ActivatorType.ATTACKSELF;
					}
					else {
						hurtType = ActivatorType.HURTPLAYER;
						attackType = ActivatorType.ATTACKPLAYER;
					}
				}
				else if (attacker != null) //By mob
				{
					hurtType = ActivatorType.HURTMOB;
				}
				else { //By environment
					hurtType = ActivatorType.HURTOTHER;
				}
			}
			else if (attacker instanceof Player) {
				attackType = ActivatorType.ATTACKMOB;
			}
			
			//Activation
			if (hurtType != null) {
				activate(hurtType, (Player) hurt, attacker);
			}
			if (attackType != null) {
				activate(attackType, (Player) attacker, hurt);
			}
		}
	}
	
	/*----------Kill & Death----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent e)
	{
		ActivatorType killType = null;
		ActivatorType deathType = null;
		LivingEntity killed = e.getEntity();
		LivingEntity killer = e.getEntity().getKiller(); //Only returns players
		
		//Player was killed
		if (killed instanceof Player)
		{
			if (killer != null) //By player
			{
				if (((Player) killed).equals(killer)) {
					killType = ActivatorType.KILLSELF;
					deathType = ActivatorType.DEATHSELF;
				}
				else {
					killType = ActivatorType.KILLPLAYER;
					deathType = ActivatorType.DEATHPLAYER;
				}
			}
			else //Not by player
			{
				if (killed.getLastDamageCause() instanceof EntityDamageByEntityEvent) //By mob
				{
					deathType = ActivatorType.DEATHMOB;
					EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) killed.getLastDamageCause();
					Entity damager = damageEvent.getDamager();
					if (damager instanceof LivingEntity) {
						killer = (LivingEntity) damager;
					}
					else if (damager instanceof Arrow) {
						ProjectileSource source = ((Arrow) damager).getShooter();
						if (source instanceof LivingEntity) killer = (LivingEntity) source;
					}
				}
				else { //By environment
					deathType = ActivatorType.DEATHOTHER;
				}
			}
		}
		else //Mob was killed
		{
			if (killer != null) {
				killType = ActivatorType.KILLMOB;
			}
		}
		
		//Activation
		if (killType != null) {
			activate(killType, (Player) killer, killed);
		}
		if (deathType != null) {
			activate(deathType, (Player) killed, killer);
		}
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
	
	//TODO: Interact
	
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
}
