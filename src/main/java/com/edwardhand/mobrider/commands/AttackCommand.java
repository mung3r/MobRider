package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;

public class AttackCommand extends BaseCommand
{
    private MobRider plugin = null;

    public AttackCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob attack <player/entity>";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mob attack");
        this.permission = "mobRider.command.attack";
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Ride ride = plugin.getRideHandler().getRide(player);

        ride.attack(args[0]);
    }
}
