/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.edwardhand.mobrider.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;

public class CommandHandler {

    protected LinkedHashMap<String, Command> commands;

    public CommandHandler() {
        commands = new LinkedHashMap<String, Command>();
    }

    public void addCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public void removeCommand(Command command) {
        commands.remove(command);
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public List<Command> getCommands() {
        return new ArrayList<Command>(commands.values());
    }

    public boolean dispatch(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
      
    	String[] arguments;
        if (args.length < 1) {
        	arguments = new String[]{ command.getName() };
        } else {
        	arguments = args;
        }
        
        for (int argsIncluded = arguments.length; argsIncluded >= 0; argsIncluded--) {
        	String identifier = "";
            for (int i = 0; i < argsIncluded; i++) {
                identifier += " " + arguments[i];
            }
            
            identifier = identifier.trim();            
            for (Command cmd : commands.values()) {
                if (cmd.isIdentifier(sender, identifier)) {
                    String[] realArgs = Arrays.copyOfRange(arguments, argsIncluded, arguments.length);

                    if (!cmd.isInProgress(sender)) {
                        if (realArgs.length < cmd.getMinArguments() || realArgs.length > cmd.getMaxArguments()) {
                            displayCommandHelp(cmd, sender);
                            return true;
                        } else if (realArgs.length > 0 && realArgs[0].equals("?")) {
                            displayCommandHelp(cmd, sender);
                            return true;
                        }
                    }

                    if (!hasPermission(sender, cmd.getPermission())) {
                        sender.sendMessage("Insufficient permission.");
                        return true;
                    }

                    cmd.execute(sender, identifier, realArgs);
                    return true;
                }
            }
        }

        return true;
    }

    private void displayCommandHelp(Command cmd, CommandSender sender) {
        sender.sendMessage("§cCommand:§e " + cmd.getName());
        sender.sendMessage("§cDescription:§e " + cmd.getDescription());
        sender.sendMessage("§cUsage:§e " + cmd.getUsage());
        if (cmd.getNotes() != null) {
            for (String note : cmd.getNotes()) {
                sender.sendMessage("§e" + note);
            }
        }
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (!(sender instanceof Player) || permission == null || permission.isEmpty()) {
            return true;
        }
        return MobRider.hasPermission((Player) sender, permission);
    }
}
