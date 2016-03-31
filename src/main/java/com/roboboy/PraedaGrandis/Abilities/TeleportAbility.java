package com.roboboy.PraedaGrandis.Abilities;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;
import com.roboboy.bukkitutil.AreaEffectTools;

class TeleportAbility extends Ability
{
	private final GrandLocation location;
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
	protected void execute(Target target) {
		LivingEntity targetEntity = target.getEntity();
		if (targetEntity == null) return;
		
		Location centerLoc = location.calculate(target);
		if (centerLoc == null) return;
		
		if (spreadX > 0 || spreadY > 0 || spreadZ > 0) centerLoc = getSpread(centerLoc);
		if (failSafe && !isSafe(centerLoc)) return;
		
		centerLoc.setDirection(target.getEntity().getLocation().getDirection());
		targetEntity.teleport(centerLoc);
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

	private List<Location> getSafeInRadius(Location center) {
		Stream<Location> stream = AreaEffectTools.cuboidStream(center, spreadX, spreadY, spreadZ);
		if (ender) stream.map(this::getFloor);
		stream.filter(location -> (includeCenter || location.getBlock() != center.getBlock()) && isSafe(location));
		
		return stream.collect(Collectors.toList());
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
