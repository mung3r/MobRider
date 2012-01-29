package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRUtil;

public class AttackCommand extends BasicCommand
{
    private MobRider plugin = null;

    public AttackCommand(MobRider plugin)
    {
        super("Attack");
        this.plugin = plugin;
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
            if (!MRUtil.isRider(player)) {
                sender.sendMessage("You must be riding a mob to use this command!");
                return true;
            }

            Ride ride = plugin.getRideHandler().getRide(player);
            ride.attack(args[0]);
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }
}
