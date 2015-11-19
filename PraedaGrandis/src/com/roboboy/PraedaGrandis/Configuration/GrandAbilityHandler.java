package com.roboboy.PraedaGrandis.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import com.roboboy.PraedaGrandis.PraedaGrandis;

/**
 * Loads all custom ability configuration files.
 * 
 * @author roboboy
 *
 */
public class GrandAbilityHandler extends MultiConfig
{
	public Map<String, GrandAbility> customAbilities = new HashMap<String, GrandAbility>();

	public GrandAbilityHandler(PraedaGrandis p, File folder){
		super(p, folder);
	}
	
	@Override
	public void reload()
	{
		customAbilities.clear();
		super.reload();
	}
	
	@Override
	protected void loadConfig(FileConfiguration config)
	{
		for (String key : config.getKeys(false))
		{
			ConfigurationSection abilityConfig = config.getConfigurationSection(key);
			customAbilities.put(key.toLowerCase(), new GrandAbility(abilityConfig));
		}
	}
}
