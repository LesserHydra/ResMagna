package com.roboboy.PraedaGrandis.Abilities;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class TeleportAbility extends Ability
{
	private final GrandLocation location;
	private final String worldSuffix;
	private final int spreadX;
	private final int spreadY;
	private final int spreadZ;
	private final int attempts;
	private final boolean includeCenter;
	private final boolean failSafe;
	private final boolean perfectSpread;
	private final boolean ender;
	
	public TeleportAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		location = args.getLocation(new GrandLocation(), true,		"location", "loc", "l", null);
		
		int spread = args.getInteger(0, false,			"spread", "sprd");
		int spreadH = args.getInteger(spread, false,	"spreadh", "sprdh", "sh");
		int spreadV = args.getInteger(spread, false,	"spreadv", "sprdv", "sv");
		spreadX = args.getInteger(spreadH, false,		"spreadx", "sx");
		spreadY = args.getInteger(spreadV, false,		"spready", "sy");
		spreadZ = args.getInteger(spreadH, false,		"spreadz", "sz");
		
		attempts = args.getInteger(32, false,			"numberofattempts", "numattempts", "attempts", "tries", "try", "att");
		includeCenter = args.getBoolean(false, false,	"includecenter", "center");
		failSafe = args.getBoolean(false, false,		"failsafe", "mustbesafe", "safe");
		perfectSpread = args.getBoolean(false, false,	"perfectspread", "perfect");
		ender = args.getBoolean(false, false,			"movetofloor", "floor");
		
		String dimensionString = args.getString(null, false,	"dimension"); //TODO: Move to GrandLocation
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
		
		if (spreadX > 0 || spreadY > 0 || spreadZ > 0) centerLoc = getSpread(centerLoc);
		if (failSafe && !isSafe(centerLoc)) return;
		
		centerLoc.setDirection(target.getEntity().getLocation().getDirection());
		target.getEntity().teleport(centerLoc);
	}

	private Location getSpread(Location center) {
		if (perfectSpread) return getSpreadFromSafe(center);
		
		for (int i = 0; i < attempts; i++) {
			Location random = center.clone().add(getRandomComponent(spreadX), getRandomComponent(spreadY), getRandomComponent(spreadZ));
			if (isSafe(random)) return random.getBlock().getLocation().add(0.5, 0, 0.5);
		}
		
		return center;
	}
	
	private int getRandomComponent(int spread){
		return PraedaGrandis.RANDOM_GENERATOR.nextInt(spread+1)-spread/2;
	}

	private Location getSpreadFromSafe(Location centerLoc) {
		List<Location> safe = getSafeInRadius(centerLoc);
		if (safe.isEmpty()) return centerLoc;
		Location result = safe.get(PraedaGrandis.RANDOM_GENERATOR.nextInt(safe.size()));
		result.add(0.5, 0, 0.5);
		return result;
	}

	private String getBaseWorldName(String worldName) {
		if (worldName.endsWith("_nether")) return worldName.replace("_nether", "");
		if (worldName.endsWith("_the_end")) return worldName.replace("_the_end", "");
		return worldName;
	}

	private List<Location> getSafeInRadius(Location center) {
		List<Location> safeLocations = new LinkedList<>();
		
		int centerX = center.getBlockX();
		int centerY = center.getBlockY();
		int centerZ = center.getBlockZ();
		
		int minX = centerX - spreadX;
		int maxX = centerX + spreadX;
		for (int x = minX; x <= maxX; x++) {
			
			int minZ = centerZ - spreadZ;
			int maxZ = centerZ + spreadZ;
			for (int z = minZ; z <= maxZ; z++) {
				
				int minY = Math.max(1, centerY - spreadY);
				int maxY = Math.min(centerY + spreadY, center.getWorld().getHighestBlockYAt(x, z) + 1);
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
