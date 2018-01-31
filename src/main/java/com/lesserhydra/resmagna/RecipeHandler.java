package com.lesserhydra.resmagna;

import com.lesserhydra.resmagna.configuration.GrandItem;
import com.lesserhydra.resmagna.configuration.ItemHandler;
import com.lesserhydra.resmagna.logging.GrandLogger;
import com.lesserhydra.resmagna.logging.LogType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeHandler implements Listener {
	
	public void registerRecipes() {
		ItemHandler.getInstance().streamRecipes().forEach(recipe -> {
			GrandLogger.log("Registering recipe:", LogType.DEBUG);
			((ShapelessRecipe) recipe).getIngredientList().forEach(item -> GrandLogger.log("- " + item.getType(), LogType.DEBUG));
			Bukkit.addRecipe(recipe);
		});
	}
	
	@EventHandler
	public void onCraftItem(PrepareItemCraftEvent event) {
		CraftingInventory inv = event.getInventory();
		ItemStack resultItem = inv.getResult();
		GrandItem gItem = ItemHandler.getInstance().matchItem(resultItem);
		if (gItem == null) return;
		//Regenerate item
		inv.setResult(gItem.create());
	}
	
}
