package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRUtil;

public class StopCommand extends BasicCommand
{
    private MobRider plugin = null;

    public StopCommand(MobRider plugin)
    {
        super("Stop");
        this.plugin = plugin;
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
            if (!MRUtil.isRider(player)) {
                sender.sendMessage("You must be riding a mob to use this command!");
                return true;
            }

            Ride ride = plugin.getRideHandler().getRide(player);
            ride.stop();
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }
}
