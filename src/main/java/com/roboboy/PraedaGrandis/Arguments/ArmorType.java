package com.roboboy.PraedaGrandis.Arguments;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public enum ArmorType {
	
	NONE		(ItemSlotType.NONE),
	
	HELMET		(ItemSlotType.HELMET,		Material.LEATHER_HELMET, Material.GOLD_HELMET, Material.CHAINMAIL_HELMET,
											Material.IRON_HELMET, Material.DIAMOND_HELMET),
	
	CHESTPLATE	(ItemSlotType.CHESTPLATE,	Material.LEATHER_CHESTPLATE, Material.GOLD_CHESTPLATE,
											Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
											Material.DIAMOND_CHESTPLATE),
	
	LEGGINGS	(ItemSlotType.LEGGINGS,		Material.LEATHER_LEGGINGS, Material.GOLD_LEGGINGS,
											Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS,
											Material.DIAMOND_LEGGINGS),
	
	BOOTS		(ItemSlotType.BOOTS,		Material.LEATHER_BOOTS, Material.GOLD_BOOTS, Material.CHAINMAIL_BOOTS,
	                                        Material.IRON_BOOTS, Material.DIAMOND_BOOTS),
	
	BLOCK		(ItemSlotType.HELMET,		Material.SKULL_ITEM, Material.PUMPKIN);
	
	private final ItemSlotType slotType;
	private final Material[] materials;
	
	ArmorType(ItemSlotType slotType, Material... materials) {
		this.slotType = slotType;
		this.materials = materials;
	}
	
	public boolean isNull() { return this == NONE; }
	
	public boolean isEmpty(Player player) {
		for (ItemStack item : slotType.getItems(player)) {
			if (item != null && item.getType() != Material.AIR) return false;
		}
		return true;
	}

	public ItemSlotType getItemSlotType() { return slotType; }
	
	
	//Mappings
	private static final Map<Material, ArmorType> materialMap = new EnumMap<>(Material.class);
	static {
		for (ArmorType type : values()) {
			for (Material material : type.materials) {
				materialMap.put(material, type);
			}
		}
	}
	
	public static ArmorType fromMaterial(Material material) {
		return materialMap.getOrDefault(material, NONE);
	}
	
}
