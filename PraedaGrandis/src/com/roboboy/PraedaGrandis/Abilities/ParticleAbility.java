package com.roboboy.PraedaGrandis.Abilities;

import org.bukkit.Location;
import com.darkblade12.particleeffect.ParticleEffect;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GrandLocation;

class ParticleAbility extends Ability
{
	private final ParticleEffect particleEffect;
	private final float offsetX;
	private final float offsetY;
	private final float offsetZ;
	private final float speed;
	private final int amount;
	private final GrandLocation centerLocation;
	private final double range;

	public ParticleAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args)
	{
		super(slotType, activator, targeter);
		
		//TODO: Verify name
		particleEffect = ParticleEffect.fromName(args.getString("", true,	"particlename", "particle", "name", "type", "n"));
		amount = args.getInteger(1, true,									"amount", "amnt", "a");
		
		centerLocation = args.getLocation(new GrandLocation(), false,	"location", "loc", "l");
		
		
		float spread = args.getFloat(0.5F, false,		"spread", "radius", "sprd", "r");
		float spreadH = args.getFloat(spread, false,	"spreadh", "sprdh", "sh", "rh");
		float spreadV = args.getFloat(spread, false,	"spreadv", "sprdv", "sv", "rv");
		offsetX = args.getFloat(spreadH, false,			"spreadx", "sx", "x");
		offsetY = args.getFloat(spreadV, false,			"spready", "sy", "y");
		offsetZ = args.getFloat(spreadH, false,			"spreadz", "sz", "z");
		
		
		speed = args.getFloat(0F, false,	"speed", "s");
		range = args.getDouble(60D, false,	"range", "r");
	}

	@Override
	protected void execute(Target target) {
		Location calculatedLocation = centerLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		particleEffect.display(offsetX, offsetY, offsetZ, speed, amount, calculatedLocation, range);
	}

}
