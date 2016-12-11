package com.lesserhydra.resmagna.commands;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public enum MainSubCommandEnum {
	
	//TODO: Implement help output
	NULL ("", new NullSubCommand()),
	
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
		subCommand.execute(sender, command, label, args);
	}
	
	public List<String> autoCompleteArg(String[] args) {
		return subCommand.autoCompleteArg(args);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public boolean isNull() {
		return this == NULL;
	}
	
	static public MainSubCommandEnum fromString(String s) {
		for (MainSubCommandEnum sub : values()) {
			if (sub.toString().equalsIgnoreCase(s)) return sub;
		}
		return NULL;
	}
	
	static public List<String> autoCompleteName(String s) {
		List<String> results = new LinkedList<String>();
		for (MainSubCommandEnum sub : values()) {
			if (sub.toString().startsWith(s.toLowerCase())) results.add(sub.toString());
		}
		return results;
	}
	
}
