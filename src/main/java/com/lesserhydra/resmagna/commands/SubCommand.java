package com.lesserhydra.resmagna.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand
{
	abstract public void execute(CommandSender sender, Command command, String label, String[] args);
	
	abstract public List<String> autoCompleteArg(String[] args);
}
