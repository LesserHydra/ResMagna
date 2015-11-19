package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.ConfigString;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

public class GhostBlockAbility extends Ability
{
	private final Material material;
	private final byte data;
	private final GrandLocation location;
	
	public GhostBlockAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, ConfigString args)
	{
		super(slotType, activator, targeter);
		
		//TODO: Error handling. Must be three arguments (condition name + 2 args)
		String[] blockStrings = args.get(1).split(":");
		material = Material.matchMaterial(blockStrings[0]);
		data = Byte.parseByte(blockStrings[1]);
		location = new GrandLocation(args.get(2));
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
