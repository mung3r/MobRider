package com.edwardhand.mobrider.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;

public class GotoCommand extends BaseCommand
{
    private MobRider plugin = null;

    public GotoCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob goto <player/entity> | <x> <z>";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mob goto");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Ride ride = plugin.getRideHandler().getRide(player);

        if (args.length == 1) {
            ride.follow(args[0]);
        }
        else if (args.length == 2 && MRUtil.isNumber(args[0]) && MRUtil.isNumber(args[1])) {
            ride.setDestination(new Location(ride.getWorld(), Integer.parseInt(args[0]), 64.0D, Integer.parseInt(args[1])));
        }
        else {
            ride.speak(MRConfig.GoConfusedMessage);
        }
    }
}
