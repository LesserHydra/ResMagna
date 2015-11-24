package com.roboboy.PraedaGrandis.Abilities;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class TeleportAbility extends Ability
{
	final private int radius;
	final private boolean atHighest;
	final private GrandLocation location;
	final private int attempts;
	
	public TeleportAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args) {
		super(slotType, activator, targeter);
		radius = Integer.parseInt(args.get(1));
		atHighest = Boolean.parseBoolean(args.get(2));
		location = new GrandLocation(args.get(3));
		attempts = Integer.parseInt(args.get(4));
	}

	@Override
	protected void execute(Target target) {
		Location centerLoc = location.calculate(target.get().getLocation());
		
		if (radius > 0) {
			Random rand = new Random();
			Location randomLoc;
			int i = 0;
			do {
				randomLoc = centerLoc.clone();
				Vector randomVector = new Vector(rand.nextInt(radius*2+1)-radius, rand.nextInt(radius*2+1)-radius, rand.nextInt(radius*2+1)-radius);
				randomLoc.add(randomVector);
				randomLoc.setY(Math.max(0, randomLoc.getY()));
				if (!atHighest) randomLoc = getSafe(randomLoc);
				i++;
			} while (i < attempts && randomLoc == null);
			if (i == attempts && randomLoc == null) return;
			
			centerLoc = randomLoc;
		}
		
		if (atHighest) centerLoc = centerLoc.getWorld().getHighestBlockAt(centerLoc).getLocation();
		
		centerLoc.add(0.5, 0, 0.5);
		centerLoc.setDirection(target.get().getLocation().getDirection());
		target.get().teleport(centerLoc);
	}

	private Location getSafe(Location loc)
	{
		Block bottom = loc.getBlock();
		Block top = bottom.getRelative(BlockFace.UP);
		if (bottom.getType().isSolid()) return null;
		if (top.getType().isSolid()) return null;
		
		Block ground = bottom;
		do {
			if (ground.getY() < 0) return null;
			if (ground.getType() == Material.LAVA || ground.getType() == Material.STATIONARY_LAVA) return null;
			bottom = ground;
			ground = bottom.getRelative(BlockFace.DOWN);
		} while (!ground.getType().isSolid());
		
		return bottom.getLocation();
	}

}
