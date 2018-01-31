package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.BlockPattern;
import com.lesserhydra.resmagna.arguments.BlockPattern.BlockConstruct;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.resmagna.util.AreaEffectTools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class GhostBlockAbility implements Ability {
	
	private final BlockPattern blockPattern;
	private final BlockMask replaceMask;
	
	private final GrandLocation centerLocation;
	private final double radius;
	
	GhostBlockAbility(ArgumentBlock args) {
		blockPattern = args.getBlockPattern(true, BlockPattern.buildEmpty(),	"blocks", "block", "b", null);
		replaceMask = args.getBlockMask(false, BlockMask.buildBlank(),			"replace", "repl");
		
		centerLocation = args.getLocation(false, new GrandLocation(),	"centerlocation", "location", "loc", "l");
		radius = args.getDouble(false, 0D,								"radius", "rad", "r");
	}
	
	@Override
	public void run(Target target) {
		AreaEffectTools.sphereStream(centerLocation.calculate(target), radius, false)
				.filter(replaceMask::testLocation)
				.forEach(this::ghostAtLocation);
	}

	@SuppressWarnings("deprecation")
	private void ghostAtLocation(Location location) {
		BlockConstruct replaceBlock = blockPattern.getBlock();
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendBlockChange(location, replaceBlock.getType(), replaceBlock.getData());
		}
	}

}
