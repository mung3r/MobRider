package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRHandler;
import com.edwardhand.mobrider.utils.MRUtil;

public class MountCommand extends BasicCommand
{
    private MRHandler riderHandler;

    public MountCommand(MobRider plugin)
    {
        super("Mount");
        riderHandler = plugin.getRiderHandler();
        setDescription("Mount nearby mob");
        setUsage("/mob mount");
        setArgumentRange(0, 0);
        setIdentifiers("mount");
        setPermission("mobrider.command.mount");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Entity vehicle = player.getVehicle();

            if (vehicle instanceof LivingEntity) {
                ((CraftPlayer) player).getHandle().setPassengerOf(null);
            }
            else {
                LivingEntity target = MRUtil.getTargetLivingEntity(player);
                if (MRUtil.canRide(player, target)) {
                    target.setPassenger(player);
                    Rider rider = riderHandler.addRider(player);
                    rider.stop();
                }
            }
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }

}
