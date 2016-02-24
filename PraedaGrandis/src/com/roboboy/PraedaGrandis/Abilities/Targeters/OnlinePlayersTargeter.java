package com.roboboy.PraedaGrandis.Abilities.Targeters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OnlinePlayersTargeter extends Targeter
{	
	@Override
	public List<Target> getTargets(Target currentTarget)
	{
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		List<Target> results = new ArrayList<>(onlinePlayers.size());
		
		//Construct targets
		for (Player player : onlinePlayers) {
			results.add(currentTarget.target(new TargetEntity(player)));
		}
		
		return results;
	}

}
