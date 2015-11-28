package com.roboboy.PraedaGrandis.Commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class NullSubCommand implements SubCommand
{
	@Override
	public void execute(CommandSender sender, Command command, String label, String[] args) {}

	@Override
	public List<String> autoCompleteArg(String[] args) {
		return new ArrayList<>();
	}
}
