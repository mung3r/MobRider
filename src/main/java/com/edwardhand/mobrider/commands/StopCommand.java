package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;

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

            if (plugin.getRiderHandler().isRider(player)) {
                Rider rider = plugin.getRiderHandler().getRider(player);
                rider.stop();
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
