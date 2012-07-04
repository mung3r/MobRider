package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.DirectionType;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRHandler;
import com.edwardhand.mobrider.utils.MRUtil;

public class GoCommand extends BasicCommand
{
    private MRHandler riderHandler;

    public GoCommand(MobRider plugin)
    {
        super("Go");
        riderHandler = plugin.getRiderHandler();
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

            if (riderHandler.isRider(player)) {
                Rider rider = riderHandler.getRider(player);
    
                if (args.length == 1 && DirectionType.fromName(args[0]) != null) {
                    rider.setDirection(DirectionType.fromName(args[0]).getDirection());
                }
                else if (args.length == 2 && DirectionType.fromName(args[0]) != null && MRUtil.isNumber(args[1])) {
                    rider.setDirection(DirectionType.fromName(args[0]).getDirection(), Integer.parseInt(args[1]));
                }
                else {
                    rider.message(MRConfig.goConfusedMessage);
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
