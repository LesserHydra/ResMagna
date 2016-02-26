package com.roboboy.PraedaGrandis.Configuration;

import java.util.Collection;
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

public class BlockMask
{
	//\s*(?:(\!)\s*)?(?:([<>])\s*)?([\w\s]+(?<!\s))(?:\s*\:\s*(\!)?\s*(\d+)(?:\s*\-\s*(\d+))?)?\s*
	private static final Pattern componentPattern = Pattern.compile("\\s*(?:(\\!)\\s*)?(?:([<>])\\s*)?([\\w\\s]+(?<!\\s))(?:\\s*\\:\\s*(\\!)?\\s*(\\d+)(?:\\s*\\-\\s*(\\d+))?)?\\s*");
	
	private final List<MaskDetails> details;
	
	private BlockMask(List<MaskDetails> details) {
		this.details = details;
	}

	public List<Location> matches(Collection<Location> locationList) {
		List<Location> results = new LinkedList<>(); 
		for (Location blockLoc : locationList) {
			if (matches(blockLoc.getBlock())) results.add(blockLoc);
		}
		return results;
	}

	public boolean matches(Block block) {
		if (details.isEmpty()) return true;
		boolean result = details.get(0).isNegator();
		for (MaskDetails nextTest : details) {
			result = nextTest.test(block, result);
		}
		return result;
	}

	public static BlockMask buildFromString(String string) {
		//Could use find instead, but this way is nicer for debugging
		String[] maskStrings = string.replaceAll("[\\(\\)]", "").split("[,;]"); //TODO: replaceall is temp
		List<MaskDetails> masks = new LinkedList<>();
		for (String maskString : maskStrings) {
			Matcher matcher = componentPattern.matcher(maskString);
			//Error message is expanded in BlockArguments
			if (!matcher.matches()) {
				GrandLogger.log("Invalid format for block mask component: " + maskString, LogType.CONFIG_ERRORS);
				return null;
			}
			//Get component, check for validity
			MaskDetails component = parseComponent(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5), matcher.group(6));
			if (component == null) return null;
			
			//Succesfully constructed, add to randomizer
			masks.add(component);
		}
		
		//Success
		return new BlockMask(masks);
	}

	private static MaskDetails parseComponent(String negatorString, String positionString, String materialString,
			String dataNegatedString, String minDataString, String maxDataString) {
		//IsNegator
		boolean negator = ("!".equals(negatorString));
		
		//Position
		BlockFace position = BlockFace.SELF;
		if (">".equals(positionString)) position = BlockFace.DOWN;
		else if ("<".equals(positionString)) position = BlockFace.UP;
		
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
			if (!Tools.isInteger(minDataString)) {
				GrandLogger.log("Invalid block data: " + minDataString, LogType.CONFIG_ERRORS);
				return null;
			}
			minData = Byte.parseByte(minDataString);
		}
		
		//Maximum data
		Byte maxData = null;
		if (maxDataString != null) {
			if (!Tools.isInteger(maxDataString)) {
				GrandLogger.log("Invalid block data: " + maxDataString, LogType.CONFIG_ERRORS);
				return null;
			}
			maxData = Byte.parseByte(maxDataString);
		}
		
		//Return result
		return new MaskDetails(negator, position, material, dataNegated, minData, maxData);
	}
	
	public static BlockMask getEmpty() {
		return new BlockMask(new LinkedList<MaskDetails>());
	}

	private static class MaskDetails {
		private final boolean negator;
		private final BlockFace positionMod;
		
		private final Material material;
		private final boolean dataNegated;
		private final Byte minData;
		private final Byte maxData;
		
		private MaskDetails(boolean negator, BlockFace positionMod, Material material, boolean dataNegated, Byte minData, Byte maxData) {
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
