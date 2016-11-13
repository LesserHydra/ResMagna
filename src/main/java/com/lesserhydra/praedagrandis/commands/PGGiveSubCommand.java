package com.lesserhydra.praedagrandis.commands;

import com.lesserhydra.praedagrandis.configuration.GrandItem;
import com.lesserhydra.praedagrandis.configuration.ItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

class PGGiveSubCommand implements SubCommand {
	
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		//Parse arguments
		if (args.length != 3) return;
		
		//Parse player
		Player givePlayer = Bukkit.getPlayer(args[1]);
		if (givePlayer == null) {
			sender.sendMessage(ChatColor.RED + "Player does not exist.");
			return;
		}
		
		//Parse item
		GrandItem giveItem = ItemHandler.getInstance().getItem(args[2]);
		if (giveItem == null) {
			sender.sendMessage(ChatColor.RED + "Item does not exist.");
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
		
		String complete = args[2].toLowerCase();
		List<String> results = ItemHandler.getInstance().getItemNames().stream()
				.filter(s -> s.startsWith(complete))
				.collect(Collectors.toList());
		Collections.sort(results, Collator.getInstance());
		return results;
	}
	
}
