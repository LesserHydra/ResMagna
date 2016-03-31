package com.roboboy.PraedaGrandis.Configuration;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;
import com.roboboy.bukkitutil.LocationTester;
import com.roboboy.util.StringTools;

public class BlockMask implements LocationTester
{
	//\s*(?:(\!)\s*)?(?:([<>])\s*)?([\w\s]+(?<!\s))(?:\s*\:\s*(\!)?\s*(\d+)(?:\s*\-\s*(\d+))?)?\s*
	private static final Pattern componentPattern = Pattern.compile("\\s*(?:(\\!)\\s*)?(?:([<>])\\s*)?([\\w\\s]+(?<!\\s))(?:\\s*\\:\\s*(\\!)?\\s*(\\d+)(?:\\s*\\-\\s*(\\d+))?)?\\s*");
	
	private final List<MaskComponent> components;
	
	private BlockMask(List<MaskComponent> components) {
		this.components = components;
	}
	
	/**
	 * Checks the given location for a match with this mask.
	 * @param location Location to match
	 * @return True if matches, false otherwise.
	 */
	public boolean testLocation(Location location) {
		Block block = location.getBlock();
		if (components.isEmpty()) return true;
		boolean result = components.get(0).isNegator();
		for (MaskComponent nextTest : components) {
			result = nextTest.test(block, result);
		}
		return result;
	}
	
	/**
	 * Builds a blank BlockMask which will match any block.
	 * @return A blank BlockMask
	 */
	public static BlockMask buildBlank() {
		return new BlockMask(new LinkedList<MaskComponent>());
	}
	
	/**
	 * Builds a BlockMask from a string.
	 * @param string String describing the requested BlockMask
	 * @return BlockMask described by given string, or null if an error was hit
	 */
	public static BlockMask buildFromString(String string) {
		//Could use find instead, but this way is nicer for debugging
		String[] componentStrings = string.replaceAll("[\\(\\)]", "").split("[,;]"); //TODO: replaceall is temp
		List<MaskComponent> components = new LinkedList<>();
		for (String componentString : componentStrings) {
			//Match component
			Matcher matcher = componentPattern.matcher(componentString);
			//Error message is expanded in BlockArguments
			if (!matcher.matches()) {
				GrandLogger.log("Invalid format for block mask component: " + componentString, LogType.CONFIG_ERRORS);
				return null;
			}
			
			//Get component, check for validity
			MaskComponent component = parseComponent(matcher.group(1), matcher.group(2), matcher.group(3),
					matcher.group(4), matcher.group(5), matcher.group(6));
			if (component == null) return null;
			
			//Succesfully constructed, add to randomizer
			components.add(component);
		}
		
		//Success
		return new BlockMask(components);
	}

	private static MaskComponent parseComponent(String negatorString, String positionString, String materialString,
			String dataNegatedString, String minDataString, String maxDataString) {
		//IsNegator
		boolean negator = ("!".equals(negatorString));
		
		//Position
		BlockFace position = parsePosition(positionString);
		
		//Material
		Material material = Tools.parseEnum(materialString, Material.class);
		if (material == null) {
			GrandLogger.log("Invalid block material: " + materialString, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Data check negated
		boolean dataNegated = ("!".equals(dataNegatedString));
		
		//Minimum data
		Byte minData = null;
		if (minDataString != null) {
			if (!StringTools.isInteger(minDataString)) {
				GrandLogger.log("Invalid block data: " + minDataString, LogType.CONFIG_ERRORS);
				return null;
			}
			minData = Byte.parseByte(minDataString);
		}
		
		//Maximum data
		Byte maxData = null;
		if (maxDataString != null) {
			if (!StringTools.isInteger(maxDataString)) {
				GrandLogger.log("Invalid block data: " + maxDataString, LogType.CONFIG_ERRORS);
				return null;
			}
			maxData = Byte.parseByte(maxDataString);
		}
		
		//Return result
		return new MaskComponent(negator, position, material, dataNegated, minData, maxData);
	}

	private static BlockFace parsePosition(String positionString) {
		if (">".equals(positionString)) return BlockFace.DOWN;
		if ("<".equals(positionString)) return BlockFace.UP;
		return BlockFace.SELF;
	}

	private static class MaskComponent
	{
		private final boolean negator;
		private final BlockFace positionMod;
		
		private final Material material;
		private final boolean dataNegated;
		private final Byte minData;
		private final Byte maxData;
		
		private MaskComponent(boolean negator, BlockFace positionMod, Material material, boolean dataNegated, Byte minData, Byte maxData) {
			this.negator = negator;
			this.positionMod = positionMod;
			
			this.material = material;
			this.dataNegated = dataNegated;
			this.minData = minData;
			this.maxData = maxData;
		}
		
		public boolean isNegator() {
			return negator;
		}

		@SuppressWarnings("deprecation")
		public boolean test(Block block, boolean old) {
			block = block.getRelative(positionMod);
			if (block.getType() != material) return old;
			if (!checkData(block.getData())) return old;
			return !negator;
		}
		
		private boolean checkData(Byte blockData) {
			if (maxData != null) return (blockData >= minData && blockData <= maxData)^dataNegated;
			if (minData != null) return (blockData == minData)^dataNegated;
			return !dataNegated;
		}
	}
	
}
