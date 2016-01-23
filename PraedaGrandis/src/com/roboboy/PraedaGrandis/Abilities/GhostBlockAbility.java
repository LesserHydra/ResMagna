package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class GhostBlockAbility extends Ability
{
	private final Material material;
	private final byte data;
	private final GrandLocation location;
	
	public GhostBlockAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args)
	{
		super(slotType, activator, targeter);
		
		String[] blockStrings = args.get("block", "stone:0", true).split(":");
		material = Material.matchMaterial(blockStrings[0]);
		data = Byte.parseByte(blockStrings[1]);
		
		location = args.getLocation("location", new GrandLocation(0, 0, 0, true, true, true), true);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void execute(Target target)
	{
		LivingEntity e = target.get();
		if (e instanceof Player)
		{
			Player p = (Player) e;
			p.sendBlockChange(location.calculate(p.getLocation()), material, data);
		}
	}

}
