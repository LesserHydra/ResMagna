package com.lesserhydra.resmagna.configuration;

import com.comphenix.attribute.Attributes;
import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;
import com.google.common.primitives.Longs;
import com.lesserhydra.resmagna.AbilityTimer;
import com.lesserhydra.resmagna.activator.ActivatorFactory;
import com.lesserhydra.resmagna.activator.ActivatorLine;
import com.lesserhydra.resmagna.activator.ActivatorType;
import com.lesserhydra.resmagna.arguments.ItemSlotType;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import com.lesserhydra.resmagna.targeters.Target;
import com.lesserhydra.util.StringTools;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GrandItem {
	
	private static final String STORAGE = "ResMagna";
	private static final String STORAGE_NAME = "Name";
	private static final String STORAGE_UUID_LEAST = "UUIDLeast";
	private static final String STORAGE_UUID_MOST = "UUIDMost";
	private static final String STORAGE_HASH = "Hash";
	
	private final String name;
	
	private final String displayName;
	private final List<String> lore;
	
	private final Material type;
	private final short durability;
	private final int amount;
	private final Color leatherColor;
	
	private final Map<Enchantment, Integer> enchants;
	private final List<GrandAttribute> attributes;
	
	private final boolean persistant;
	private final boolean placeable;
	
	private final boolean unbreakable;
	private final boolean hideEnchants;
	private final boolean hideAttributes;
	private final boolean hideUnbreakable;
	
	private final boolean updateName;
	private final boolean updateDurability;
	private final boolean updateAmount;
	private final boolean updateEnchantments;
	
	private final List<ActivatorLine> abilities;
	private final List<AbilityTimer> timers;
	
	private final long hash;
	
	//TODO: Make factory function?
	GrandItem(ConfigurationSection itemConfig) {
		this.name = itemConfig.getName().toLowerCase();
		
		this.displayName = itemConfig.getString("display", "").replace('&', ChatColor.COLOR_CHAR);
		this.lore = itemConfig.getStringList("lore").stream()
				.map(loreString -> loreString.replace('&', ChatColor.COLOR_CHAR))
				.collect(Collectors.toList());
		
		this.type = parseMaterial(itemConfig.getString("type", "stone"), itemConfig.getName());
		this.durability = (short) itemConfig.getInt("durability", 0);
		this.amount = itemConfig.getInt("amount", 1);
		this.leatherColor = parseLeatherColor(itemConfig.getConfigurationSection("leatherColor"), type, itemConfig.getName());
		
		this.enchants = parseEnchantments(itemConfig.getStringList("enchantments"), itemConfig.getName());
		this.attributes = parseAttributes(itemConfig.getStringList("attributes"), itemConfig.getName());
		
		this.persistant = itemConfig.getBoolean("options.persistant", false);
		this.placeable = itemConfig.getBoolean("options.placeable", false);
		
		this.unbreakable = itemConfig.getBoolean("options.unbreakable", false);
		this.hideUnbreakable = itemConfig.getBoolean("options.hideUnbreakable", false);
		this.hideEnchants = itemConfig.getBoolean("options.hideEnchantments", false);
		this.hideAttributes = itemConfig.getBoolean("options.hideAttributes", false);
		this.updateName = itemConfig.getBoolean("options.fixName", false);
		this.updateDurability = itemConfig.getBoolean("options.fixDurability", false);
		this.updateAmount = itemConfig.getBoolean("options.fixAmount", false);
		this.updateEnchantments = itemConfig.getBoolean("options.fixEnchantments", false);
		
		//Abilities
		this.abilities = new ArrayList<>();
		this.timers = new ArrayList<>();
		for (String abilityString : itemConfig.getStringList("abilities")) {
			ActivatorLine a = ActivatorFactory.build(abilityString);
			if (a == null) {
				//Continued error message
				GrandLogger.log("  In Item: " + name, LogType.CONFIG_ERRORS);
				continue;
			}
			
			if (a.getType() != ActivatorType.TIMER) abilities.add(a);
			//Special case. Timers are handled differently.
			else timers.add(new AbilityTimer(this, a, a.getTimerDelay()));
		}
		
		this.hash = calculateChecksum();
	}
	
	
	/*---------------Item creation/maintenance---------------*/
	@NotNull @Contract(pure = true)
	public ItemStack create() {
		ItemStack result = new ItemStack(type, amount, durability);
		
		result.addUnsafeEnchantments(enchants);
		
		//Meta data stuff
		ItemMeta meta = result.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		meta.setUnbreakable(unbreakable);
		if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if (hideAttributes) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (hideUnbreakable) meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if (leatherColor != null && meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta)meta).setColor(leatherColor);
		}
		result.setItemMeta(meta);
		
		//Attributes
		Attributes att = new Attributes(result);
		for (GrandAttribute a: attributes) {
			att.add(a.build());
		}
		result = att.getStack();
		
		//Set NBT name, id, and hash
		NbtCompound nbt = NbtFactory.fromItemTag(result, true);
		NbtCompound storage = nbt.getMap(STORAGE, true);
		
		storage.put(STORAGE_NAME, name);
		
		UUID newID = UUID.randomUUID();
		storage.put(STORAGE_UUID_LEAST, newID.getLeastSignificantBits());
		storage.put(STORAGE_UUID_MOST, newID.getMostSignificantBits());
		
		storage.put(STORAGE_HASH, hash);
		
		return result;
	}
	
	@NotNull
	public ItemStack markItem(@NotNull ItemStack item) {
		ItemStack result = NbtFactory.getCraftItemStack(item);
		
		NbtCompound nbt = NbtFactory.fromItemTag(result, true);
		NbtCompound storage = nbt.getMap(STORAGE, true);
		
		//Set name (Identifies the represented GrandItem)
		storage.put(STORAGE_NAME, name);
		
		//Set UUID (Serves as an identifier for inventory operations, and keeps items from stacking)
		UUID newID = UUID.randomUUID();
		storage.put(STORAGE_UUID_LEAST, newID.getLeastSignificantBits());
		storage.put(STORAGE_UUID_MOST, newID.getMostSignificantBits());
		
		//Hash will be set on update
		return result;
	}
	
	/**
	 * Updates the given item to conform to the currently described grand item.
	 * @param item Item to update
	 * @return The updated item, or null if no update was needed
	 */
	@Nullable
	public ItemStack update(@NotNull ItemStack item) {
		ItemStack result = NbtFactory.getCraftItemStack(item);
		NbtCompound tag = NbtFactory.fromItemTag(result, true);
		
		//TEMP while transitioning
		UUID legacyUUID = getLegacyUUID(tag);
		if (legacyUUID != null) {
			GrandLogger.log("Updating item NBT", LogType.DEBUG);
			tag.remove("CustomStorage");
			NbtCompound storage = tag.getMap(STORAGE, true);
			storage.put(STORAGE_NAME, name);
			//Keep same UUID so as not to confuse inventory handling
			storage.put(STORAGE_UUID_LEAST, legacyUUID.getLeastSignificantBits());
			storage.put(STORAGE_UUID_MOST, legacyUUID.getMostSignificantBits());
		}
		
		//Check if needs updating
		NbtCompound storage = tag.getMap(STORAGE, true);
		long itemHash = storage.getLong(STORAGE_HASH, 0L);
		if (itemHash == hash) return null;
		
		GrandLogger.log("Updating item: " + name, LogType.DEBUG);
		
		//Update hash
		storage.put(STORAGE_HASH, hash);
		
		result.setType(type);
		if (updateAmount) result.setAmount(amount);
		if (updateDurability) result.setDurability(durability);
		
		ItemMeta meta = result.getItemMeta();
		if (updateName) meta.setDisplayName(displayName);
		meta.setLore(lore);
		if (leatherColor != null && meta instanceof LeatherArmorMeta) ((LeatherArmorMeta)meta).setColor(leatherColor);
		meta.setUnbreakable(unbreakable);
		if (updateEnchantments) {
			for (Enchantment e : Enchantment.values()) {
				result.removeEnchantment(e);
				if (enchants.get(e) != null) result.addUnsafeEnchantment(e, enchants.get(e));
			}
		}
		meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if (hideAttributes) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (hideUnbreakable) meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		result.setItemMeta(meta);
		
		Attributes att = new Attributes(result);
		att.clear();
		attributes.forEach(a -> att.add(a.build()));
		return att.getStack();
	}
	/*-------------------------------------------------------*/
	
	
	/*----------------------Activators-----------------------*/
	public void activateAbilities(@NotNull ActivatorType activatorType,
	                              @NotNull ItemSlotType slotType,
	                              @NotNull Target target) {
		abilities.stream()
				.filter(a -> activatorType.isSubtypeOf(a.getType()))
				.forEach(a -> a.activate(slotType, target));
	}
	
	public void sendReEquip(@NotNull Player holder, @NotNull ItemSlotType unEquipFrom, @NotNull ItemSlotType equipTo) {
		for (ActivatorLine line : abilities) {
			ItemSlotType request = line.getRequestedSlot();
			
			//Equip
			if (line.getType() == ActivatorType.EQUIP
					&& equipTo.isSubtypeOf(request) && !unEquipFrom.isSubtypeOf(request)) {
				line.activate(request, Target.make(holder, Target.from(holder), Target.none()));
			}
			//Unequip
			else if (line.getType() == ActivatorType.UNEQUIP
					&& unEquipFrom.isSubtypeOf(request) && !equipTo.isSubtypeOf(request)) {
				line.activate(request, Target.make(holder, Target.from(holder), Target.none()));
			}
		}
	}
	
	public void activateTimers(@NotNull Player holder) {
		timers.forEach(timer -> timer.activatePlayer(holder));
	}

	/**
	 * Cancels all running ability timers
	 */
	public void stopTimers() {
		timers.forEach(AbilityTimer::stopTimer);
	}
	/*-------------------------------------------------------*/
	
	
	/*----------------------Informative----------------------*/
	@NotNull @Contract(pure = true)
	public String getName() { return name; }
	
	@NotNull @Contract(pure = true)
	public String getDisplayName() { return displayName; }
	
	@Contract(pure = true)
	public boolean isPersistant() { return persistant; }
	
	@Contract(pure = true)
	public boolean isPlaceable() { return placeable; }
	
    /**
     * Gets the hash code from the name string.
     */
    @Override @Contract(pure = true)
	public int hashCode() {
		return name.hashCode();
	}
	
    /**
     * Compares GrandItems by their names. No two GrandItems should ever have the same name.
     */
	@Override @Contract(pure = true)
	public boolean equals(Object obj) {
		return this == obj
				|| (obj instanceof GrandItem && name.equals(((GrandItem)obj).name));
	}
	/*-------------------------------------------------------*/
	
	
	/*-------------------Static NBT helpers------------------*/
	/**
	 * Finds the name of the GrandItem represented by the given item stack.
	 * @param item Item
	 * @return Name, or empty string
	 */
	@Nullable @SuppressWarnings("WeakerAccess")
	public static String getItemName(@NotNull ItemStack item) {
		NbtCompound tag = NbtFactory.fromItemTag(NbtFactory.getCraftItemStack(item), false);
		if (tag == null) return null;
		
		NbtCompound storage = tag.getMap(STORAGE, false);
		if (storage == null) return getLegacyName(tag); //Temp while transitioning
		
		return storage.getString(STORAGE_NAME, null);
	}
	
	@NotNull @SuppressWarnings("WeakerAccess")
	public static UUID getItemUUID(@NotNull ItemStack item) {
		NbtCompound tag = NbtFactory.fromItemTag(NbtFactory.getCraftItemStack(item), false);
		if (tag == null) throw new IllegalStateException("Item does not have the tag compound.");
		
		NbtCompound storage = tag.getMap(STORAGE, false);
		if (storage == null) {
			//Temp while transitioning
			UUID legacy = getLegacyUUID(tag);
			if (legacy == null) throw new IllegalStateException("Item does not represent a GrandItem.");
			return legacy;
		}
		
		long least = storage.getLong(STORAGE_UUID_LEAST, 0L);
		long most = storage.getLong(STORAGE_UUID_MOST, 0L);
		return new UUID(most, least);
	}
	
	private static String getLegacyName(NbtCompound tag) {
		NbtList storage = tag.getList("CustomStorage", false);
		if (storage == null) return null;
		
		return storage.stream()
				.filter(obj -> obj instanceof NbtCompound)
				.map(obj -> (NbtCompound) obj)
				.filter(compound -> "PraedaGrandis.GrandItemName".equals(compound.getString("Name", null)))
				.map(compound -> compound.getString("Data", null))
				.findAny().orElse(null);
	}
	
	private static UUID getLegacyUUID(NbtCompound tag) {
		NbtList storage = tag.getList("CustomStorage", false);
		if (storage == null) return null;
		
		return storage.stream()
				.filter(obj -> obj instanceof NbtCompound)
				.map(obj -> (NbtCompound) obj)
				.filter(compound -> "PraedaGrandis.GrandItemID".equals(compound.getString("Name", null)))
				.map(compound -> UUID.fromString(compound.getString("Data", "")))
				.findAny().orElse(null);
	}
	/*-------------------------------------------------------*/
	
	
	/*--------------------Parsing helpers--------------------*/
	private static Color parseLeatherColor(ConfigurationSection colorSection, Material itemType, String name) {
		if (colorSection == null) return null;
		if (itemType != Material.LEATHER_BOOTS && itemType != Material.LEATHER_LEGGINGS
				&& itemType != Material.LEATHER_CHESTPLATE && itemType != Material.LEATHER_HELMET) {
			GrandLogger.log("Material \'" + itemType.name() + "\' cannot be colored.", LogType.CONFIG_ERRORS);
			GrandLogger.log("  In item config: " + name, LogType.CONFIG_ERRORS);
			return null;
		}
		
		return Color.fromRGB(colorSection.getInt("red", 0),
				colorSection.getInt("green", 0),
				colorSection.getInt("blue", 0));
	}

	private static List<GrandAttribute> parseAttributes(List<String> attributeLines, String name) {
		List<GrandAttribute> results = new ArrayList<>(attributeLines.size());
		for (String line : attributeLines) {
			GrandAttribute attribute = GrandAttribute.fromString(line);
			if (attribute == null) {
				//Continued error message
				GrandLogger.log("  In item config: " + name, LogType.CONFIG_ERRORS);
				continue;
			}
			
			results.add(attribute);
		}
		return results;
	}

	private static Map<Enchantment, Integer> parseEnchantments(List<String> enchantmentLines, String name) {
		Map<Enchantment, Integer> results = new HashMap<>();
		for (String line : enchantmentLines) {
			String[] args = line.split(" ");
			if (args.length != 2) {
				GrandLogger.log("Invalid enchantment line: " + line, LogType.CONFIG_ERRORS);
				GrandLogger.log("  Expected: <enchantment> <level>", LogType.CONFIG_ERRORS);
				GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
				GrandLogger.log("  In item config: " + name, LogType.CONFIG_ERRORS);
				continue;
			}
			
			Enchantment enchant = Enchantment.getByName(args[0].toUpperCase());
			if (enchant == null) {
				GrandLogger.log("Unknown enchantment: " + args[0], LogType.CONFIG_ERRORS);
				GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
				GrandLogger.log("  In item config: " + name, LogType.CONFIG_ERRORS);
				continue;
			}
			
			if (!StringTools.isInteger(args[1])) {
				GrandLogger.log("Invalid enchantment level: " + args[1], LogType.CONFIG_ERRORS);
				GrandLogger.log("  Expected integer", LogType.CONFIG_ERRORS);
				GrandLogger.log("  In line: " + line, LogType.CONFIG_ERRORS);
				GrandLogger.log("  In item config: " + name, LogType.CONFIG_ERRORS);
				continue;
			}
			
			results.put(enchant, Integer.parseInt(args[1]));
		}
		return results;
	}
	
	private static Material parseMaterial(String materialString, String name) {
		//Material
		Material matchType = Material.matchMaterial(materialString);
		if (matchType == null) {
			GrandLogger.log("Invalid material: " + materialString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  For item: " + name, LogType.CONFIG_ERRORS);
			return Material.STONE;
		}
		return matchType;
	}
		
	private long calculateChecksum() {
		try (
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream dataOutput = new DataOutputStream(byteStream)
		){
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			
			//GrandItemName
			dataOutput.writeChars(name);
			
			//Display name and lore
			dataOutput.writeChars(displayName);
			dataOutput.writeInt(lore.size());
			for (String s : lore) {
				dataOutput.writeChars(s);
			}
			
			//Type, durability, amount, and color
			dataOutput.writeInt(type.ordinal());
			dataOutput.writeShort(durability);
			dataOutput.writeInt(amount);
			dataOutput.writeInt(leatherColor == null ? 0 : leatherColor.asRGB());
			
			//Enchantments
			List<Enchantment> sortedEnchants = Arrays.stream(Enchantment.values())
					.sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
					.collect(Collectors.toList());
			for (Enchantment e : sortedEnchants) {
				dataOutput.writeInt(enchants.getOrDefault(e, 0));
			}
			
			//Attributes
			dataOutput.writeInt(attributes.size());
			for (GrandAttribute a : attributes) {
				dataOutput.writeChars(a.getType().getMinecraftId());
				dataOutput.writeInt(a.getSlot().ordinal());
				dataOutput.writeInt(a.getOperation().getId());
				dataOutput.writeDouble(a.getOperand());
			}
			
			//Boolean options
			dataOutput.writeBoolean(persistant);
			dataOutput.writeBoolean(placeable);
			dataOutput.writeBoolean(unbreakable);
			dataOutput.writeBoolean(hideEnchants);
			dataOutput.writeBoolean(hideAttributes);
			dataOutput.writeBoolean(hideUnbreakable);
			dataOutput.writeBoolean(updateName);
			dataOutput.writeBoolean(updateAmount);
			dataOutput.writeBoolean(updateEnchantments);
			
			//Calculate MD5
			dataOutput.flush();
			byte[] hashBytes = md5.digest(byteStream.toByteArray());
			
			//Get first 64 bits as a long
			return Longs.fromByteArray(Arrays.copyOfRange(hashBytes, 0, 64));
			
		} catch (NoSuchAlgorithmException|IOException e) {
			throw new IllegalStateException("Failed to compute checksum", e);
		}
	}
	/*-------------------------------------------------------*/
	
}
