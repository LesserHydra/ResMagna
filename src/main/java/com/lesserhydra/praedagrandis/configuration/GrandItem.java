package com.lesserhydra.praedagrandis.configuration;

import com.comphenix.attribute.Attributes;
import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;
import com.google.common.primitives.Longs;
import com.lesserhydra.praedagrandis.AbilityTimer;
import com.lesserhydra.praedagrandis.activator.ActivatorFactory;
import com.lesserhydra.praedagrandis.activator.ActivatorLine;
import com.lesserhydra.praedagrandis.activator.ActivatorType;
import com.lesserhydra.praedagrandis.arguments.ItemSlotType;
import com.lesserhydra.praedagrandis.logging.GrandLogger;
import com.lesserhydra.praedagrandis.logging.LogType;
import com.lesserhydra.praedagrandis.targeters.Target;
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
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class GrandItem {
	
	private static final String STORAGE = "PraedaGrandis";
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
		displayName = itemConfig.getString("display", "").replace('&', ChatColor.COLOR_CHAR);
		this.lore = itemConfig.getStringList("lore").stream()
				.map(loreString -> loreString.replace('&', ChatColor.COLOR_CHAR))
				.collect(Collectors.toList());
		
		//Material
		String materialString = itemConfig.getString("type", "stone");
		Material matchType = Material.matchMaterial(materialString);
		if (matchType == null) {
			GrandLogger.log("Invalid material: " + materialString, LogType.CONFIG_ERRORS);
			GrandLogger.log("  For item: " + itemConfig.getName(), LogType.CONFIG_ERRORS);
			matchType = Material.STONE;
		}
		type = matchType;
		
		durability = (short) itemConfig.getInt("durability", 0);
		amount = itemConfig.getInt("amount", 1);
		
		//Leather armor color
		if (itemConfig.contains("leatherColor")) {
			leatherColor = Color.fromRGB(itemConfig.getInt("leatherColor.red", 0),
					itemConfig.getInt("leatherColor.green", 0),
					itemConfig.getInt("leatherColor.blue", 0));
		}
		else leatherColor = null;
		
		//Enchantments
		this.enchants = new HashMap<>();
		for (String s : itemConfig.getStringList("enchantments")) {
			String[] eStrings = s.split(" ");
			
			if (eStrings.length == 2) {
				Enchantment enchant = Enchantment.getByName(eStrings[0].toUpperCase());
				if (enchant != null) enchants.put(enchant, Integer.parseInt(eStrings[1]));
				else GrandLogger.log("Unknown enchantment: " + eStrings[0], LogType.CONFIG_ERRORS);
			}
			//TODO: Error handling
		}
		
		//Attributes
		this.attributes = itemConfig.getStringList("attributes").stream()
				.map(GrandAttribute::fromString)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		
		persistant = itemConfig.getBoolean("options.persistant", false);
		placeable = itemConfig.getBoolean("options.placeable", false);
		
		unbreakable = itemConfig.getBoolean("options.unbreakable", false);
		hideUnbreakable = itemConfig.getBoolean("options.hideUnbreakable", false);
		hideEnchants = itemConfig.getBoolean("options.hideEnchantments", false);
		hideAttributes = itemConfig.getBoolean("options.hideAttributes", false);
		
		updateName = itemConfig.getBoolean("options.fixName", false);
		updateDurability = itemConfig.getBoolean("options.fixDurability", false);
		updateAmount = itemConfig.getBoolean("options.fixAmount", false);
		updateEnchantments = itemConfig.getBoolean("options.fixEnchantments", false);
		
		//Abilities
		this.abilities = new ArrayList<>();
		this.timers = new ArrayList<>();
		for (String abilityString : itemConfig.getStringList("abilities")) {
			ActivatorLine a = ActivatorFactory.build(abilityString);
			if (a == null) {
				GrandLogger.log("  In Item: " + name, LogType.CONFIG_ERRORS);
				continue;
			}
			
			if (a.getType() != ActivatorType.TIMER) abilities.add(a);
			//Special case. Timers are handled differently.
			else timers.add(new AbilityTimer(this, a, a.getTimerDelay()));
		}
		
		this.hash = calculateChecksum();
	}
	
	private long calculateChecksum() {
		try (
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			DataOutputStream dataOutput = new DataOutputStream(byteStream)
		){
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			
			dataOutput.writeChars(name);
			dataOutput.writeChars(displayName);
			
			for (String s : lore) {
				dataOutput.writeChars(s);
			}
			
			dataOutput.writeInt(type.ordinal());
			dataOutput.writeShort(durability);
			dataOutput.writeInt(amount);
			if (leatherColor != null) dataOutput.writeInt(leatherColor.asRGB());
			
			for (Enchantment e : Enchantment.values()) {
				dataOutput.writeInt(enchants.getOrDefault(e, 0));
			}
			
			for (GrandAttribute a : attributes) {
				dataOutput.writeChars(a.getType().toString());
				dataOutput.writeInt(a.getSlot().ordinal());
				dataOutput.writeInt(a.getOperation().getId());
				dataOutput.writeDouble(a.getOperand());
			}
			
			dataOutput.writeBoolean(persistant);
			dataOutput.writeBoolean(placeable);
			dataOutput.writeBoolean(unbreakable);
			dataOutput.writeBoolean(hideEnchants);
			dataOutput.writeBoolean(hideAttributes);
			dataOutput.writeBoolean(hideUnbreakable);
			dataOutput.writeBoolean(updateName);
			dataOutput.writeBoolean(updateAmount);
			dataOutput.writeBoolean(updateEnchantments);
			
			dataOutput.flush();
			
			byte[] hashBytes = md5.digest(byteStream.toByteArray());
			return Longs.fromByteArray(Arrays.copyOfRange(hashBytes, 0, 64));
			
		} catch (NoSuchAlgorithmException| IOException e) {
			throw new IllegalStateException("Failed to compute checksum", e);
		}
	}

	public ItemStack create() {
		ItemStack result = new ItemStack(type, amount, durability);
		
		result.addUnsafeEnchantments(enchants);
		
		//Meta data stuff
		ItemMeta meta = result.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLore(lore);
		meta.spigot().setUnbreakable(unbreakable);
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
		
		//Set NBT name and id
		result = markItem(result);
		
		return result;
	}

	public ItemStack update(@NotNull ItemStack item) {
		//TEMP while transitioning
		item = NbtFactory.getCraftItemStack(item);
		NbtCompound tag = NbtFactory.fromItemTag(item, true);
		UUID legacyUUID = getLegacyUUID(tag);
		if (legacyUUID != null) {
			tag.remove("CustomStorage");
			NbtCompound storage = tag.getMap(STORAGE, true);
			storage.put(STORAGE_NAME, name);
			//Keep same UUID so as not to confuse inventory handling
			storage.put(STORAGE_UUID_LEAST, legacyUUID.getLeastSignificantBits());
			storage.put(STORAGE_UUID_MOST, legacyUUID.getMostSignificantBits());
		}
		
		//Check is needs updating
		NbtCompound storage = tag.getMap(STORAGE, true);
		long itemHash = storage.getLong(STORAGE_HASH, 0L);
		if (itemHash == hash) return item;
		
		//Update hash
		storage.put(STORAGE_HASH, hash);
		
		item.setType(type);
		if (updateAmount) item.setAmount(amount);
		if (updateDurability) item.setDurability(durability);
		
		if (updateEnchantments) {
			for (Enchantment e : Enchantment.values()) {
				item.removeEnchantment(e);
				if (enchants.get(e) != null) {
					item.addUnsafeEnchantment(e, enchants.get(e));
				}
			}
		}
		
		ItemMeta meta = item.getItemMeta();
		if (updateName) meta.setDisplayName(displayName);
		meta.setLore(lore);
		meta.spigot().setUnbreakable(unbreakable);
		meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
		if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if (hideAttributes) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (hideUnbreakable) meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if (leatherColor != null && meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta)meta).setColor(leatherColor);
		}
		item.setItemMeta(meta);
		
		Attributes att = new Attributes(item);
		//Remove all attributes
		att.clear();
		
		//Re-add the proper attributes
		for (GrandAttribute a: attributes) {
			att.add(a.build());
		}
		
		return att.getStack();
	}
	
	public void activateAbilities(ActivatorType activatorType, ItemSlotType slotType, Target target) {
		abilities.stream()
				.filter(a -> activatorType.isSubtypeOf(a.getType()))
				.forEach(a -> a.activate(slotType, target));
	}
	
	public void activateTimers(Player holder) {
		timers.forEach(timer -> timer.activatePlayer(holder));
	}
	
	public void sendEquip(Player holder, ItemSlotType equipTo) {
		activateAbilities(ActivatorType.EQUIP, equipTo, Target.make(holder, Target.from(holder), Target.none()));
	}
	
	public void sendUnEquip(Player holder, ItemSlotType unEquipFrom) {
		activateAbilities(ActivatorType.UNEQUIP, unEquipFrom, Target.make(holder, Target.from(holder), Target.none()));
	}
	
	public void sendReEquip(Player holder, ItemSlotType unEquipFrom, ItemSlotType equipTo) {
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
	
	/**
	 * Cancels all running ability timers
	 */
	void stopTimers() {
		for (AbilityTimer at : timers) {
			at.stopTimer();
		}
	}
	
	public boolean isPersistant() {
		return persistant;
	}

	public String getDisplayName() {
		return displayName;
	}

	public boolean isPlaceable() {
		return placeable;
	}
	
    public String getName() {
        return name;
    }
    
    /**
     * Gets the hash code from the name string.
     */
    @Override
	public int hashCode() {
		return name.hashCode();
	}
	
    /**
     * Compares GrandItems by their names. No two GrandItems should ever have the same name.
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof GrandItem)) return false;
		return name.equals(((GrandItem)obj).name);
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
	 * Finds the name of the GrandItem represented by the given item stack.
	 * @param item Item
	 * @return Name, or empty string
	 */
	@Nullable
	public static String getItemName(ItemStack item) {
		NbtCompound tag = NbtFactory.fromItemTag(NbtFactory.getCraftItemStack(item), false);
		if (tag == null) return null;
		
		NbtCompound storage = tag.getMap(STORAGE, false);
		if (storage == null) return getLegacyName(tag); //Temp while transitioning
		
		return storage.getString(STORAGE_NAME, null);
	}
	
	@NotNull
	public static UUID getItemUUID(ItemStack item) {
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
	
}
