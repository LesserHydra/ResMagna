package com.lesserhydra.praedagrandis.abilities;

import com.lesserhydra.praedagrandis.function.Functor;
import com.lesserhydra.praedagrandis.targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

interface Ability extends Functor {
	
	interface ForEntity extends Functor {
		void run(LivingEntity target);
		default void run(Target target) { if (target.isEntity()) run(target.asEntity()); }
	}
	
	interface ForLocation extends Functor {
		void run(Location target);
		default void run(Target target) { if (target.isLocation()) run(target.asLocation()); }
	}
	
	interface ForPlayer extends Functor {
		void run(Player target);
		default void run(Target target) { if (target.isPlayer()) run(target.asPlayer()); }
	}
	
}
