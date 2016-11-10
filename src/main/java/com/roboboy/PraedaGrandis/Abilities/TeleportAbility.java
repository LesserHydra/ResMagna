package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Arguments.GrandLocation;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.bukkitutil.AreaEffectTools;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TeleportAbility implements Ability {
	
	private final GrandLocation location;
	private final int spreadX;
	private final int spreadY;
	private final int spreadZ;
	private final int attempts;
	private final boolean includeCenter;
	private final boolean failSafe;
	private final boolean perfectSpread;
	private final boolean ender;
	
	TeleportAbility(ArgumentBlock args) {
		location = args.getLocation(false, new GrandLocation(),		"location", "loc", "l", null);
		
		int spread = args.getInteger(false, 0,			"spread", "sprd");
		int spreadH = args.getInteger(false, spread,	"spreadh", "sprdh", "sh");
		int spreadV = args.getInteger(false, spread,	"spreadv", "sprdv", "sv");
		spreadX = args.getInteger(false, spreadH,		"spreadx", "sx");
		spreadY = args.getInteger(false, spreadV,		"spready", "sy");
		spreadZ = args.getInteger(false, spreadH,		"spreadz", "sz");
		
		attempts = args.getInteger(false, 32,			"numberofattempts", "numattempts", "attempts", "tries", "try", "att");
		includeCenter = args.getBoolean(false, false,	"includecenter", "center");
		failSafe = args.getBoolean(false, false,		"failsafe", "mustbesafe", "safe");
		perfectSpread = args.getBoolean(false, false,	"perfectspread", "perfect");
		ender = args.getBoolean(false, false,			"movetofloor", "floor");
	}
	
	@Override
	public void execute(Target target) {
		LivingEntity targetEntity = target.asEntity();
		if (targetEntity == null) return;
		
		Location centerLoc = location.calculate(target);
		if (centerLoc == null) return;
		
		if (spreadX > 0 || spreadY > 0 || spreadZ > 0) centerLoc = getSpread(centerLoc);
		if (failSafe && !isSafe(centerLoc)) return;
		
		centerLoc.setDirection(target.asEntity().getLocation().getDirection());
		targetEntity.teleport(centerLoc);
	}

	private Location getSpread(Location center) {
		if (perfectSpread) return getSpreadFromSafe(center);
		
		for (int i = 0; i < attempts; i++) {
			Location random = center.clone().add(getRandomComponent(spreadX), getRandomComponent(spreadY), getRandomComponent(spreadZ));
			if (ender) random = getFloor(random);
			if (isSafe(random)) return random.getBlock().getLocation().add(0.5, 0, 0.5);
		}
		
		return center;
	}
	
	private int getRandomComponent(int spread){
		return PraedaGrandis.RANDOM_GENERATOR.nextInt(2*spread+1)-spread;
	}

	private Location getSpreadFromSafe(Location centerLoc) {
		List<Location> safe = getSafeInRadius(centerLoc);
		if (safe.isEmpty()) return centerLoc;
		Location result = safe.get(PraedaGrandis.RANDOM_GENERATOR.nextInt(safe.size()));
		result = result.getBlock().getLocation();
		result.add(0.5, 0, 0.5);
		return result;
	}

	private List<Location> getSafeInRadius(Location center) {
		Stream<Location> stream = AreaEffectTools.cuboidStream(center, spreadX, spreadY, spreadZ);
		if (ender) stream = stream.map(this::getFloor);
		
		return stream.filter(location -> (includeCenter || location.getBlock() != center.getBlock()) && isSafe(location))
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
