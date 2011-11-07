package com.edwardhand.mobrider.commands;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.MobManager;
import com.edwardhand.mobrider.models.MobIntent;

public class MobStopCommand extends BaseCommand
{
    private MobRider plugin = null;

    public MobStopCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob stop";
        this.minArgs = 0;
        this.maxArgs = 0;
        this.identifiers.add("mob stop");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Entity vehicle = ((CraftPlayer) commandSender).getHandle().vehicle;

        if (vehicle instanceof EntityCreature) {
            plugin.getMobManager().setAbsolute(player, vehicle.getBukkitEntity().getLocation(), MobIntent.STOP);
            ((EntityCreature) vehicle).setTarget(null);
            MobManager.speak(player, vehicle.getBukkitEntity(), "");
        }
    }
}
