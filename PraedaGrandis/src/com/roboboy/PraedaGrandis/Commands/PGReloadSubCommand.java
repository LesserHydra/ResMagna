package com.roboboy.PraedaGrandis.Commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.roboboy.PraedaGrandis.PraedaGrandis;

public class PGReloadSubCommand implements SubCommand
{
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		PraedaGrandis.plugin.reload();
		sender.sendMessage(ChatColor.GREEN + "Reloaded config files.");
	}

	@Override
	public List<String> autoCompleteArg(String[] args) {
		return new ArrayList<>();
	}
}
