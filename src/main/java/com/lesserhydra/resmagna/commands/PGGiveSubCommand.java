package com.lesserhydra.resmagna.commands;

import com.lesserhydra.resmagna.configuration.GrandItem;
import com.lesserhydra.resmagna.configuration.ItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class PGGiveSubCommand implements SubCommand {
	
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		//Parse arguments
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Insert usage text here"); //TODO: Help
			return;
		}
		
		//Parse player
		Player givePlayer;
		String itemName;
		if (args.length == 2) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Player must be specified for non-player sender.");
				sender.sendMessage(ChatColor.RED + "Insert usage text here"); //TODO: Help
				return;
			}
			givePlayer = (Player) sender;
			itemName = args[1];
		}
		else {
			givePlayer = parsePlayer(args[1], sender);
			itemName = args[2];
		}
		
		if (givePlayer == null) {
			sender.sendMessage(ChatColor.RED + "Unknown player.");
			return;
		}
		
		//Parse item
		GrandItem giveItem = ItemHandler.getInstance().getItem(itemName);
		if (giveItem == null) {
			sender.sendMessage(ChatColor.RED + "Unknown item.");
			return;
		}
		
		//Give to player
		givePlayer.getInventory().addItem(giveItem.create())
				//Drop on ground if inventory full
				.forEach((i, item) -> givePlayer.getWorld().dropItem(givePlayer.getLocation(), item));
	}
	
	private Player parsePlayer(String playerString, CommandSender sender) {
		if (playerString.equalsIgnoreCase("@p")) {
			Location senderLoc = getSenderLocation(sender);
			if (senderLoc == null) return null;
			
			Optional<ImmutablePair<Double, Player>> found = senderLoc.getWorld().getPlayers().stream()
					.map(p -> new ImmutablePair<>(p.getLocation().distanceSquared(senderLoc), p))
					.min((a, b) -> (int) (a.left - b.left));
			return found.isPresent() ? found.get().right : null;
		}
		return Bukkit.getPlayer(playerString);
	}
	
	private Location getSenderLocation(CommandSender sender) {
		if (sender instanceof BlockCommandSender) return ((BlockCommandSender)sender).getBlock().getLocation();
		if (sender instanceof Entity) return ((Entity)sender).getLocation();
		return null;
	}
	
	@Override
	public List<String> autoCompleteArg(String[] args) {
		if (args.length == 2) return null;				//Player name
		if (args.length != 3) return new ArrayList<>();	//Nothing
		
		String complete = args[2].toLowerCase();
		List<String> results = ItemHandler.getInstance().getItemNames().stream()
				.filter(s -> s.startsWith(complete))
				.collect(Collectors.toList());
		results.sort(Collator.getInstance());
		return results;
	}
	
}
