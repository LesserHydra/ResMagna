package com.roboboy.PraedaGrandis.Commands;

import java.text.Collator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.roboboy.PraedaGrandis.PraedaGrandis;
import com.roboboy.PraedaGrandis.Configuration.GrandItem;

public enum MainSubCommand
{
	//TODO: Make seperate classes, hold as member variables.
	//TODO: Implement help output
	NONE {
		@Override public void execute(CommandSender sender, Command command, String label, String[] args) {}
		@Override public List<String> autoCompleteArg(String[] args) {return null;}
	},
	
	LIST {
		@Override
		public void execute(CommandSender sender, Command command, String label, String[] args) {
			sender.sendMessage(ChatColor.BLUE + PraedaGrandis.plugin.itemHandler.listItems());
		}
		@Override public List<String> autoCompleteArg(String[] args) {return null;}
	},
	
	GIVE {
		@Override
		public void execute(CommandSender sender, Command command, String label, String[] args) {
			if (args.length < 3) return;
			Player givePlayer = Bukkit.getPlayer(args[1]);
			GrandItem giveItem = PraedaGrandis.plugin.itemHandler.getItem(args[2]);
			if (givePlayer != null && giveItem != null) {
				givePlayer.getInventory().addItem(giveItem.create());
				//TODO: Drop on ground if inventory full
			}
			else {
				sender.sendMessage(ChatColor.RED + "Item or player does not exist.");
			}
		}

		@Override
		public List<String> autoCompleteArg(String[] args) {
			if (args.length != 3) return null;
			List<String> results = new LinkedList<String>();
			for (String s : PraedaGrandis.plugin.itemHandler.getItemNames()) {
				if (s.startsWith(args[2].toLowerCase())) results.add(s);
			}
			//Sorted for convenience
			Collections.sort(results, Collator.getInstance());
			return results;
		}
	},
	
	RELOAD {
		@Override
		public void execute(CommandSender sender, Command command, String label, String[] args) {
			PraedaGrandis.plugin.reload();
			sender.sendMessage(ChatColor.GREEN + "Reloaded config files.");
		}
		@Override public List<String> autoCompleteArg(String[] args) {return null;}
	};

	abstract public void execute(CommandSender sender, Command command, String label, String[] args);
	abstract public List<String> autoCompleteArg(String[] args);
	
	static public MainSubCommand fromString(String s) {
		for (MainSubCommand sub : values()) {
			if (sub == NONE) continue;
			if (sub.toString().equals(s.toUpperCase())) return sub;
		}
		return NONE;
	}
	
	static public List<String> autoCompleteName(String s) {
		List<String> results = new LinkedList<String>();
		for (MainSubCommand sub : values()) {
			if (sub == NONE) continue;
			if (sub.toString().startsWith(s.toUpperCase())) results.add(sub.toString().toLowerCase());
		}
		return results;
	}
}
