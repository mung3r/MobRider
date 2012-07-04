package com.edwardhand.mobrider.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRHandler;
import com.edwardhand.mobrider.utils.MRUtil;

public class GotoCommand extends BasicCommand
{
    private MRHandler riderHandler;

    public GotoCommand(MobRider plugin)
    {
        super("Goto");
        riderHandler = plugin.getRiderHandler();
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

            if (riderHandler.isRider(player)) {
                Rider rider = riderHandler.getRider(player);
    
                if (args.length == 1) {
                    rider.follow(args[0]);
                }
                else if (args.length == 2 && MRUtil.isNumber(args[0]) && MRUtil.isNumber(args[1])) {
                    rider.setDestination(new Location(rider.getWorld(), Integer.parseInt(args[0]), 64.0D, Integer.parseInt(args[1])));
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
