package com.edwardhand.mobrider.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.Utils;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.Rider;

public class MountCommand extends BasicCommand
{
    private ConfigManager configManager;
    private RiderManager riderManager;
    private GoalManager goalManager;

    public MountCommand(MobRider plugin)
    {
        super("Mount");
        configManager = plugin.getConfigManager();
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
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
                riderManager.removeRider(player);
            }
            else {
                LivingEntity target = Utils.getNearByTarget(player, (int) configManager.MOUNT_RANGE);
                if (player.getItemInHand().getType() == Material.SADDLE && riderManager.canRide(player, target)) {
                    target.setPassenger(player);
                    Rider rider = riderManager.addRider(player);
                    goalManager.setStopGoal(rider);
                }
            }
        }
        else {
            sender.sendMessage("Console cannot control mobs!");
        }

        return true;
    }

}
