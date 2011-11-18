package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;

public class StopCommand extends BaseCommand
{
    private MobRider plugin = null;

    public StopCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob stop";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mob stop");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Ride ride = plugin.getRideHandler().getRide(player);

        ride.stop();
    }
}
