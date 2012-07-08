package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.Rider;

public class StopCommand extends BasicCommand
{
    private RiderManager riderManager;
    private GoalManager goalManager;

    public StopCommand(MobRider plugin)
    {
        super("Stop");
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        setDescription("Stop your mob");
        setUsage("/mob stop");
        setArgumentRange(0, 0);
        setIdentifiers("stop");
        setPermission("mobrider.command.stop");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (riderManager.isRider(player)) {
                Rider rider = riderManager.getRider(player);
                goalManager.setStopGoal(rider);
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
