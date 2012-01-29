package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRUtil;

public class FollowCommand extends BasicCommand
{
    private MobRider plugin = null;

    public FollowCommand(MobRider plugin)
    {
        super("Follow");
        this.plugin = plugin;
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
            if (!MRUtil.isRider(player)) {
                sender.sendMessage("You must be riding a mob to use this command!");
                return true;
            }

            Ride ride = plugin.getRideHandler().getRide(player);
            ride.follow(args[0]);
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }
}
