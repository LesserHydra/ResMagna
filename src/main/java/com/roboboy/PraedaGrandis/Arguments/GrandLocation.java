package com.roboboy.PraedaGrandis.Arguments;

import com.roboboy.PraedaGrandis.Configuration.GroupingParser;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrandLocation {
	
	//(?:(@\w+\s*(?:\((\$[\d]+)\))?)\s*)?(?:\|\s*)?([^<>\|\n]+)?(?:\s*\|\s*)?(?:<([*\w]+)>)?
	static private final Pattern formatPattern = Pattern.compile("(?:(@\\w+\\s*(?:\\((\\$[\\d]+)\\))?)\\s*)?(?:\\|\\s*)?([^<>\\|\\n]+)?(?:\\s*\\|\\s*)?(?:<([*\\w]+)>)?");
	//([~a-zA-Z]+=?)([+-]?[\d\.]+)?
	static private final Pattern componentPattern = Pattern.compile("([~a-zA-Z]+=?)([+-]?[\\d\\.]+)?");
	
	private final Targeter locationTargeter;
	private final List<Pair<ComponentType, Double>> componentList;
	private final Dimension dimension;
	
	public GrandLocation() {
		locationTargeter = Targeters.CURRENT;
		componentList = Collections.emptyList();
		dimension = Dimension.SAME;
	}
	
	public GrandLocation(Targeter locationTargeter, List<Pair<ComponentType, Double>> componentList, Dimension dimension) {
		this.locationTargeter = locationTargeter;
		this.componentList = componentList;
		this.dimension = dimension;
	}

	public Location calculate(Target mainTarget) {
		//Get new target from targeter
		Target newTarget = locationTargeter.getRandomTarget(mainTarget);
		if (newTarget.asLocation() == null) return null;
		
		//Modify according to components
		Location finalLoc = newTarget.asLocation();
		for (Pair<ComponentType, Double> componentPair : componentList) {
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
	
	@Nullable
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
		List<Pair<ComponentType, Double>> componentList = new LinkedList<>();
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
	
	private static void addComponentToList(String componentTypeString, String componentDoubleString, List<Pair<ComponentType, Double>> componentList) {
		//Get component type
		ComponentType componentType = ComponentType.fromString(componentTypeString);
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
	
	private enum ComponentType {
		
		X_ABSOLUTE			("X=") {
			@Override public void modify(Location location, double amount) {
				location.setX(amount);
		}},
		Y_ABSOLUTE			("Y=") {
			@Override public void modify(Location location, double amount) {
				location.setY(amount);
		}},
		Z_ABSOLUTE			("Z=") {
			@Override public void modify(Location location, double amount) {
				location.setZ(amount);
		}},
		X_RELATIVE			("X") {
			@Override public void modify(Location location, double amount) {
				location.add(amount, 0D, 0D);
		}},
		Y_RELATIVE			("Y") {
			@Override public void modify(Location location, double amount) {
				location.add(0D, amount, 0D);
		}},
		Z_RELATIVE			("Z") {
			@Override public void modify(Location location, double amount) {
				location.add(0D, 0D, amount);
		}},
		FACING				("F") {
			@Override public void modify(Location location, double amount) {
				Vector forward = location.getDirection();
				location.add(forward.multiply(amount));
		}},
		FACING_UP			("U") {
			@Override public void modify(Location location, double amount) {
				Vector forward = location.getDirection();
				Vector right = new Vector(-forward.getZ(), 0, forward.getX());
				Vector up = right.crossProduct(forward);
				up.normalize();
				location.add(up.multiply(amount));
		}},
		FACING_RIGHT		("R") {
			@Override public void modify(Location location, double amount) {
				Vector forward = location.getDirection();
				Vector right = new Vector(-forward.getZ(), 0, forward.getX());
				right.normalize().multiply(amount);
				location.add(right.multiply(amount));
		}},
		FACING_HORIZONTAL	("FH") {
			@Override public void modify(Location location, double amount) {
				Vector hForward = location.getDirection().setY(0);
				hForward.normalize();
				location.add(hForward.multiply(amount));
		}};
		
		private final String identifier;
		
		ComponentType(String identifier) {
			this.identifier = identifier;
		}
		
		abstract void modify(Location location, double amount);
		
		@Nullable
		static ComponentType fromString(String typeString) {
			typeString = typeString.toUpperCase();
			for (ComponentType type : ComponentType.values()) {
				if (type.identifier.equals(typeString)) return type;
			}
			return null;
		}
		
	}
	
}
