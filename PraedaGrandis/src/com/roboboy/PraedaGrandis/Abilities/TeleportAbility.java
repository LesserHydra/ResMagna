package com.roboboy.PraedaGrandis.Abilities;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class TeleportAbility extends Ability
{
	final private static Random randomGenerator = new Random();
	
	final private GrandLocation location;
	final private int radius;
	
	public TeleportAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		location = args.getLocation("location", new GrandLocation(0, 0, 0, true, true, true), true);
		radius = args.getInteger("radius", 0, false);
	}

	@Override
	protected void execute(Target target) {
		Location centerLoc = location.calculate(target.get().getLocation());
		
		if (radius > 0) {
			List<Location> safe = getSafeInRadius(centerLoc, radius);
			if (safe.isEmpty()) return;
			
			centerLoc = safe.get(randomGenerator.nextInt(safe.size()));
		}
		
		centerLoc.add(0.5, 0, 0.5);
		centerLoc.setDirection(target.get().getLocation().getDirection());
		target.get().teleport(centerLoc);
	}

	private List<Location> getSafeInRadius(Location center, int radius) {
		List<Location> safeLocations = new LinkedList<>();
		
		int centerX = center.getBlockX();
		int centerY = center.getBlockY();
		int centerZ = center.getBlockZ();
		
		int maxX = centerX + radius;
		for (int x = centerX-radius; x <= maxX; x++) {
			int maxZ = centerZ + radius;
			for (int z = centerZ-radius; z <= maxZ; z++) {
				int maxY = Math.min(centerY + radius, center.getWorld().getHighestBlockYAt(x, z) + 1);
				for (int y = centerY-radius; y <= maxY; y++)
				{
					if (x == centerX && z == centerZ && y == centerY) continue;
					Location toCheck = new Location(center.getWorld(), x, y, z);
					if (isSafe(toCheck)) safeLocations.add(toCheck);
				}
			}
		}
		
		return safeLocations;
	}
	
	private boolean isSafe(Location loc) {
		Block feetBlock = loc.getBlock();
		if (feetBlock.getType().isSolid()) return false;
		if (!feetBlock.getRelative(BlockFace.DOWN).getType().isSolid()) return false;
		if (feetBlock.getRelative(BlockFace.UP).getType().isSolid()) return false;
		
		if (feetBlock.getType() == Material.STATIONARY_LAVA || feetBlock.getType() == Material.STATIONARY_WATER) return false;
		
		return true;
	}

}
