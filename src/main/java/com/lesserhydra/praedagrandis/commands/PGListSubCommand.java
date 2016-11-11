package com.lesserhydra.praedagrandis.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.lesserhydra.praedagrandis.configuration.ItemHandler;

public class PGListSubCommand implements SubCommand
{
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.BLUE + ItemHandler.getInstance().listItems());
	}

	@Override
	public List<String> autoCompleteArg(String[] args) {
		return new ArrayList<>();
	}
}
