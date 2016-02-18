package com.roboboy.PraedaGrandis.Abilities;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import com.roboboy.PraedaGrandis.ActivatorType;
import com.roboboy.PraedaGrandis.ItemSlotType;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;

class FireworkAbility extends Ability
{	
	private final int power;
	private final FireworkEffect effect;
	
	public FireworkAbility(ItemSlotType slotType, ActivatorType activator, Targeter targeter, BlockArguments args) {
		super(slotType, activator, targeter);
		
		FireworkEffect.Type type = Type.valueOf(args.get("type", "BALL", true).toUpperCase());
		boolean flicker = args.getBoolean("flicker", false, false);
		boolean trail = args.getBoolean("trail", false, false);
		
		List<Color> colors = new LinkedList<Color>();
		Color lastColor = args.getColor("color", Color.BLACK, false);
		lastColor = args.getColor("color1", lastColor, false);
		for (int i = 2; lastColor != null; i++) {
			colors.add(lastColor);
			lastColor = args.getColor("color"+i, null, false);
		}
		
		List<Color> fades = new LinkedList<Color>();
		Color lastFade = args.getColor("fade", null, false);
		lastFade = args.getColor("fade1", lastFade, false);
		for (int i = 2; lastFade != null; i++) {
			colors.add(lastFade);
			lastColor = args.getColor("fade"+i, null, false);
		}
		
		power = args.getInteger("power", -1, false);
		effect = FireworkEffect.builder().with(type).flicker(flicker).trail(trail).withColor(colors).withFade(fades).build();
	}

	@Override
	protected void execute(Target target) {
		final Firework firework = target.getLocation().getWorld().spawn(target.getLocation(), Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		if (power >= 0) meta.setPower(power);
		firework.setFireworkMeta(meta);
		
		if (power < 0) {
			new BukkitRunnable() { @Override public void run() {
				firework.detonate();
			}}.runTaskLater(PraedaGrandis.plugin, 1L);
		}
	}

}
