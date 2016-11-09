package com.roboboy.PraedaGrandis.Abilities.Conditions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Configuration.BlockArguments;
import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import org.bukkit.Location;
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

public class ConditionFactory {
	
	//~?(\w+)\s*(?:(?:\((\$[\d]+)\))|([\w\s=<>\.]*[\w\.]))?\s*(@\w+\s*(?:\((\$[\d]+)\))?)?
	static private final Pattern conditionLinePattern = Pattern.compile("~?(\\w+)\\s*(?:(?:\\((\\$[\\d]+)\\))|([\\w\\s=<>\\.]*[\\w\\.]))?\\s*(@\\w+\\s*(?:\\((\\$[\\d]+)\\))?)?");
	
	public static Condition build(String conditionLine) {
		conditionLine = conditionLine.toLowerCase();
		
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(conditionLine);
		String simplifiedLine = groupParser.getSimplifiedString();
		
		//Match
		Matcher lineMatcher = conditionLinePattern.matcher(simplifiedLine);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid condition format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + conditionLine, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + simplifiedLine, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Get condition name and NOT opperator
		String conditionName = lineMatcher.group(1).toLowerCase();
		boolean not = conditionLine.startsWith("~");
		
		//Get condition arguments, if exist
		String argumentsGroupID = lineMatcher.group(2);
		BlockArguments conditionArgs = new BlockArguments(groupParser.getGrouping(argumentsGroupID), conditionLine);
		
		//Get unenclosed arguments, if exist
		String variableArgsString = lineMatcher.group(3);
		
		//Get Targeter
		String targeterString = lineMatcher.group(4);
		String targeterArgumentsGroupID = lineMatcher.group(5);
		targeterString = groupParser.readdGrouping(targeterString, targeterArgumentsGroupID);
		Targeter targeter = TargeterFactory.build(targeterString);
		
		//Construct condition by name
		Condition c = createCondition(conditionName, conditionArgs, variableArgsString);
		if (c == null) {
			GrandLogger.log("Invalid condition name: " + conditionName, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Return result, negated if appropriate
		return (not ? c.negate() : c);
	}
	
	private static Condition createCondition(String name, BlockArguments args, String varArgsString) {
		switch (name) {
		//Identity checks
		case "isnone":			return Target::isNull;
		case "isentity":        return Target::isEntity;
		case "isholder":		return t -> t.getHolder().equals(t.getEntity());
		case "isplayer":		return Target::isPlayer;
		case "iscreature":		return t -> t.is(Creature.class);
		case "isageable":		return t -> t.is(Ageable.class);
		case "isanimal":		return t -> t.is(Animals.class);
		case "istameable":      return t -> t.is(Tameable.class);
		case "ismonster":		return t -> t.is(Monster.class);
		
		//Trivial entity state checks
		case "isvalid":			return t -> t.isEntity() && t.getEntity().isValid();
		case "hasai":		    return t -> t.isEntity() && t.getEntity().hasAI();
		case "hasgravity":		return t -> t.isEntity() && t.getEntity().hasGravity();
		case "iscollidable":	return t -> t.isEntity() && t.getEntity().isCollidable();
		case "isinvulnerable":	return t -> t.isEntity() && t.getEntity().isInvulnerable();
		case "issilent":		return t -> t.isEntity() && t.getEntity().isSilent();
		case "isburning":		return t -> t.isEntity() && t.getEntity().getFireTicks() > 0;
		case "isonground":		return t -> t.isEntity() && t.getEntity().isOnGround();
		case "ismount":			return t -> t.isEntity() && t.getEntity().getPassenger() instanceof LivingEntity;
		case "isriding":		return t -> t.isEntity() && t.getEntity().isInsideVehicle();
		case "isgliding":		return t -> t.isEntity() && t.getEntity().isGliding();
		case "isglowing":		return t -> t.isEntity() && t.getEntity().isGlowing();
		case "isleashed":		return t -> t.isEntity() && t.getEntity().isLeashed();
		
		//Trivial creature state checks
		case "hastarget":       return t -> t.is(Creature.class) && t.as(Creature.class).getTarget() != null;
		case "isadult":         return t -> t.is(Ageable.class) && t.as(Ageable.class).isAdult();
		case "isageLocked":     return t -> t.is(Ageable.class) && t.as(Ageable.class).getAgeLock();
		case "canbreed":        return t -> t.is(Ageable.class) && t.as(Ageable.class).canBreed();
		case "istamed":         return t -> t.is(Tameable.class) && t.as(Tameable.class).isTamed();
		case "iswolfsitting":   return t -> t.is(Wolf.class) && t.as(Wolf.class).isSitting();
		case "iswolfangry":     return t -> t.is(Wolf.class) && t.as(Wolf.class).isAngry();
		case "iscatsitting":    return t -> t.is(Ocelot.class) && t.as(Ocelot.class).isSitting();
		case "ispigmanangry":   return t -> t.is(PigZombie.class) && t.as(PigZombie.class).isAngry();
		
		//Trivial player state checks
		case "issneaking":		return t -> t.isPlayer() && t.asPlayer().isSneaking();
		case "issprinting":		return t -> t.isPlayer() && t.asPlayer().isSprinting();
		case "isblocking":		return t -> t.isPlayer() && t.asPlayer().isBlocking();
		case "ishandraised":	return t -> t.isPlayer() && t.asPlayer().isHandRaised();
		case "issleeping":		return t -> t.isPlayer() && t.asPlayer().isSleeping();
		
		//Trivial location state checks
		case "israining":		return t -> !t.isNull() && t.getLocation().getWorld().hasStorm();
		case "isthundering":	return t -> !t.isNull() && t.getLocation().getWorld().isThundering();
		case "issheltered":		return ConditionFactory::isSheltered;
		
		//Non-trivial state checks
		case "isblock":			return new IsBlock(args);
		case "iswearing":		return new IsWearing(args);
		case "isholding":		return new IsHolding(args);
		case "isaffected":		return new IsAffected(args);
		//TODO: isowner
		
		//Has line of sight
		//Scoreboard
		//Permission
		//Op
		//Gamemode
		//Attribute
	
		//Non-trivial value checks
		case "isvariable":		return new IsVariable(varArgsString);
		case "ishealth":		return new IsHealth(varArgsString);
		case "ishunger":		return new IsHunger(varArgsString);
		case "isexp":			return new IsExp(varArgsString);
		case "islevel":			return new IsLevel(varArgsString);
		
		default:				return null;
		}
	}
	
	private static boolean isSheltered(Target target) {
		Location loc = target.getLocation();
		return loc != null && loc.getBlockY() < loc.getWorld().getHighestBlockYAt(loc);
	}
	
}
