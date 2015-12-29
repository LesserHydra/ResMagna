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
		DelayAbility currentDelayAbility = null;
		
		//Conditions (if)
		for (String s : abilitySection.getStringList("if"))
		{
			Condition c = ConditionFactory.build(s);
			if (c != null) conditions.add(c);
		}
		
		//Abilities (then)
		for (String s : abilitySection.getStringList("then"))
		{
			//Construct ability
			Ability a = AbilityFactory.build(s);
			if (a == null) continue;
			
			//Add to delay ability if exists
			if (currentDelayAbility != null) currentDelayAbility.addAbility(a);
			//Otherwise, add to ability list
			else abilities.add(a);
			
			//Following abilities are assigned to this DelayAbility
			if (a instanceof DelayAbility) {
				currentDelayAbility = (DelayAbility) a;
			}
		}
		
		//Otherwise (else)
		for (String s : abilitySection.getStringList("else"))
		{
			Ability a = AbilityFactory.build(s);
			if (a != null) elseAbilities.add(a);
		}
	}
	
	public void run(Target target) //TODO: Add GrandLocation argument.
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
