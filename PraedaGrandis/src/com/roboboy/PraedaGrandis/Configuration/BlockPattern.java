package com.roboboy.PraedaGrandis.Configuration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.Tools;
import com.roboboy.PraedaGrandis.Logging.GrandLogger;
import com.roboboy.PraedaGrandis.Logging.LogType;

public class BlockPattern
{
	//\s*(?:([\d\.]+)\s*\?\s*)?([\w\s]+(?<!\s))(?:\s*\:\s*(\d+))?\s*
	private static final Pattern blockComponentPattern = Pattern.compile("\\s*(?:([\\d\\.]+)\\s*\\?\\s*)?([\\w\\s]+(?<!\\s))(?:\\s*\\:\\s*(\\d+))?\\s*");
	
	private final RandomCollection<BlockConstruct> blockCollection;
	
	private BlockPattern(RandomCollection<BlockConstruct> blockCollection) {
		this.blockCollection = blockCollection;
	}
	
	@SuppressWarnings("deprecation")
	public void replace(List<Location> locationList, BlockMask replacePattern) {
		for (Location blockLocation : locationList) {
			Block block = blockLocation.getBlock();
			if (!replacePattern.matches(block)) continue;
			
			BlockConstruct replaceBlock = blockCollection.next();
			block.setType(replaceBlock.getMaterial());
			block.setData(replaceBlock.getData());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void makeGhost(List<Location> locationList, BlockMask replacePattern) {
		for (Location blockLocation : locationList) {
			Block block = blockLocation.getBlock();
			if (!replacePattern.matches(block)) continue;
			
			BlockConstruct replaceBlock = blockCollection.next();
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendBlockChange(blockLocation, replaceBlock.getMaterial(), replaceBlock.getData());
			}
		}
	}

	public static BlockPattern buildFromString(String string) {
		//Could use find instead, but this way is nicer for debugging
		String[] blockStrings = string.replaceAll("[\\(\\)]", "").split("[,;]"); //TODO: Temp
		RandomCollection<BlockConstruct> blockCollection = new RandomCollection<>();
		for (String blockString : blockStrings) {
			Matcher blockMatcher = blockComponentPattern.matcher(blockString);
			//Error message is expanded in BlockArguments
			if (!blockMatcher.matches()) {
				GrandLogger.log("Invalid format for block pattern component: " + blockString, LogType.CONFIG_ERRORS);
				return null;
			}
			//Get block construct, check for validity
			BlockConstruct blockConstruct = parsePattern(blockMatcher.group(2), blockMatcher.group(3));
			if (blockConstruct == null) return null;
			
			//Parse probability
			double weight = 1;
			String weightString = blockMatcher.group(1);
			if (weightString != null) {
				if (!Tools.isFloat(weightString)) {
					GrandLogger.log("Invalid block weight: " + weightString, LogType.CONFIG_ERRORS);
					return null;
				}
				weight = Double.parseDouble(weightString);
			}
			
			//Succesfully constructed, add to randomizer
			blockCollection.add(weight, blockConstruct);
		}
		
		//Success
		return new BlockPattern(blockCollection);
	}

	private static BlockConstruct parsePattern(String materialString, String dataString) {
		//Parse material
		Material material = Tools.parseEnum(materialString, Material.class);
		if (material == null) {
			GrandLogger.log("Invalid block material: " + materialString, LogType.CONFIG_ERRORS);
			return null;
		}
		
		//Parse data
		byte data = 0;
		if (dataString != null) {
			if (!Tools.isInteger(dataString)) {
				GrandLogger.log("Invalid block data: " + dataString, LogType.CONFIG_ERRORS);
				return null;
			}
			data = Byte.parseByte(dataString);
		}
		
		//Return result
		return new BlockConstruct(material, data);
	}
	
	public static BlockPattern getEmpty() {
		return new BlockPattern(new RandomCollection<BlockConstruct>());
	}

	private static class BlockConstruct {
		private final Material material;
		private final byte data;
		
		private BlockConstruct(Material material, byte data) {
			this.material = material;
			this.data = data;
		}
		
		public Material getMaterial() {
			return material;
		}
		
		public byte getData() {
			return data;
		}
	}
	
}
