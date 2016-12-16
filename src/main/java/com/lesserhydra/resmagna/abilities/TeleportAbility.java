package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.bukkitutil.AreaEffectTools;
import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.arguments.GrandLocation;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TeleportAbility implements Ability {
	
	//Location
	private final Evaluators.ForLocation location;
	
	//Integer
	private final Evaluators.ForInt spreadX;
	private final Evaluators.ForInt spreadY;
	private final Evaluators.ForInt spreadZ;
	private final Evaluators.ForInt attempts;
	
	//Boolean
	private final Evaluators.ForBoolean includeCenter;
	private final Evaluators.ForBoolean failSafe;
	private final Evaluators.ForBoolean perfectSpread;
	private final Evaluators.ForBoolean ender;
	
	TeleportAbility(ArgumentBlock args) {
		location = args.getLocation(false, GrandLocation.CURRENT,         "location", "loc", "l", null);
		
		Evaluators.ForInt spread = args.getInteger(false, 0,    "spread", "sprd");
		Evaluators.ForInt spreadH = args.getInteger(false, spread,      "spreadh", "sprdh", "sh");
		Evaluators.ForInt spreadV = args.getInteger(false, spread,      "spreadv", "sprdv", "sv");
		spreadX = args.getInteger(false, spreadH,		"spreadx", "sx");
		spreadY= args.getInteger(false, spreadV,		"spready", "sy");
		spreadZ= args.getInteger(false, spreadH,		"spreadz", "sz");
		
		attempts = args.getInteger(false, 32,			"numberofattempts", "numattempts", "attempts", "tries", "try", "att");
		includeCenter = args.getBoolean(false, false,	"includecenter", "center");
		failSafe = args.getBoolean(false, false,		"failsafe", "mustbesafe", "safe");
		perfectSpread = args.getBoolean(false, false,	"perfectspread", "perfect");
		ender = args.getBoolean(false, false,			"movetofloor", "floor");
	}
	
	@Override
	public void run(Target target) {
		if (!target.isEntity()) {
			GrandLogger.log("Tried to run teleport ability with invalid target.", LogType.RUNTIME_ERRORS);
			return;
		}
		LivingEntity targetEntity = target.asEntity();
		
		//Evaluate parameters
		if (!evaluateParams(target)) return;
		
		Location centerLoc = location.get();
		if (spreadX.get() > 0 || spreadY.get() > 0 || spreadZ.get() > 0) centerLoc = getSpread(centerLoc);
		if (failSafe.get() && !isSafe(centerLoc)) return;
		
		centerLoc.setDirection(targetEntity.getLocation().getDirection());
		targetEntity.teleport(centerLoc);
	}
	
	private boolean evaluateParams(Target target) {
		return spreadX.evaluate(target)
				&& spreadY.evaluate(target)
				&& spreadZ.evaluate(target)
				&& attempts.evaluate(target)
				&& includeCenter.evaluate(target)
				&& failSafe.evaluate(target)
				&& perfectSpread.evaluate(target)
				&& ender.evaluate(target)
				&& location.evaluate(target);
	}

	private Location getSpread(Location center) {
		if (perfectSpread.get()) return getSpreadFromSafe(center);
		
		for (int i = 0; i < attempts.get(); i++) {
			Location random = center.clone().add(getRandomComponent(spreadX.get()), getRandomComponent(spreadY.get()), getRandomComponent(spreadZ.get()));
			if (ender.get()) random = getFloor(random);
			if (isSafe(random)) return random.getBlock().getLocation().add(0.5, 0, 0.5);
		}
		
		return center;
	}
	
	private int getRandomComponent(int spread){
		return ResMagna.RANDOM_GENERATOR.nextInt(2*spread+1)-spread;
	}

	private Location getSpreadFromSafe(Location centerLoc) {
		List<Location> safe = getSafeInRadius(centerLoc);
		if (safe.isEmpty()) return centerLoc;
		Location result = safe.get(ResMagna.RANDOM_GENERATOR.nextInt(safe.size()));
		result = result.getBlock().getLocation();
		result.add(0.5, 0, 0.5);
		return result;
	}

	private List<Location> getSafeInRadius(Location center) {
		Stream<Location> stream = AreaEffectTools.cuboidStream(center, spreadX.get(), spreadY.get(), spreadZ.get());
		if (ender.get()) stream = stream.map(this::getFloor);
		
		return stream.filter(location -> (includeCenter.get() || location.getBlock() != center.getBlock()) && isSafe(location))
				.collect(Collectors.toList());
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
		return !feetBlock.getType().isSolid()
				&& !feetBlock.isLiquid()
				&& feetBlock.getRelative(BlockFace.DOWN).getType().isSolid()
				&& !feetBlock.getRelative(BlockFace.UP).getType().isSolid();
		
	}

}
