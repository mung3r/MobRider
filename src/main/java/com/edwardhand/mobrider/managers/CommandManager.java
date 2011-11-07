/*
 * CommandManager from RightLegRed
 */

package com.edwardhand.mobrider.managers;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.edwardhand.mobrider.commands.BaseCommand;

public class CommandManager
{
	protected List<BaseCommand> commands = new ArrayList<BaseCommand>();


	public boolean dispatch(CommandSender sender, Command command, String label, String[] args) {
		String input = label + " ";
		for (String s : args) {
			input = input + s + " ";
		}

		BaseCommand match = null;
		String[] trimmedArgs = (String[])null;
		StringBuilder identifier = new StringBuilder();

		for (BaseCommand cmd : this.commands) {
			StringBuilder tmpIdentifier = new StringBuilder();
			String[] tmpArgs = cmd.validate(input, tmpIdentifier);
			if (tmpIdentifier.length() > identifier.length()) {
				identifier = tmpIdentifier;
				match = cmd;
				trimmedArgs = tmpArgs;
			}
		}

		if (match != null) {
			if (trimmedArgs != null) {
				match.execute(sender, trimmedArgs);
				return true;
			}
			sender.sendMessage("§cUsage: " + match.getUsage());
		}

		return true;
	}

	public void addCommand(BaseCommand command) {
		this.commands.add(command);
	}

	public void removeCommand(BaseCommand command) {
		this.commands.remove(command);
	}

	public List<BaseCommand> getCommands() {
		return this.commands;
	}
}