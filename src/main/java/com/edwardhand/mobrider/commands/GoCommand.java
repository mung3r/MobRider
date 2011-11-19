package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.DirectionType;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;

public class GoCommand extends BaseCommand
{
    private MobRider plugin = null;

    public GoCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob go <direction> [distance]";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mob go");
        this.permission = "mobRider.command.go";
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Ride ride = plugin.getRideHandler().getRide(player);

        if (args.length == 1) {
            ride.setDirection(DirectionType.fromName(args[0]).getDirection());
        }
        else if (args.length == 2 && DirectionType.fromName(args[0]) != null && MRUtil.isNumber(args[1])) {
            ride.setDirection(DirectionType.fromName(args[0]).getDirection(), Integer.parseInt(args[1]));
        }
        else {
            ride.speak(MRConfig.GoConfusedMessage);
        }
    }
}
