package com.lesserhydra.praedagrandis.conditions;

import com.lesserhydra.praedagrandis.targeters.Target;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

class Conditions {
	
	//Identity checks
	static Condition IS_NONE        = Target::isNull;
	static Condition IS_ENTITY      = Target::isEntity;
	static Condition IS_PLAYER      = Target::isPlayer;
	static Condition IS_HOLDER      = t -> t.getHolder().equals(t.asEntity());
	static Condition IS_CREATURE    = t -> t.asEntity() instanceof Creature;
	static Condition IS_AGEABLE     = t -> t.asEntity() instanceof Ageable;
	static Condition IS_ANIMAL      = t -> t.asEntity() instanceof Animals;
	static Condition IS_TAMEABLE    = t -> t.asEntity() instanceof Tameable;
	static Condition IS_MONSTER     = t -> t.asEntity() instanceof Monster;
	
	//Trivial entity state checks
	static Condition.ForEntity IS_VALID        = LivingEntity::isValid;
	static Condition.ForEntity HAS_AI          = LivingEntity::hasAI;
	static Condition.ForEntity HAS_GRAVITY     = LivingEntity::hasGravity;
	static Condition.ForEntity IS_COLLIDABLE   = LivingEntity::isCollidable;
	static Condition.ForEntity IS_INVULNERABLE = LivingEntity::isInvulnerable;
	static Condition.ForEntity IS_SILENT       = LivingEntity::isSilent;
	static Condition.ForEntity IS_ON_GROUND    = LivingEntity::isOnGround;
	static Condition.ForEntity IS_RIDING       = LivingEntity::isInsideVehicle;
	static Condition.ForEntity IS_GLIDING      = LivingEntity::isGliding;
	static Condition.ForEntity IS_GLOWING      = LivingEntity::isGlowing;
	static Condition.ForEntity IS_LEASHED      = LivingEntity::isLeashed;
	static Condition.ForEntity IS_BURNING      = t -> t.getFireTicks() > 0;
	static Condition.ForEntity IS_MOUNT        = t -> t.getPassenger() instanceof LivingEntity;
	
	//Trivial creature state checks
	static Condition HAS_TARGET         = t -> t.is(Creature.class) && t.as(Creature.class).getTarget() != null;
	static Condition IS_ADULT           = t -> t.is(Ageable.class) && t.as(Ageable.class).isAdult();
	static Condition IS_AGE_LOCKED      = t -> t.is(Ageable.class) && t.as(Ageable.class).getAgeLock();
	static Condition CAN_BREED          = t -> t.is(Ageable.class) && t.as(Ageable.class).canBreed();
	static Condition IS_TAMED           = t -> t.is(Tameable.class) && t.as(Tameable.class).isTamed();
	static Condition IS_WOLF_SITTING    = t -> t.is(Wolf.class) && t.as(Wolf.class).isSitting();
	static Condition IS_WOLF_ANGRY      = t -> t.is(Wolf.class) && t.as(Wolf.class).isAngry();
	static Condition IS_CAT_SITTING     = t -> t.is(Ocelot.class) && t.as(Ocelot.class).isSitting();
	static Condition IS_PIGMAN_ANGRY    = t -> t.is(PigZombie.class) && t.as(PigZombie.class).isAngry();

	//Trivial player state checks
	static Condition.ForPlayer IS_SNEAKING     = Player::isSneaking;
	static Condition.ForPlayer IS_SPRINTING    = Player::isSprinting;
	static Condition.ForPlayer IS_BLOCKING     = Player::isBlocking;
	static Condition.ForPlayer IS_HAND_RAISED  = Player::isHandRaised;
	static Condition.ForPlayer IS_SLEEPING     = Player::isSleeping;
	
	//Trivial location state checks
	static Condition.ForLocation IS_RAINING    = l -> l.getWorld().hasStorm();
	static Condition.ForLocation IS_THUNDERING = l -> l.getWorld().isThundering();
	static Condition.ForLocation IS_SHELTERED  = l -> l.getBlockY() < l.getWorld().getHighestBlockYAt(l);
	
	
}
