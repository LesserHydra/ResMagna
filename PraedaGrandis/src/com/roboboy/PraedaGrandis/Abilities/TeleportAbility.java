package com.roboboy.PraedaGrandis.Abilities;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class TeleportAbility extends Ability
{
	final private GrandLocation location;
	final private String worldSuffix;
	final private int spreadH;
	final private int spreadV;
	final private boolean includeCenter;
	final private boolean ender;
	
	public TeleportAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		location = args.getLocation("location", new GrandLocation(), true);
		spreadH = args.getInteger("spreadh", 0, false);
		spreadV = args.getInteger("spreadv", 0, false);
		includeCenter = args.getBoolean("includecenter", true, false);
		ender = args.getBoolean("ender", false, false);
		
		String dimensionString = args.get("dimension", null, false);
		worldSuffix = getWorldSuffix(dimensionString);
	}

	private String getWorldSuffix(String dimensionString)
	{
		if (dimensionString == null)						return null;
		if (dimensionString.equalsIgnoreCase("overworld"))	return "";
		if (dimensionString.equalsIgnoreCase("nether"))		return "_nether";
		if (dimensionString.equalsIgnoreCase("end"))		return "_the_end";
		
		//Error logging
		return null;
	}

	@Override
	protected void execute(Target target) {
		Location centerLoc = location.calculate(target);
		if (centerLoc == null) return;
		
		if (worldSuffix != null) {
			String worldName = getBaseWorldName(centerLoc.getWorld().getName()) + worldSuffix;
			World world = Bukkit.getWorld(worldName);
			//Error logging
			if (world == null) return;
			centerLoc.setWorld(world);
		}
		
		if (spreadH > 0 || spreadV > 0) {
			List<Location> safe = getSafeInRadius(centerLoc);
			if (!safe.isEmpty()) centerLoc = safe.get(PraedaGrandis.RANDOM_GENERATOR.nextInt(safe.size()));
		}
		
		centerLoc.add(0.5, 0, 0.5);
		centerLoc.setDirection(target.get().getLocation().getDirection());
		target.get().teleport(centerLoc);
	}

	private String getBaseWorldName(String worldName)
	{
		if (worldName.endsWith("_nether")) return worldName.replace("_nether", "");
		if (worldName.endsWith("_the_end")) return worldName.replace("_the_end", "");
		return worldName;
	}

	private List<Location> getSafeInRadius(Location center) {
		List<Location> safeLocations = new LinkedList<>();
		
		int centerX = center.getBlockX();
		int centerY = center.getBlockY();
		int centerZ = center.getBlockZ();
		
		int minX = centerX - spreadH;
		int maxX = centerX + spreadH;
		for (int x = minX; x <= maxX; x++) {
			
			int minZ = centerZ - spreadH;
			int maxZ = centerZ + spreadH;
			for (int z = minZ; z <= maxZ; z++) {
				
				int minY = Math.max(1, centerY - spreadV);
				int maxY = Math.min(centerY + spreadV, center.getWorld().getHighestBlockYAt(x, z) + 1);
				for (int y = minY; y <= maxY; y++) {
					
					if (!includeCenter && x == centerX && z == centerZ && y == centerY) continue;
					Location toCheck = new Location(center.getWorld(), x, y, z);
					if (ender) toCheck = getFloor(toCheck);
					if (isSafe(toCheck)) safeLocations.add(toCheck);
				}
			}
		}
		
		return safeLocations;
	}
	
	private Location getFloor(Location loc) {
		Block currentBlock = loc.getBlock();
		Block nextBlock = currentBlock;
		while (nextBlock.getY() > 0 && !nextBlock.getType().isSolid()) {
			currentBlock = nextBlock;
			nextBlock = currentBlock.getRelative(BlockFace.DOWN);
		}
		return currentBlock.getLocation();
	}

	private boolean isSafe(Location loc) {
		Block feetBlock = loc.getBlock();
		if (feetBlock.getType().isSolid()) return false;
		if (!feetBlock.getRelative(BlockFace.DOWN).getType().isSolid()) return false;
		if (feetBlock.getRelative(BlockFace.UP).getType().isSolid()) return false;
		
		if (feetBlock.isLiquid()) return false;
		
		return true;
	}

}
