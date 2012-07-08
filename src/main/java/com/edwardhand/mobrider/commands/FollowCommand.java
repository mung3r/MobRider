package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.Rider;

public class FollowCommand extends BasicCommand
{
    private RiderManager riderManager;
    private GoalManager goalManager;

    public FollowCommand(MobRider plugin)
    {
        super("Follow");
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        setDescription("Follow another player or mob");
        setUsage("/mob follow §9<player|mob>");
        setArgumentRange(1, 1);
        setIdentifiers("follow");
        setPermission("mobrider.command.follow");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (riderManager.isRider(player)) {
                Rider rider = riderManager.getRider(player);
                goalManager.setFollowGoal(rider, args[0]);
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
