package com.edwardhand.mobrider.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.MessageManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRUtil;

public class GotoCommand extends BasicCommand
{
    private RiderManager riderManager;
    private GoalManager goalManager;
    private MessageManager messageManager;

    public GotoCommand(MobRider plugin)
    {
        super("Goto");
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        messageManager = plugin.getMessageManager();
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
                    goalManager.setFollowGoal(rider, args[0]);
                }
                else if (args.length == 2 && MRUtil.isNumber(args[0]) && MRUtil.isNumber(args[1])) {
                    goalManager.setDestination(rider, new Location(rider.getWorld(), Integer.parseInt(args[0]), 64.0D, Integer.parseInt(args[1])));
                }
                else {
                    messageManager.sendMessage(rider, ConfigManager.goConfusedMessage);
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
