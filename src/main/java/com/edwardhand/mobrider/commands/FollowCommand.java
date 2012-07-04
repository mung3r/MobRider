package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRHandler;

public class FollowCommand extends BasicCommand
{
    private MRHandler riderHandler;

    public FollowCommand(MobRider plugin)
    {
        super("Follow");
        riderHandler = plugin.getRiderHandler();
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
 
            if (riderHandler.isRider(player)) {
                Rider rider = riderHandler.getRider(player);
                rider.follow(args[0]);
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
