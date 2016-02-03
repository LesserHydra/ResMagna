package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class TemporaryBlockAbility extends Ability
{
	private final Material material;
	private final byte data;
	private final long lifetime;
	private final GrandLocation location;
	
	public TemporaryBlockAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args)
	{
		super(slotType, activator, targeter);
		
		material = Material.matchMaterial(args.get("type", "AIR", true));
		lifetime = args.getLong("ticks", 0L, true);
		data = (byte) args.getInteger("data", 0, false); //TODO: Please do properly
		location = args.getLocation("location", new GrandLocation(), false);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Target target) {
		Location calcLocation = location.calculate(target);
		if (calcLocation == null) return;
		
		final Block block = calcLocation.getBlock();
		final Material oldBlockMaterial = block.getType();
		final byte oldBlockData = block.getData();
		
		block.setType(material);
		block.setData(data);
		
		new BukkitRunnable() { @Override public void run() {
			block.setType(oldBlockMaterial);
			block.setData(oldBlockData);
		}}.runTaskLater(PraedaGrandis.plugin, lifetime);
	}

}
