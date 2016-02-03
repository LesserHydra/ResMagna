package com.roboboy.PraedaGrandis.Configuration;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import com.roboboy.PraedaGrandis.Abilities.Ability;
import com.roboboy.PraedaGrandis.Abilities.AbilityFactory;
import com.roboboy.PraedaGrandis.Abilities.DelayAbility;
import com.roboboy.PraedaGrandis.Abilities.Conditions.Condition;
import com.roboboy.PraedaGrandis.Abilities.Conditions.ConditionFactory;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

/**
 * Describes a custom ability.
 * 
 * @author roboboy
 *
 */
public class GrandAbility
{
	List<Condition> conditions = new ArrayList<Condition>();
	List<Ability> abilities = new ArrayList<Ability>();
	List<Ability> elseAbilities = new ArrayList<Ability>();
	
	public GrandAbility(ConfigurationSection abilitySection)
	{	
		//Conditions (if)
		for (String s : abilitySection.getStringList("if")) {
			Condition c = ConditionFactory.build(s);
			if (c != null) conditions.add(c);
		}
		
		//Abilities (then)
		DelayAbility currentDelayAbility = null;
		for (String s : abilitySection.getStringList("then")) {
			currentDelayAbility = constructAbility(s, currentDelayAbility, abilities);
		}
		
		//Otherwise (else)
		currentDelayAbility = null;
		for (String s : abilitySection.getStringList("else")) {
			currentDelayAbility = constructAbility(s, currentDelayAbility, elseAbilities);
		}
	}
	
	private DelayAbility constructAbility(String abilityString, DelayAbility delayAbility, List<Ability> addToList) {
		//Construct ability
		Ability ability = AbilityFactory.build(abilityString);
		if (ability == null) return delayAbility;
		
		//Add to delay ability if exists
		if (delayAbility != null) delayAbility.addAbility(ability);
		//Otherwise, add to ability list
		else addToList.add(ability);
		
		//Following abilities are assigned to this DelayAbility
		if (ability instanceof DelayAbility) delayAbility = (DelayAbility) ability;
		return delayAbility;
	}

	public void run(Target target)
	{
		//Check conditions
		boolean run = true;
		for (Condition c : conditions) {
			run = run && c.check(target); //AND results together
		}
		
		if (run) { //Run abilities
			for (Ability a : abilities) {
				a.activate(target);
			}
		}
		else { //Run elseAbilities
			for (Ability a : elseAbilities) {
				a.activate(target);
			}
		}
	}
}
