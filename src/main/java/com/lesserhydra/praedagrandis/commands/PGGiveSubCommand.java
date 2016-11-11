package com.lesserhydra.praedagrandis.commands;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.lesserhydra.praedagrandis.configuration.GrandItem;
import com.lesserhydra.praedagrandis.configuration.ItemHandler;

public class PGGiveSubCommand implements SubCommand
{
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		//Parse arguments
		if (args.length != 3) return;
		Player givePlayer = Bukkit.getPlayer(args[1]);
		GrandItem giveItem = ItemHandler.getInstance().getItem(args[2]);
		
		//Return if player or item does not exist
		if (givePlayer == null || giveItem == null) {
			sender.sendMessage(ChatColor.RED + "Item or player does not exist.");
			return;
		}
		
		//Give to player
		HashMap<Integer, ItemStack> remainingItems = givePlayer.getInventory().addItem(giveItem.create());
		
		//Drop on ground if inventory full
		for (ItemStack item : remainingItems.values()) {
			givePlayer.getWorld().dropItem(givePlayer.getLocation(), item);
		}
	}

	@Override
	public List<String> autoCompleteArg(String[] args) {
		if (args.length == 2) return null;				//Player name
		if (args.length != 3) return new ArrayList<>();	//Nothing
		
		List<String> results = new LinkedList<String>();
		for (String s : ItemHandler.getInstance().getItemNames()) {
			if (s.startsWith(args[2].toLowerCase())) results.add(s);
		}
		//Sorted for convenience
		Collections.sort(results, Collator.getInstance());
		return results;
	}
}
