package com.edwardhand.mobrider.commands;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.MobManager;
import com.edwardhand.mobrider.models.MobIntent;
import com.edwardhand.mobrider.utils.IsDigits;

public class MobGotoCommand extends BaseCommand
{
    private MobRider plugin = null;

    public MobGotoCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob goto <player/entity> | <x> <z>";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mob goto");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Entity vehicle = ((CraftPlayer) commandSender).getHandle().vehicle;

        if (vehicle instanceof EntityCreature) {
            switch (args.length) {
                case 1:
                    LivingEntity target = plugin.findEntity(args[0], player);
                    if (target != null) {
                        plugin.getMobManager().setFollowing(player, target, MobIntent.PASSIVE);
                        MobManager.speak(player, vehicle.getBukkitEntity(), "");
                    }
                    else {
                        MobManager.speak(player, vehicle.getBukkitEntity(), "?");
                    }
                    break;
                case 2:
                    if (IsDigits.check(args[0]) && IsDigits.check(args[1])) {
                        Location destination = new Location(player.getWorld(), Integer.parseInt(args[0]), 64.0D, Integer.parseInt(args[1]));
                        plugin.getMobManager().setAbsolute(player, destination, MobIntent.PASSIVE);
                        ((EntityCreature) vehicle).setTarget(null);
                        MobManager.speak(player, vehicle.getBukkitEntity(), "");
                    }
                    else {
                        MobManager.speak(player, vehicle.getBukkitEntity(), "?");
                    }
                    break;
            }
        }
    }
}
