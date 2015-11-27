package com.roboboy.PraedaGrandis.Commands;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public enum MainSubCommandEnum
{
	//TODO: Implement help output
	NONE ("", null),
	
	LIST ("list", new PGListSubCommand()),
	GIVE ("give", new PGGiveSubCommand()),
	RELOAD ("reload", new PGReloadSubCommand());

	final private String name;
	final private SubCommand subCommand;
	
	private MainSubCommandEnum(String name, SubCommand subCommand) {
		this.name = name;
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
	
	@Override
	public String toString() {
		return name;
	}
	
	static public MainSubCommandEnum fromString(String s) {
		for (MainSubCommandEnum sub : values()) {
			if (s.equalsIgnoreCase(sub.toString())) return sub;
		}
		return NONE;
	}

	static public List<String> autoCompleteName(String s) {
		List<String> results = new LinkedList<String>();
		for (MainSubCommandEnum sub : values()) {
			if (s.toLowerCase().startsWith(sub.toString())) results.add(sub.toString());
		}
		return results;
	}
}
