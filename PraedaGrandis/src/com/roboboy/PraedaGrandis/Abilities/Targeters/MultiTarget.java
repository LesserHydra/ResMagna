package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.LivingEntity;

public class MultiTarget implements Iterable<Target>
{
	private List<Target> targets = new LinkedList<>();
	
	public MultiTarget(List<LivingEntity> targetList, Target target) {
		for (LivingEntity e : targetList) {
			targets.add(target.target(e));
		}
	}
	
	public MultiTarget(LivingEntity singleTarget, Target target) {
		targets.add(target.target(singleTarget));
	}

	@Override
	public Iterator<Target> iterator() {
		return targets.iterator();
	}
}
