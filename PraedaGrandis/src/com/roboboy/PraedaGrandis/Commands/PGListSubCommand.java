package com.roboboy.PraedaGrandis.Commands;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public class PGListSubCommand implements SubCommand
{
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage(ChatColor.BLUE + PraedaGrandis.plugin.itemHandler.listItems());
	}

	@Override
	public List<String> autoCompleteArg(String[] args) {
		return null;
	}
}