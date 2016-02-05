package com.roboboy.PraedaGrandis.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Abilities.Targeters.CurrentTargeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Abilities.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class GrandLocation
{
	//(?:(@\w+\s*(?:\((\$[\d]+)\))?)\s*)?(?:\|\s*)?(.+)?
	static private final Pattern formatPattern = Pattern.compile("(?:(@\\w+\\s*(?:\\((\\$[\\d]+)\\))?)\\s*)?(?:\\|\\s*)?(.+)?");
	//([~a-zA-Z]+=?)([+-]?[\d\.]+)?
	static private final Pattern componentPattern = Pattern.compile("([~a-zA-Z]+=?)([+-]?[\\d\\.]+)?");
	
	private final Targeter locationTargeter;
	private final List<Pair<LocationComponentType, Double>> componentList = new LinkedList<>();
	
	public GrandLocation() {
		locationTargeter = new CurrentTargeter();
	}
	
	public GrandLocation(String locString) {
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(locString);
		String simplifiedString = groupParser.getSimplifiedString();
		
		//Match
		Matcher lineMatcher = formatPattern.matcher(simplifiedString);
		if (!lineMatcher.matches()) {
			GrandLogger.log("Invalid location format:", LogType.CONFIG_ERRORS);
			GrandLogger.log("  " + locString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  Simplified: " + simplifiedString, LogType.CONFIG_ERRORS);
			locationTargeter = new CurrentTargeter();
			return;
		}
		
		//Get Targeter, or default if none exist
		String targeterString = lineMatcher.group(1);
		String targeterArgumentsGroupID = lineMatcher.group(2);
		targeterString = groupParser.readdGrouping(targeterString, targeterArgumentsGroupID);
		locationTargeter = TargeterFactory.build(targeterString);
		
		//Get components, if exist
		String componentsString = lineMatcher.group(3);
		if (componentsString == null) return;
		Matcher componentMatcher = componentPattern.matcher(componentsString);
		while (componentMatcher.find()) {
			//Get component type
			String componentTypeString = componentMatcher.group(1);
			LocationComponentType componentType = LocationComponentType.fromString(componentTypeString);
			if (componentType == null) {
				GrandLogger.log("Invalid location component type: " + componentTypeString, LogType.CONFIG_ERRORS);
				continue;
			}
			
			//Get modifier
			String componentDoubleString = componentMatcher.group(2);
			if (componentDoubleString == null) continue;
			if (!Tools.isFloat(componentDoubleString)) {
				GrandLogger.log("Invalid location component modifier: " + componentDoubleString, LogType.CONFIG_ERRORS);
				GrandLogger.log("Expected a floating point value.", LogType.CONFIG_ERRORS);
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
	
	public Vector calculateDirection(Target mainTarget, Location fromLocation) {
		Location toLocation = calculate(mainTarget);
		return toLocation.toVector().subtract(fromLocation.toVector());
	}
}
