package com.roboboy.PraedaGrandis;

import java.util.EnumSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.roboboy.PraedaGrandis.Abilities.ItemSlotType;

public enum ArmorType
{
	NONE		(ItemSlotType.NONE,			EnumSet.noneOf(Material.class)),
	HELMET		(ItemSlotType.HELMET,		EnumSet.of(Material.LEATHER_HELMET, Material.GOLD_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET)),
	CHESTPLATE	(ItemSlotType.CHESTPLATE,	EnumSet.of(Material.LEATHER_CHESTPLATE, Material.GOLD_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE)),
	LEGGINGS	(ItemSlotType.LEGGINGS,		EnumSet.of(Material.LEATHER_LEGGINGS, Material.GOLD_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS)),
	BOOTS		(ItemSlotType.BOOTS,		EnumSet.of(Material.LEATHER_BOOTS, Material.GOLD_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS)),
	BLOCK		(ItemSlotType.HELMET,		EnumSet.of(Material.SKULL_ITEM, Material.PUMPKIN));
	
	private final ItemSlotType slotType;
	private final EnumSet<Material> materialSet;
	
	private ArmorType(ItemSlotType slotType, EnumSet<Material> materialSet) {
		this.slotType = slotType;
		this.materialSet = materialSet;
	}
	
	public boolean isNull() {
		return (this == NONE);
	}
	
	public boolean matches(Material materialToCheck) {
		return (materialSet.contains(materialToCheck));
	}

	public boolean isEmpty(Player player) {
		for (ItemStack item : slotType.getItems(player)) {
			if (item != null && item.getType() != Material.AIR) return false;
		}
		return true;
	}

	public ItemSlotType getItemSlotType() {
		return slotType;
	}
	
	static public ArmorType fromMaterial(Material material) {
		for (ArmorType type : values()) {
			if (type.matches(material)) return type;
		}
		return NONE;
	}
	
}
