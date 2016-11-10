package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Arguments.ArgumentBlock;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;

class FireworkAbility implements Ability.ForLocation {
	
	private final int power;
	private final FireworkEffect effect;
	
	FireworkAbility(ArgumentBlock args) {
		FireworkEffect.Type type = args.getEnum(true, FireworkEffect.Type.BALL,		"fireworkeffecttype", "firework", "effecttype", "type");
		
		boolean flicker = args.getBoolean(false, false,		"flicker");
		boolean trail = args.getBoolean(false, false,		"trail");
		
		List<Color> colors = new LinkedList<>();
		Color lastColor = args.getColor(true, Color.BLACK,		"color", "color1");
		for (int i = 2; lastColor != null; i++) {
			colors.add(lastColor);
			lastColor = args.getColor(false, null,				"color"+i);
		}
		
		List<Color> fades = new LinkedList<>();
		Color lastFade = args.getColor(false, null,		"fade", "fade1");
		for (int i = 2; lastFade != null; i++) {
			fades.add(lastFade);
			lastFade = args.getColor(false, null,		"fade"+i);
		}
		
		power = args.getInteger(false, -1,	"power");
		
		effect = FireworkEffect.builder().with(type).flicker(flicker).trail(trail).withColor(colors).withFade(fades).build();
	}

	@Override
	public void run(Location target) {
		final Firework firework = target.getWorld().spawn(target, Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		if (power >= 0) meta.setPower(power);
		firework.setFireworkMeta(meta);
		
		if (power < 0) {
			//TODO: No.
			new BukkitRunnable() { @Override public void run() {
				firework.detonate();
			}}.runTaskLater(PraedaGrandis.plugin, 1L);
		}
	}

}
