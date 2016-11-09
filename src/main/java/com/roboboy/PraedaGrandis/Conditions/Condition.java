package com.roboboy.PraedaGrandis.Conditions;

import com.roboboy.PraedaGrandis.Targeters.Target;

public interface Condition {
	
	boolean test(Target target);
	
	default Condition negate() { return target -> !test(target); }
	
}
