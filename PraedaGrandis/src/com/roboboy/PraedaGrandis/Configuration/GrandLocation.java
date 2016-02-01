package com.roboboy.PraedaGrandis.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Abilities.Targeters.CurrentTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class GrandLocation
{
	//\((?:(@\w+(?:\s*(?:\(.*\)))?)\s*\|)?\s*(.+)?\)
	static private final Pattern formatPattern = Pattern.compile("\\((?:(@\\w+(?:\\s*(?:\\(.*\\)))?)\\s*\\|)?\\s*(.+)?\\)");
	//([~a-zA-Z]+=?)([+-]?[\d\.]+)?
	static private final Pattern componentPattern = Pattern.compile("([~a-zA-Z]+=?)([+-]?[\\d\\.]+)?");
	
	private final Targeter locationTargeter;
	private final List<Pair<LocationComponentType, Double>> componentList = new LinkedList<>();
	
	public GrandLocation() {
		locationTargeter = new CurrentTargeter();
	}
	
	/**
	 * Constructs a GrandLocation from the given string. The string must have already been checked for validity.<br>
	 * <pre>Format is as follows:
	 * Enclosed in brackets ("()")
	 * Three components:
	 *  Either a tilda ("~"), denoting relativity, or a floating value
	 *  Float may be prefixed with a tilda, denoting relativity</pre>
	 * @param locString String to construct from
	 */
	public GrandLocation(String locString) {
		//Match
		Matcher lineMatcher = formatPattern.matcher(locString);
		if (!lineMatcher.matches()) {
			PraedaGrandis.plugin.logger.log("Invalid location format:", LogType.CONFIG_ERRORS);
			PraedaGrandis.plugin.logger.log("  " + locString, LogType.CONFIG_ERRORS);
			locationTargeter = new CurrentTargeter();
			return;
		}
		
		//Get Targeter, or default if none exist
		locationTargeter = TargeterFactory.build(lineMatcher.group(1));
		
		//Get components, if exist
		String componentsString = lineMatcher.group(2);
		if (componentsString == null) return;
		Matcher componentMatcher = componentPattern.matcher(componentsString);
		while (componentMatcher.find()) {
			//Get component type
			String componentTypeString = componentMatcher.group(1);
			LocationComponentType componentType = LocationComponentType.fromString(componentTypeString);
			if (componentType == null) {
				PraedaGrandis.plugin.logger.log("Invalid location component type: " + componentTypeString, LogType.CONFIG_ERRORS);
				continue;
			}
			
			//Get modifier
			String componentDoubleString = componentMatcher.group(2);
			if (componentDoubleString == null) continue;
			if (!Tools.isFloat(componentDoubleString)) {
				PraedaGrandis.plugin.logger.log("Invalid location component modifier: " + componentDoubleString, LogType.CONFIG_ERRORS);
				PraedaGrandis.plugin.logger.log("Expected a floating point value.", LogType.CONFIG_ERRORS);
				continue;
			}
			Double componentDouble = Double.parseDouble(componentDoubleString);
			
			componentList.add(new ImmutablePair<>(componentType, componentDouble));
		}
	}
	
	public Location calculate(Target mainTarget) {
		//Get new target from targeter
		Target newTarget = locationTargeter.getRandomTarget(mainTarget);
		if (newTarget == null || newTarget.get() == null) return null;
		
		//Modify according to components
		Location finalLoc = newTarget.get().getLocation();
		for (Pair<LocationComponentType, Double> componentPair : componentList) {
			componentPair.getLeft().modify(finalLoc, componentPair.getRight());
		}
		
		//Return result
		return finalLoc;
	}
}
