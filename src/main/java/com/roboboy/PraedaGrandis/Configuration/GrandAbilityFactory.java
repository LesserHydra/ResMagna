package com.roboboy.PraedaGrandis.Configuration;

import java.util.function.Predicate;

import com.roboboy.PraedaGrandis.Configuration.Function.GrandFunction;
import com.roboboy.PraedaGrandis.Configuration.Function.LineFactory;
import org.bukkit.configuration.ConfigurationSection;
import com.roboboy.PraedaGrandis.Abilities.Conditions.Condition;
import com.roboboy.PraedaGrandis.Abilities.Conditions.ConditionFactory;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

public class GrandAbilityFactory {
	
	public static GrandAbility build(ConfigurationSection abilitySection) {
		Predicate<Target> conditions = x -> true;
		
		//Conditions (if)
		for (String s : abilitySection.getStringList("if")) {
			Condition c = ConditionFactory.build(s);
			if (c != null) conditions = conditions.and(c::check);
		}
		
		//Abilities (then)
		GrandFunction thenFunction = LineFactory.parseAndLinkLines(abilitySection.getStringList("then"));
		
		//Otherwise (else)
		GrandFunction elseFunction = LineFactory.parseAndLinkLines(abilitySection.getStringList("else"));
		
		return new GrandAbility(conditions, thenFunction, elseFunction);
	}

}
