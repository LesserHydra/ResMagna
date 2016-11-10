package com.roboboy.PraedaGrandis.Abilities;

import com.roboboy.PraedaGrandis.Function.Functor;
import com.roboboy.PraedaGrandis.Targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

interface Ability extends Functor {
	
	interface WithEntity extends Functor {
		void run(LivingEntity target);
		default void run(Target target) { if (target.isEntity()) run(target.asEntity()); }
	}
	
	interface WithLocation extends Functor {
		void run(Location target);
		default void run(Target target) { if (target.isLocation()) run(target.asLocation()); }
	}
	
	interface WithPlayer extends Functor {
		void run(Player target);
		default void run(Target target) { if (target.isPlayer()) run(target.asPlayer()); }
	}
	
}
