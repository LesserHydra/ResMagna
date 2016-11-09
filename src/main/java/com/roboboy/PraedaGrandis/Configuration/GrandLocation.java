package com.roboboy.PraedaGrandis.Configuration;

import com.roboboy.PraedaGrandis.Targeters.Target;
import com.roboboy.PraedaGrandis.Targeters.Targeter;
import com.roboboy.PraedaGrandis.Targeters.TargeterFactory;
import com.roboboy.PraedaGrandis.Targeters.Targeters;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.util.StringTools;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrandLocation
{
	//(?:(@\w+\s*(?:\((\$[\d]+)\))?)\s*)?(?:\|\s*)?([^<>\|\n]+)?(?:\s*\|\s*)?(?:<([*\w]+)>)?
	static private final Pattern formatPattern = Pattern.compile("(?:(@\\w+\\s*(?:\\((\\$[\\d]+)\\))?)\\s*)?(?:\\|\\s*)?([^<>\\|\\n]+)?(?:\\s*\\|\\s*)?(?:<([*\\w]+)>)?");
	//([~a-zA-Z]+=?)([+-]?[\d\.]+)?
	static private final Pattern componentPattern = Pattern.compile("([~a-zA-Z]+=?)([+-]?[\\d\\.]+)?");
	
	private final Targeter locationTargeter;
	private final List<Pair<LocationComponentType, Double>> componentList;
	private final Dimension dimension;
	
	public GrandLocation() {
		locationTargeter = Targeters.CURRENT;
		componentList = Collections.emptyList();
		dimension = Dimension.SAME;
	}
	
	public GrandLocation(Targeter locationTargeter, List<Pair<LocationComponentType, Double>> componentList, Dimension dimension) {
		this.locationTargeter = locationTargeter;
		this.componentList = componentList;
		this.dimension = dimension;
	}

	public Location calculate(Target mainTarget) {
		//Get new target from targeter
		Target newTarget = locationTargeter.getRandomTarget(mainTarget);
		if (newTarget.getLocation() == null) return null;
		
		//Modify according to components
		Location finalLoc = newTarget.getLocation();
		for (Pair<LocationComponentType, Double> componentPair : componentList) {
			componentPair.getLeft().modify(finalLoc, componentPair.getRight());
		}
		
		//Set world
		finalLoc.setWorld(dimension.getWorld(finalLoc.getWorld()));
		
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
		if (componentsString != null) {
			Matcher componentMatcher = componentPattern.matcher(componentsString);
			while (componentMatcher.find()) {
				addComponentToList(componentMatcher.group(1), componentMatcher.group(2), componentList);
			}
		}
		
		//Get dimension
		Dimension dimension = Dimension.fromString(lineMatcher.group(4));
		
		//Return result
		return new GrandLocation(locationTargeter, componentList, dimension);
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
		if (!StringTools.isFloat(componentDoubleString)) {
			GrandLogger.log("Invalid location component modifier: " + componentDoubleString, LogType.CONFIG_ERRORS);
			GrandLogger.log("Expected a floating point value.", LogType.CONFIG_ERRORS);
			return;
		}
		Double componentDouble = Double.parseDouble(componentDoubleString);
		
		//Add component to componentList
		componentList.add(new ImmutablePair<>(componentType, componentDouble));
	}
	
}
