package com.roboboy.PraedaGrandis.Configuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

/**
 * Loads and stores all GrandAbilities
 * @author roboboy
 */
public class GrandAbilityHandler extends MultiConfig
{
	private static GrandAbilityHandler instance = new GrandAbilityHandler();
	private GrandAbilityHandler() {}
	public static GrandAbilityHandler getInstance() {
		return instance;
	}
	
	private Map<String, GrandAbility> customAbilities = new HashMap<String, GrandAbility>();
	private List< Pair<FunctionRunner, String> > requestList = new LinkedList<>();
	
	private boolean fullyLoaded = false;
	
	public void requestFunction(FunctionRunner requester, String requestName) {
		requestName = requestName.toLowerCase();
		if (!fullyLoaded) {
			requestList.add(new ImmutablePair<FunctionRunner, String>(requester, requestName));
			return;
		}
		handleRequest(requester, requestName);
	}

	/**
	 * Reloads the ability configuration files.<br>
	 * <br>
	 * <strong>The ConfigManager must be reloaded first.</strong>
	 */
	@Override
	public void reload() {
		fullyLoaded = false;
		customAbilities.clear();
		super.reload(ConfigManager.getInstance().getAbilityFolder());
		
		//Finish custom abilities after all GrandAbilities are constructed
		fullyLoaded = true;
		handleRequests();
	}

	/**
	 * Loads GrandAbilities from a single configuration file
	 */
	@Override
	protected void loadConfig(FileConfiguration config) {
		for (String key : config.getKeys(false)) {
			ConfigurationSection abilityConfig = config.getConfigurationSection(key);
			GrandAbility result = GrandAbilityFactory.build(abilityConfig);
			customAbilities.put(key.toLowerCase(), result);
		}
	}
	
	private void handleRequests() {
		for (Pair<FunctionRunner, String> request : requestList) {
			handleRequest(request.getLeft(), request.getRight());
		}
		requestList.clear();
	}
	
	private void handleRequest(FunctionRunner requester, String requestName) {
		GrandAbility result = customAbilities.get(requestName);
		requester.returnRequest(result);
		if (result == null) GrandLogger.log("Requested custom ability not found: " + requestName, LogType.CONFIG_ERRORS);
	}
	
}