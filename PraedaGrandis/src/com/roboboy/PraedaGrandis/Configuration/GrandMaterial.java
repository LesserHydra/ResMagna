package com.roboboy.PraedaGrandis.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Material;

public class GrandMaterial implements Iterable<Material>
{
	List<Material> materials = new ArrayList<Material>();
	
	public GrandMaterial(String materialString)
	{
		//"STATIONARY_LAVA", "(STATIONARY_WATER FLOWING_WATER)", "(STATIONARY_WATER, FLOWING_WATER)", ect...
		for (String s : materialString.replace("(", "").replace(")", "").replace(",", "").split(" ")) {
			materials.add(Material.matchMaterial(s));
		}
	}
	
	public boolean contains(Material m){
		return materials.contains(m);
	}

	@Override
	public Iterator<Material> iterator() {
		return materials.iterator();
	}
}
