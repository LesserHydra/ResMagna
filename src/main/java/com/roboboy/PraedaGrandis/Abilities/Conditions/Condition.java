package com.roboboy.PraedaGrandis.Abilities.Conditions;

import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

public interface Condition {
	
	boolean test(Target target);
	
	default Condition negate() { return target -> !test(target); }
	
}
