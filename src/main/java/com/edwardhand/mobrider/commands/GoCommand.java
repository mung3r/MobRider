package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.DirectionType;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;

public class GoCommand extends BasicCommand
{
    private MobRider plugin = null;

    public GoCommand(MobRider plugin)
    {
        super("Go");
        this.plugin = plugin;
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
            Ride ride = plugin.getRideHandler().getRide(player);

            if (args.length == 1 && DirectionType.fromName(args[0]) != null) {
                ride.setDirection(DirectionType.fromName(args[0]).getDirection());
            }
            else if (args.length == 2 && DirectionType.fromName(args[0]) != null && MRUtil.isNumber(args[1])) {
                ride.setDirection(DirectionType.fromName(args[0]).getDirection(), Integer.parseInt(args[1]));
            }
            else {
                ride.speak(MRConfig.goConfusedMessage);
            }
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }
}
