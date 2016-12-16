package com.lesserhydra.resmagna.abilities;

import com.lesserhydra.resmagna.ResMagna;
import com.lesserhydra.resmagna.arguments.ArgumentBlock;
import com.lesserhydra.resmagna.arguments.Evaluators;
import com.lesserhydra.resmagna.targeters.Target;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.LinkedList;
import java.util.List;

class FireworkAbility implements Ability {
	
	private final FireworkEffect.Type type;
	private final Evaluators.ForBoolean flicker;
	private final Evaluators.ForBoolean trail;
	private final List<Color> colors;
	private final List<Color> fades;
	
	private final Evaluators.ForInt power;
	
	FireworkAbility(ArgumentBlock args) {
		this.type = args.getEnum(true, FireworkEffect.Type.BALL,
				"fireworkeffecttype", "firework", "effecttype", "type");
		
		this.flicker = args.getBoolean(false, false, "flicker");
		this.trail = args.getBoolean(false, false,   "trail");
		
		this.colors = new LinkedList<>();
		Color lastColor = args.getColor(true, Color.BLACK,      "color", "color1");
		for (int i = 2; lastColor != null; i++) {
			colors.add(lastColor);
			lastColor = args.getColor(false, null,      "color"+i);
		}
		
		this.fades = new LinkedList<>();
		Color lastFade = args.getColor(false, null,     "fade", "fade1");
		for (int i = 2; lastFade != null; i++) {
			fades.add(lastFade);
			lastFade = args.getColor(false, null,       "fade"+i);
		}
		
		power = args.getInteger(false, -1,              "power");
	}

	@Override
	public void run(Target target) {
		if (!target.isLocation()) return;
		Location loc = target.asLocation();
		
		if (!(flicker.evaluate(target)
				&& trail.evaluate(target)
				&& power.evaluate(target))) return;
		
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder()
				.with(type)
				.flicker(flicker.get())
				.trail(trail.get())
				.withColor(colors)
				.withFade(fades)
				.build();
		meta.addEffect(effect);
		if (power.get() >= 0) meta.setPower(power.get());
		firework.setFireworkMeta(meta);
		
		//TODO: Why is this on a delay again?
		if (power.get() < 0) Bukkit.getScheduler().runTask(ResMagna.plugin, firework::detonate);
	}

}
