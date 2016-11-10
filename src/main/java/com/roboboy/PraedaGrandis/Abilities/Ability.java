package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

interface Ability extends Functor {
	
	interface Entity extends Functor {
		void run(LivingEntity target);
		default void run(Target target) { if (target.isEntity()) run(target.asEntity()); }
	}
	
	interface Location extends Functor {
		void run(org.bukkit.Location target);
		default void run(Target target) { if (target.isLocation()) run(target.asLocation()); }
	}
	
	interface Player extends Functor {
		void run(org.bukkit.entity.Player target);
		default void run(Target target) { if (target.isPlayer()) run(target.asPlayer()); }
	}
	
}
