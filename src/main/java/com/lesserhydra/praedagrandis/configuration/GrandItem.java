package com.lesserhydra.praedagrandis.configuration;

import com.comphenix.attribute.Attributes;
import com.comphenix.attribute.NbtFactory;
import com.comphenix.attribute.NbtFactory.NbtCompound;
import com.comphenix.attribute.NbtFactory.NbtList;
import com.lesserhydra.praedagrandis.AbilityTimer;
import com.lesserhydra.praedagrandis.PraedaGrandis;
import com.lesserhydra.praedagrandis.activator.ActivatorFactory;
import com.lesserhydra.praedagrandis.activator.ActivatorLine;
import com.lesserhydra.praedagrandis.activator.ActivatorType;
import com.lesserhydra.praedagrandis.arguments.ItemSlotType;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GrandItem {
	
	private final String name;
	
	private String displayName;
	private List<String> lore = new ArrayList<>();
	
	private Material type;
	private short durability;
	private int amount;
	private Color leatherColor = null;
	
	private Map<Enchantment, Integer> enchants = new HashMap<>();
	private List<GrandAttribute> attributes = new ArrayList<>();
	
	private boolean persistant;
	private boolean placeable;
	
	private boolean unbreakable;
	private boolean hideEnchants;
	private boolean hideAttributes;
	private boolean hideUnbreakable;
	
	private boolean updateName = false;
	private boolean updateDurability = false;
	private boolean updateAmount = false;
	private boolean updateEnchantments = false;
	
	private List<ActivatorLine> abilities = new ArrayList<>();
	
	private List<AbilityTimer> timers = new ArrayList<>();
	
	//TODO: Make factory function
	GrandItem(ConfigurationSection itemConfig) {
		this.name = itemConfig.getName();
		
		displayName = itemConfig.getString("display", "").replace('&', ChatColor.COLOR_CHAR);
		for (String loreString : itemConfig.getStringList("lore")) {
			lore.add(loreString.replace('&', ChatColor.COLOR_CHAR));
		}
		
		type = Material.matchMaterial(itemConfig.getString("type", "stone"));
		durability = (short) itemConfig.getInt("durability", 0);
		amount = itemConfig.getInt("amount", 1);
		
		if (itemConfig.contains("leatherColor")) {
			leatherColor = Color.fromRGB(itemConfig.getInt("leatherColor.red", 0), itemConfig.getInt("leatherColor.green", 0), itemConfig.getInt("leatherColor.blue", 0));
		}
		
		//Enchantments
		for (String s : itemConfig.getStringList("enchantments")) {
			String[] eStrings = s.split(" ");
					
			if (eStrings.length == 2) {
				Enchantment enchant = Enchantment.getByName(eStrings[0].toUpperCase());
				if (enchant != null) {
					enchants.put(enchant, Integer.parseInt(eStrings[1]));
				}
				else {
					PraedaGrandis.plugin.getLogger().info("Unknown enchantment: " + eStrings[0]);
				}
			}
			//TODO: Error handling
		}
		
		//Attributes
		for (String s : itemConfig.getStringList("attributes")) {
			GrandAttribute att = GrandAttribute.fromString(s);
			if (att != null) attributes.add(att);
		}
		
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
		for (String abilityString : itemConfig.getStringList("abilities")) {
			ActivatorLine a = ActivatorFactory.build(abilityString);
			if (a == null) continue;
			
			if (a.getType() != ActivatorType.TIMER) {
				abilities.add(a);
			}
			else {//Special case. Timers are handled differently.
				timers.add(new AbilityTimer(this, a, a.getTimerDelay()));
			}
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

	public ItemStack update(ItemStack item) {
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
		NbtCompound storage = nbt.getMap("PraedaGrandis", true);
		
		//Set name (Identifies the represented GrandItem)
		storage.put("Name", name);
		
		//Set UUID (Serves as an identifier for inventory operations, and keeps items from stacking)
		UUID newID = UUID.randomUUID();
		storage.put("UUIDLeast", newID.getLeastSignificantBits());
		storage.put("UUIDMost", newID.getMostSignificantBits());
		
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
		
		NbtCompound storage = tag.getMap("PraedaGrandis", false);
		if (storage == null) return getLegacyName(tag); //Temp while transitioning
		
		return storage.getString("Name", null);
	}
	
	@NotNull
	public static UUID getItemUUID(ItemStack item) {
		NbtCompound tag = NbtFactory.fromItemTag(NbtFactory.getCraftItemStack(item), false);
		if (tag == null) throw new IllegalStateException("Item does not have the tag compound.");
		
		NbtCompound storage = tag.getMap("PraedaGrandis", false);
		if (storage == null) {
			//Temp while transitioning
			UUID legacy = getLegacyUUID(tag);
			if (legacy == null) throw new IllegalStateException("Item does not have the PraedaGrandis compound.");
			return legacy;
		}
		
		long least = storage.getLong("UUIDLeast", 0L);
		long most = storage.getLong("UUIDLeast", 0L);
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
