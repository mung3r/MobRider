package com.edwardhand.mobrider.commands;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;

public class BuckCommand extends BasicCommand
{
    private Permission permission;

    public BuckCommand(MobRider plugin)
    {
        super("Buck");
        permission = plugin.getPermission();
        setDescription("Buck a player riding you");
        setUsage("/mob buck");
        setArgumentRange(0, 0);
        setIdentifiers("buck");
        setPermission("mobrider.command.buck");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Entity rider = player.getPassenger();

            if (rider instanceof LivingEntity && permission.has(player, "mobrider.command.buck")) {
                ((CraftLivingEntity) rider).getHandle().setPassengerOf(null);
            }
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }

}
