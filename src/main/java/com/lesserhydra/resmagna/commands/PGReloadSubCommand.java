package com.lesserhydra.resmagna.commands;

import java.util.ArrayList;
import java.util.List;

import com.lesserhydra.resmagna.ResMagna;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class PGReloadSubCommand implements SubCommand
{
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		ResMagna.plugin.reload();
		sender.sendMessage(ChatColor.GREEN + "Reloaded config files.");
	}

	@Override
	public List<String> autoCompleteArg(String[] args) {
		return new ArrayList<>();
	}
}
