package com.roboboy.PraedaGrandis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.Abilities.Ability;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

/* Every so often (configurable in main config) the plugin should
 * activate all players that have a GrandItem, calling this class'
 * activatePlayer function.
 */
public class AbilityTimer
{
	final private GrandItem item;
	final private Ability ability;
	final private long delay;
	
	private boolean running = false;
	private TimerRunnable timer;
	
	final private Set<Player> activePlayers = new HashSet<>();
	
	//Runnable
	private class TimerRunnable extends BukkitRunnable
	{
		@Override
		public void run()
		{
			//PraedaGrandis.plugin.getLogger().info("Running timer!");
			//For all active players
			for (Iterator<Player> it = activePlayers.iterator(); it.hasNext();)
			{
				Player p = it.next();
				
				//If the player dies or logs off
				if (!p.isOnline() || !p.isValid()) {
					it.remove();
					continue;
				}
				
				//Activate for every matching item in player inventory
				GrandInventory pInv = PraedaGrandis.plugin.inventoryHandler.getItemsFromPlayer(p);
				List<GrandInventory.InventoryElement> elements = pInv.getItems(item.getName());
				for (GrandInventory.InventoryElement element : elements) {
					ability.activate(element.slotType, new Target(p, p, null));
				}
				
				if (elements.isEmpty()) it.remove(); //Deactivate player
			}
			
			//If no players are active, cancel the timer
			if (activePlayers.isEmpty()) {
				this.cancel();
				running = false;
			}
		}
	}
	
	public AbilityTimer(GrandItem item, Ability ability, long delay) {
		this.item = item;
		this.ability = ability;
		this.delay = delay;
	}
	
	/**
	 * Adds the given player to the set of players this timer should run for.
	 * @param player Player to activate this timer for
	 */
	public void activatePlayer(Player player)
	{
		activePlayers.add(player);
		if (!running) {
			running = true;
			timer = new TimerRunnable(); //It seems you must construct a new instance every time
			timer.runTaskTimer(PraedaGrandis.plugin, 1L, delay);;
		}
	}
}
