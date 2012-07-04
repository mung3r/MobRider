package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRHandler;

public class AttackCommand extends BasicCommand
{
    private MRHandler riderHandler;

    public AttackCommand(MobRider plugin)
    {
        super("Attack");
        riderHandler = plugin.getRiderHandler();
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
 
            if (riderHandler.isRider(player)) {
                Rider rider = riderHandler.getRider(player);
                rider.attack(args[0]);
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
