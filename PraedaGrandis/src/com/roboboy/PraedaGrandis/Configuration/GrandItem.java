package com.roboboy.PraedaGrandis.Configuration;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import com.comphenix.attribute.Attributes;
import com.comphenix.attribute.NBTStorage;
import com.roboboy.PraedaGrandis.AbilityTimer;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Abilities.Ability;
import com.roboboy.PraedaGrandis.Abilities.AbilityFactory;
import com.roboboy.PraedaGrandis.Abilities.ActivatorType;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;
import com.roboboy.PraedaGrandis.Abilities.Targeters.Target;

public class GrandItem
{
	private final String id;
	
	private String name;
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
	
	private List<Ability> abilities = new ArrayList<>();
	
	private List<AbilityTimer> timers = new ArrayList<>();
	
	public GrandItem(ConfigurationSection itemConfig)
	{
		this.id = itemConfig.getName();
		
		name = itemConfig.getString("display", "").replace('&', ChatColor.COLOR_CHAR);
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
		for (String s : itemConfig.getStringList("enchantments"))
		{
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
			attributes.add(new GrandAttribute(new ConfigString(s)));
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
		for (String abilityString : itemConfig.getStringList("abilities"))
		{
			Ability a = AbilityFactory.build(abilityString);
			if (a == null) continue;
			
			if (a.getActivator() != ActivatorType.TIMER) {
				abilities.add(a);
			}
			else {//Special case. Timers are handled differently.
				timers.add(new AbilityTimer(this, a, a.getTimerDelay()));
			}
		}
	}

	public ItemStack create()
	{
		ItemStack item = new ItemStack(type, amount, durability);
		
		item.addUnsafeEnchantments(enchants);
		
		//Meta data stuff
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.spigot().setUnbreakable(unbreakable);
		if (hideEnchants) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if (hideAttributes) meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (hideUnbreakable) meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		if (leatherColor != null && meta instanceof LeatherArmorMeta) {
			((LeatherArmorMeta)meta).setColor(leatherColor);
		}
		item.setItemMeta(meta);
		
		//Attributes
		Attributes att = new Attributes(item);
		for (GrandAttribute a: attributes)
			att.add(a.build());
		
		//NBT Name
		NBTStorage storage = NBTStorage.newTarget(att.getStack(), PraedaGrandis.STORAGE_ITEM_NAME);
		storage.setData(id);
		
		//NBT ID
		UUID newID = UUID.randomUUID();
		storage = NBTStorage.newTarget(storage.getTarget(), PraedaGrandis.STORAGE_ITEM_ID);
		storage.setData(newID.toString());
		
		return storage.getTarget();
	}

	public ItemStack update(ItemStack item)
	{
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
		if (updateName) meta.setDisplayName(name);
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
	
	public void activateAbilities(ActivatorType activatorType, EnumSet<ItemSlotType> slotTypes, Target target)
	{
		for (Ability a : abilities) {
			if (!activatorType.isSubtypeOf(a.getActivator())) continue;
			a.activate(slotTypes, target);
		}
	}
	
	public void activateTimers(Player holder)
	{
		for (AbilityTimer at : timers) {
			at.activatePlayer(holder);
		}
	}
	
	public boolean isPersistant() {
		return persistant;
	}

	public String getDisplayName() {
		return name;
	}

	public boolean isPlaceable() {
		return placeable;
	}
	
    public String getId() {
        return this.id;
    }
}
