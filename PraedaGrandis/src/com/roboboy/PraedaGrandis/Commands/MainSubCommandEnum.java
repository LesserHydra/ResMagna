package com.roboboy.PraedaGrandis.Commands;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public enum MainSubCommandEnum
{
	//TODO: Implement help output
	NONE (null),
	
	LIST (new PGListSubCommand()),
	GIVE (new PGGiveSubCommand()),
	RELOAD (new PGReloadSubCommand());

	final private SubCommand subCommand;
	
	private MainSubCommandEnum(SubCommand subCommand) {
		this.subCommand = subCommand;
	}
	
	public void execute(CommandSender sender, Command command, String label, String[] args) {
		if (this == NONE) return;
		subCommand.execute(sender, command, label, args);
	}
	
	public List<String> autoCompleteArg(String[] args) {
		if (this == NONE) return null;
		return subCommand.autoCompleteArg(args);
	}
	
	static public MainSubCommandEnum fromString(String s) {
		for (MainSubCommandEnum sub : values()) {
			if (sub == NONE) continue;
			if (sub.toString().equals(s.toUpperCase())) return sub;
		}
		return NONE;
	}
	
	static public List<String> autoCompleteName(String s) {
		List<String> results = new LinkedList<String>();
		for (MainSubCommandEnum sub : values()) {
			if (sub == NONE) continue;
			if (sub.toString().startsWith(s.toUpperCase())) results.add(sub.toString().toLowerCase());
		}
		return results;
	}
}
