package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;

public class FollowCommand extends BaseCommand
{
    private MobRider plugin = null;

    public FollowCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob follow <player/entity>";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mob follow");
        this.permission = "mobRider.command.follow";
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Ride ride = plugin.getRideHandler().getRide(player);

        ride.follow(args[0]);
    }
}
