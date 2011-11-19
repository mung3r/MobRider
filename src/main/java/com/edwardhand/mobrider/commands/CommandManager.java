/*
 * CommandManager from RightLegRed
 */

package com.edwardhand.mobrider.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;

public class CommandManager
{
    protected List<BaseCommand> commands = new ArrayList<BaseCommand>();

    public boolean dispatch(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length < 1) {
            return false;
        }
        else if (!isRider(sender)) {
            return true;
        }

        String input = label + " ";
        for (String s : args) {
            input = input + s + " ";
        }

        BaseCommand match = null;
        String[] trimmedArgs = (String[]) null;
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
                if (hasPermission(sender, match.getPermission())) {
                    match.execute(sender, trimmedArgs);
                }
                else {
                    sender.sendMessage("You do not have the necessary permissions.");
                }
            }
            sender.sendMessage("§cUsage: " + match.getUsage());
            return true;
        }

        return false;
    }

    public Boolean isRider(CommandSender sender)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Entity vehicle = ((CraftPlayer) player).getHandle().vehicle;

            if (vehicle == null) {
                player.sendMessage("You must be riding a mob to use a /mob command.");
                return false;
            }
            else if (!(vehicle instanceof EntityCreature)) {
                player.sendMessage("You can't control this mob.");
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

    public void addCommand(BaseCommand command)
    {
        this.commands.add(command);
    }

    public void removeCommand(BaseCommand command)
    {
        this.commands.remove(command);
    }

    public List<BaseCommand> getCommands()
    {
        return this.commands;
    }

    public static boolean hasPermission(CommandSender sender, String permission)
    {
        if (!(sender instanceof Player) || permission == null || permission.isEmpty()) {
            return true;
        }

        return MobRider.hasPermission((Player) sender, permission);
    }
}
