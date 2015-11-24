package com.roboboy.PraedaGrandis.Commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MainCommandExecutor implements TabCompleter, CommandExecutor
{
	final static public String COMMAND_NAME = "PraedaGrandis";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (!command.getName().equalsIgnoreCase(COMMAND_NAME)) return false;
		if (args.length == 0) return false;
		
		MainSubCommand sub = MainSubCommand.fromString(args[0]);
		sub.execute(sender, command, alias, args);
		return (sub != MainSubCommand.NONE);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (!command.getName().equalsIgnoreCase(COMMAND_NAME)) return null;
		if (args.length <= 1) {
			return MainSubCommand.autoCompleteName(args.length == 0 ? "" : args[0]);
		}
		
		MainSubCommand sub = MainSubCommand.fromString(args[0]);
		return sub.autoCompleteArg(args);
	}

}
