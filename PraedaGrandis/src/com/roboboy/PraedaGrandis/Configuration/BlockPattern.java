package com.roboboy.PraedaGrandis.Configuration;

import java.util.Collection;
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

	public boolean matches(Collection<Block> toCheck) {
		for (Block block : toCheck) {
			if (!matches(block)) return false;
		}
		return true;
	}

	public boolean matches(Block block) {
		if (blockCollection.isEmpty()) return true;
		for (BlockConstruct nextTest : blockCollection) {
			if (nextTest.matches(block)) return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void replace(List<Location> locationList, BlockPattern replacePattern) {
		for (Location blockLocation : locationList) {
			Block block = blockLocation.getBlock();
			if (!replacePattern.matches(block)) continue;
			
			BlockConstruct replaceBlock = blockCollection.next();
			block.setType(replaceBlock.getMaterial());
			block.setData(replaceBlock.getData());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void makeGhost(List<Location> locationList, BlockPattern replacePattern) {
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
		Byte data = null;
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
		private final Byte data;
		
		private BlockConstruct(Material material, Byte data) {
			this.material = material;
			this.data = data;
		}
		
		@SuppressWarnings("deprecation")
		public boolean matches(Block block) {
			if (block.getType() != material) return false;
			if (data != null && data.compareTo(block.getData()) != 0) return false;
			return true;
		}
		
		public Material getMaterial() {
			return material;
		}
		
		public Byte getData() {
			return data;
		}
	}
	
}
