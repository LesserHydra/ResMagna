package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.bukkitutil.AreaEffectTools;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.BlockMask;
import com.lesserhydra.resmagna.arguments.BlockPattern;
import com.lesserhydra.resmagna.arguments.BlockPattern.BlockConstruct;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

class GhostBlockAbility implements Ability {
	
	private final Evaluators.ForBlockPattern blockPattern;
	private final Evaluators.ForBlockMask replaceMask;
	
	private final Evaluators.ForLocation centerLocation;
	private final Evaluators.ForDouble radius;
	
	GhostBlockAbility(ArgumentBlock args) {
		this.blockPattern = args.getBlockPattern(true, BlockPattern.buildEmpty(),	"blocks", "block", "b", null);
		this.replaceMask = args.getBlockMask(false, BlockMask.buildBlank(),			"replace", "repl");
		
		this.centerLocation = args.getLocation(false, GrandLocation.CURRENT,	"centerlocation", "location", "loc", "l");
		this.radius = args.getDouble(false, 0D,								"radius", "rad", "r");
	}
	
	@Override
	public void run(Target target) {
		//Verify parameters
		if (!(blockPattern.evaluate(target)
				&& replaceMask.evaluate(target)
				&& centerLocation.evaluate(target)
				&& radius.evaluate(target))) return;
		
		AreaEffectTools.sphereStream(centerLocation.get(), radius.get(), false)
				.filter(replaceMask.get()::testLocation)
				.forEach(this::ghostAtLocation);
	}

	@SuppressWarnings("deprecation")
	private void ghostAtLocation(Location location) {
		BlockConstruct replaceBlock = blockPattern.get().getBlock();
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendBlockChange(location, replaceBlock.getType(), replaceBlock.getData());
		}
	}

}
