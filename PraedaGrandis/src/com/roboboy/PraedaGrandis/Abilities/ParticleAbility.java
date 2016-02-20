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
		particleEffect = ParticleEffect.fromName(args.getString(true, "",	"particlename", "particle", "name", "type", "n"));
		amount = args.getInteger(true, 1,									"amount", "amnt", "a");
		
		centerLocation = args.getLocation(false, new GrandLocation(),	"location", "loc", "l");
		
		
		float spread = args.getFloat(false, 0.5F,		"spread", "radius", "sprd", "r");
		float spreadH = args.getFloat(false, spread,	"spreadh", "sprdh", "sh", "rh");
		float spreadV = args.getFloat(false, spread,	"spreadv", "sprdv", "sv", "rv");
		offsetX = args.getFloat(false, spreadH,			"spreadx", "sx", "x");
		offsetY = args.getFloat(false, spreadV,			"spready", "sy", "y");
		offsetZ = args.getFloat(false, spreadH,			"spreadz", "sz", "z");
		
		
		speed = args.getFloat(false, 0F,	"speed", "s");
		range = args.getDouble(false, 60D,	"range", "r");
	}

	@Override
	protected void execute(Target target) {
		Location calculatedLocation = centerLocation.calculate(target);
		if (calculatedLocation == null) return;
		
		particleEffect.display(offsetX, offsetY, offsetZ, speed, amount, calculatedLocation, range);
	}

}
