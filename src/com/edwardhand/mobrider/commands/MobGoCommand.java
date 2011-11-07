package com.edwardhand.mobrider.commands;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.MobManager;
import com.edwardhand.mobrider.models.MobIntent;
import com.edwardhand.mobrider.utils.IsDigits;

public class MobGoCommand extends BaseCommand
{
    private MobRider plugin = null;

    public MobGoCommand(MobRider plugin)
    {
        this.plugin = plugin;
        this.usage = "/mob go <direction> [distance]";
        this.minArgs = 1;
        this.maxArgs = 2;
        this.identifiers.add("mob go");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args)
    {
        if (!(commandSender instanceof Player))
            return;

        Player player = (Player) commandSender;
        Entity vehicle = ((CraftPlayer) commandSender).getHandle().vehicle;

        Vector direction = MobRider.directions.get(args[0].toUpperCase());
        int distance = args.length >= 2 ? parseDistance(args[1]) : 100;

        if (vehicle instanceof EntityCreature) {
            if (MobRider.directions.containsKey(args[0].toUpperCase())) {
                direction.multiply(Math.min(2.5D, distance / 100.0D));
                plugin.getMobManager().setRelative(player, direction.getX(), player.getWorld().getHighestBlockYAt((int) direction.getX(), (int) direction.getZ()) + 1, direction.getZ(), MobIntent.PASSIVE);
                ((EntityCreature) vehicle).setTarget(null);
                MobManager.speak(player, vehicle.getBukkitEntity(), "");
            }
            else {
                MobManager.speak(player, vehicle.getBukkitEntity(), "?");
            }
        }
    }

    private static Integer parseDistance(String arg)
    {
        if (IsDigits.check(arg)) {
            return Integer.parseInt(arg);
        }

        return 100;
    }
}
