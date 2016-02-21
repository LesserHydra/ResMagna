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
	private final List<Pair<LocationComponentType, Double>> componentList;
	
	public GrandLocation() {
		locationTargeter = new CurrentTargeter();
		componentList = new LinkedList<>();
	}
	
	public GrandLocation(Targeter locationTargeter, List<Pair<LocationComponentType, Double>> componentList) {
		this.locationTargeter = locationTargeter;
		this.componentList = componentList;
	}

	public Location calculate(Target mainTarget) {
		//Get new target from targeter
		Target newTarget = locationTargeter.getRandomTarget(mainTarget);
		if (newTarget == null || newTarget.getLocation() == null) return null;
		
		//Modify according to components
		Location finalLoc = newTarget.getLocation();
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
	
	public static GrandLocation buildFromString(String locString) {
		//Remove groupings
		GroupingParser groupParser = new GroupingParser(locString);
		String simplifiedString = groupParser.getSimplifiedString();
		
		//Match
		Matcher lineMatcher = formatPattern.matcher(simplifiedString);
		if (!lineMatcher.matches()) return null;
		
		//Get Targeter, or default if none exist
		String targeterString = lineMatcher.group(1);
		String targeterArgumentsGroupID = lineMatcher.group(2);
		targeterString = groupParser.readdGrouping(targeterString, targeterArgumentsGroupID);
		Targeter locationTargeter = TargeterFactory.build(targeterString);
		
		//Get components, if exist
		List<Pair<LocationComponentType, Double>> componentList = new LinkedList<>();
		String componentsString = lineMatcher.group(3);
		if (componentsString == null) return new GrandLocation(locationTargeter, componentList);
		Matcher componentMatcher = componentPattern.matcher(componentsString);
		while (componentMatcher.find()) {
			addComponentToList(componentMatcher.group(1), componentMatcher.group(2), componentList);
		}
		
		return new GrandLocation(locationTargeter, componentList);
	}
	
	private static void addComponentToList(String componentTypeString, String componentDoubleString, List<Pair<LocationComponentType, Double>> componentList) {
		//Get component type
		LocationComponentType componentType = LocationComponentType.fromString(componentTypeString);
		if (componentType == null) {
			GrandLogger.log("Invalid location component type: " + componentTypeString, LogType.CONFIG_ERRORS);
			return;
		}
		
		//Get modifier
		if (componentDoubleString == null) return;
		if (!Tools.isFloat(componentDoubleString)) {
			GrandLogger.log("Invalid location component modifier: " + componentDoubleString, LogType.CONFIG_ERRORS);
			GrandLogger.log("Expected a floating point value.", LogType.CONFIG_ERRORS);
			return;
		}
		Double componentDouble = Double.parseDouble(componentDoubleString);
		
		//Add component to componentList
		componentList.add(new ImmutablePair<>(componentType, componentDouble));
	}
	
}
