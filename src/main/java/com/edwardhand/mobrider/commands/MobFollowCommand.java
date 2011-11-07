package com.edwardhand.mobrider.commands;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.MobManager;
import com.edwardhand.mobrider.models.MobIntent;

public class MobFollowCommand extends BaseCommand
{
    private MobRider plugin = null;

    public MobFollowCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob follow <player/entity>";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.identifiers.add("mob follow");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Entity vehicle = ((CraftPlayer) commandSender).getHandle().vehicle;

        if (vehicle instanceof EntityCreature) {
            LivingEntity target = plugin.findEntity(args[0], player);
            if (target != null) {
                plugin.getMobManager().setFollowing(player, target, MobIntent.PASSIVE);
                MobManager.speak(player, vehicle.getBukkitEntity(), "!");
            }
            else {
                MobManager.speak(player, vehicle.getBukkitEntity(), "?");
            }
        }
    }
}
