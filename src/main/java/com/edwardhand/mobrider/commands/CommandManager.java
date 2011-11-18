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
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        Entity vehicle = ((CraftPlayer) player).getHandle().vehicle;

        if (vehicle == null) {
            player.sendMessage("You must be riding a mob to use a /mob command.");
            return true;
        }
        else if (!(vehicle instanceof EntityCreature)) {
            player.sendMessage("You can't control this mob.");
            return true;
        }

        if (args.length < 1)
            return false;

        if (!MobRider.permission.playerHas(player, "mobRider.command." + args[0].toLowerCase())) {
            player.sendMessage("You do not have the necessary permissions.");
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
                match.execute(sender, trimmedArgs);
                return true;
            }
            sender.sendMessage("§cUsage: " + match.getUsage());
        }

        return true;
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
}
