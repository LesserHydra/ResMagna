package com.lesserhydra.praedagrandis.conditions;

import com.lesserhydra.praedagrandis.targeters.Target;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface Condition {
	
	boolean test(Target target);
	
	default Condition negate() { return target -> !test(target); }
	
	interface ForEntity extends Condition {
		boolean test(LivingEntity target);
		default boolean test(Target target) { return target.isEntity() && test(target.asEntity()); }
	}
	
	interface ForLocation extends Condition {
		boolean test(Location target);
		default boolean test(Target target) { return target.isLocation() && test(target.asLocation()); }
	}
	
	interface ForPlayer extends Condition {
		boolean test(Player target);
		default boolean test(Target target) { return target.isPlayer() && test(target.asPlayer()); }
	}
	
}
