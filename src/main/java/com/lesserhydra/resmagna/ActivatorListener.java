package com.lesserhydra.resmagna;

import com.lesserhydra.resmagna.activator.ActivatorType;
import com.lesserhydra.resmagna.configuration.GrandItem;
import com.lesserhydra.resmagna.configuration.ItemHandler;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ActivatorListener implements Listener {
	
	private ResMagna plugin;
	ActivatorListener(ResMagna plugin) { this.plugin = plugin; }
	
	/*----------CLICK----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerClick(PlayerInteractEvent e) {
		//Unless sneaking, skip blocks that can be interacted
		if (e.getClickedBlock() != null
				&& !e.getPlayer().isSneaking()
				&& ResMagna.CLICK_STEALERS.contains(e.getClickedBlock().getType())) return;
		
		ActivatorType clickType = getClickActivator(e.getAction());
		if (clickType.isValid()) activate(clickType, e.getPlayer(), (LivingEntity)null);
	}
	
	/*----------Interact----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof LivingEntity)) return;
		
		activate(ActivatorType.INTERACT, e.getPlayer(), (LivingEntity) e.getRightClicked());
	}
	
	
	/*----------BlockBreak----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		activate(ActivatorType.BREAK_BLOCK, e.getPlayer(), e.getBlock().getLocation());
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
	public void onPlayerMove(PlayerMoveEvent e) {
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
	
	/*----------TELEPORT----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		activate(e instanceof PlayerPortalEvent ? ActivatorType.PORTAL : ActivatorType.TELEPORT,
				e.getPlayer(),
				e.getFrom());
	}
	
	/*----------Hurt & Attack----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		//Redirect to other handler
		if (event instanceof EntityDamageByEntityEvent) {
			onEntityDamageByEntity((EntityDamageByEntityEvent) event);
			return;
		}
		
		//Only interested in players
		if (!(event.getEntity() instanceof Player)) return;
		activate(ActivatorType.HURT, (Player) event.getEntity(), (LivingEntity)null);
	}
	
	private void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
		//Skip if a non living entity was damaged (painting, minecart, ect)
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		//Get hurt and attacker
		LivingEntity hurt = (LivingEntity) event.getEntity();
		LivingEntity attacker = getAttacker(event.getDamager());
		
		//Activation
		if (hurt instanceof Player) activate(ActivatorType.HURT, (Player) hurt, attacker);
		if (attacker instanceof Player) activate(ActivatorType.ATTACK, (Player) attacker, hurt);
	}
	
	
	/*----------Kill & Death----------*/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity killed = e.getEntity();
		LivingEntity killer = e.getEntity().getKiller(); //Only returns players
		
		if (killer == null) killer = getKiller(killed);
		
		//Activation
		if (killed instanceof Player) activate(ActivatorType.DEATH, (Player) killed, killer);
		if (killer instanceof Player) activate(ActivatorType.KILL, (Player) killer, killed);
	}
	
	
	/*----------Activation Functions----------*/
	
	/* Activates with entity targeter
	 */
	private void activate(@NotNull ActivatorType type, @NotNull Player holder, @Nullable LivingEntity activatorTarget) {
		activate(type, Target.make(holder, Target.from(holder), Target.from(activatorTarget)));
	}
	
	/* Activates with location targeter
	 */
	private void activate(@NotNull ActivatorType type, @NotNull Player holder, @Nullable Location activatorTarget) {
		activate(type, Target.make(holder, Target.from(holder), Target.from(activatorTarget)));
	}
	
	private void activate(@NotNull ActivatorType type, @NotNull Target target) {
		GrandInventory pInv = InventoryHandler.getInstance().getItemsFromPlayer(target.getHolder());
		Bukkit.getScheduler().runTask(plugin,
				() -> pInv.getItems().forEach(e -> e.activateAbilities(type, target))
		);
	}
	
	
	/*----------Helper Functions----------*/
	
	/* Calculates the appropriate click activator
	 */
	@NotNull @Contract(pure = true)
	private ActivatorType getClickActivator(Action action) {
		switch (action) {
		case LEFT_CLICK_AIR:    return ActivatorType.LEFT_CLICK;
		case RIGHT_CLICK_AIR:   return ActivatorType.RIGHT_CLICK;
		case LEFT_CLICK_BLOCK:  return ActivatorType.LEFT_CLICK_BLOCK;
		case RIGHT_CLICK_BLOCK: return ActivatorType.RIGHT_CLICK_BLOCK;
		default:                return ActivatorType.NONE;
		}
	}
	
	/* Calculates the appropriate move activator
	 */
	@NotNull @Contract(pure = true)
	private ActivatorType getMoveActivator(@NotNull Location from, @NotNull Location to) {
		if (from.getY() < to.getY()) return ActivatorType.MOVE_UP;
		if (from.getY() > to.getY()) return ActivatorType.MOVE_DOWN;
		return ActivatorType.MOVE_WALK;
	}
	
	/* Gets an attacking LivingEntity from the damager Entity. Looks for the
	 * projectile source if damager is a projectile.
	 */
	@Nullable @Contract(pure = true)
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
	
	/* Gets a living entity's killer from last damage event.
	 */
	@Nullable @Contract(pure = true)
	private LivingEntity getKiller(@NotNull LivingEntity died) {
		if (!(died.getLastDamageCause() instanceof EntityDamageByEntityEvent)) return null;
		return getAttacker( ((EntityDamageByEntityEvent)died.getLastDamageCause()).getDamager() );
	}
	
}
