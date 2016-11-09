package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class LightningAbility implements Ability {
	
	private final boolean effectOnly;
	private final boolean snap;
	private final boolean hitCeiling;
	
	LightningAbility(BlockArguments args) {
		effectOnly = args.getBoolean(false, false,		"effectonly", "iseffect", "effect", "e");
		snap = args.getBoolean(false, false,			"snaptofloor", "snap", "floor");
		hitCeiling = args.getBoolean(false, false,		"hitceiling", "ceiling");
	}

	@Override
	public void execute(Target target) {
		Location location = getLocation(target.getLocation());
		if (effectOnly) location.getWorld().strikeLightningEffect(location);
		else location.getWorld().strikeLightning(location);
	}

	private Location getLocation(Location targetLocation) {
		if (!snap) return targetLocation;
		
		Block targetBlock = targetLocation.getBlock();
		if (hitCeiling) targetBlock = targetLocation.getWorld().getHighestBlockAt(targetLocation);
		
		Location result;
		if (targetBlock.getType().isSolid()) result = findAirSpace(targetBlock);
		else result = findGround(targetBlock);
		
		return result;
	}

	private Location findAirSpace(Block targetBlock) {
		while (targetBlock.getType().isSolid()) {
			targetBlock = targetBlock.getRelative(BlockFace.UP);
		}
		return targetBlock.getLocation();
	}
	
	private Location findGround(Block targetBlock) {
		Block last = null;
		while (!targetBlock.getType().isSolid()) {
			last = targetBlock;
			targetBlock = targetBlock.getRelative(BlockFace.DOWN);
		}
		return last.getLocation();
	}

}
