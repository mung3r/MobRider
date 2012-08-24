package com.edwardhand.mobrider.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.MessageUtils;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

public class GotoCommand extends BasicCommand
{
    private ConfigManager configManager;
    private RiderManager riderManager;
    private GoalManager goalManager;

    public GotoCommand(MobRider plugin)
    {
        super("Goto");
        configManager = plugin.getConfigManager();
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        setDescription("Go to a player, mob or location");
        setUsage("/mob goto §9<player|mob> | <x> <z>");
        setArgumentRange(1, 2);
        setIdentifiers("goto");
        setPermission("mobrider.command.goto");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (riderManager.isRider(player)) {
                Rider rider = riderManager.getRider(player);

                if (args.length == 1) {
                    goalManager.setGotoGoal(rider, args[0]);
                }
                else if (args.length == 2 && EntityUtils.isInteger(args[0]) && EntityUtils.isInteger(args[1])) {
                    goalManager.setDestination(rider, new Location(rider.getWorld(), Integer.parseInt(args[0]), 64.0D, Integer.parseInt(args[1])));
                }
                else {
                    MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
                }
            }
            else {
                sender.sendMessage("You must be riding a mob to use this command!");
            }
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }
}
