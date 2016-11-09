package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

public interface Condition {
	
	/*public final boolean check(Target target) {
		//boolean result = true;
		for (Target t : targeter.getTargets(target)) {
			if ( !(not ^ test(t)) ) return false;
			//result = result && (not ^ test(t.get())); //(not ? !result : result)
		}
		//return result;
		return true;
	}*/
	
	boolean test(Target target);
	
	default Condition negate() { return target -> !test(target); }
	
}
