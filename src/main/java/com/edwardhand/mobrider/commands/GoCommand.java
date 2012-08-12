package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.Utils;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.MessageManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.DirectionType;
import com.edwardhand.mobrider.models.Rider;

public class GoCommand extends BasicCommand
{
    private ConfigManager configManager;
    private RiderManager riderManager;
    private GoalManager goalManager;
    private MessageManager messageManager;

    public GoCommand(MobRider plugin)
    {
        super("Go");
        configManager = plugin.getConfigManager();
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        messageManager = plugin.getMessageManager();
        setDescription("Travel a direction with optional distance");
        setUsage("/mob go §9<direction> [distance]");
        setArgumentRange(1, 2);
        setIdentifiers("go");
        setPermission("mobrider.command.go");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (riderManager.isRider(player)) {
                Rider rider = riderManager.getRider(player);

                if (args.length == 1 && DirectionType.fromName(args[0]) != null) {
                    goalManager.setDirection(rider, DirectionType.fromName(args[0]).getDirection());
                }
                else if (args.length == 2 && DirectionType.fromName(args[0]) != null && Utils.isInteger(args[1])) {
                    goalManager.setDirection(rider, DirectionType.fromName(args[0]).getDirection(), Integer.parseInt(args[1]));
                }
                else {
                    messageManager.sendMessage(rider, configManager.goConfusedMessage);
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
