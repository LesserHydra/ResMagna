package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.BlockMask;
import com.roboboy.PraedaGrandis.Configuration.BlockPattern;
import com.roboboy.PraedaGrandis.Configuration.BlockPattern.BlockConstruct;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.bukkitutil.AreaEffectTools;

class GhostBlockAbility extends Ability
{
	private final BlockPattern blockPattern;
	private final BlockMask replaceMask;
	
	private final GrandLocation centerLocation;
	private final double radius;
	
	public GhostBlockAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		blockPattern = args.getBlockPattern(true, BlockPattern.buildEmpty(),		"blocks", "block", "b", null);
		replaceMask = args.getBlockMask(false, BlockMask.buildBlank(),			"replace", "repl");
		
		centerLocation = args.getLocation(false, new GrandLocation(),	"centerlocation", "location", "loc", "l");
		radius = args.getDouble(false, 0D,								"radius", "rad", "r");
	}
	
	@Override
	protected void execute(Target target) {
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
