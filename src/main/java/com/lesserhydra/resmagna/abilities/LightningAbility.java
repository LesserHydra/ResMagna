package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

class LightningAbility implements Ability {
	
	private final Evaluators.ForBoolean effectOnly;
	private final Evaluators.ForBoolean snap;
	private final Evaluators.ForBoolean hitCeiling;
	
	LightningAbility(ArgumentBlock args) {
		this.effectOnly = args.getBoolean(false, false,		"effectonly", "iseffect", "effect", "e");
		this.snap = args.getBoolean(false, false,			"snaptofloor", "snap", "floor");
		this.hitCeiling = args.getBoolean(false, false,		"hitceiling", "ceiling");
	}

	@Override
	public void run(Target target) {
		//Verify target
		if (!target.isLocation()) {
			GrandLogger.log("Tried to run lightning ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		
		//Verify parameters
		if (!(effectOnly.evaluate(target)
				&& snap.evaluate(target)
				&& hitCeiling.evaluate(target))) return;
		
		Location location = getLocation(target.asLocation());
		if (effectOnly.get()) location.getWorld().strikeLightningEffect(location);
		else location.getWorld().strikeLightning(location);
	}

	private Location getLocation(Location targetLocation) {
		if (!snap.get()) return targetLocation;
		
		Block targetBlock = targetLocation.getBlock();
		if (hitCeiling.get()) targetBlock = targetLocation.getWorld().getHighestBlockAt(targetLocation);
		
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
		Block last = targetBlock;
		while (!targetBlock.getType().isSolid()) {
			last = targetBlock;
			targetBlock = targetBlock.getRelative(BlockFace.DOWN);
		}
		return last.getLocation();
	}

}
