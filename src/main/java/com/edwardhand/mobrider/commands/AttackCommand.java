package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

public class AttackCommand extends BasicCommand
{
    private RiderManager riderManager;
    private GoalManager goalManager;

    public AttackCommand(MobRider plugin)
    {
        super("Attack");
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        setDescription("Attack another player or mob");
        setUsage("/mob attack §9<player|mobs>");
        setArgumentRange(1, 1);
        setIdentifiers("attack");
        setPermission("mobrider.command.attack");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (riderManager.isRider(player)) {
                Rider rider = riderManager.getRider(player);
                goalManager.setAttackGoal(rider, args[0]);
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
