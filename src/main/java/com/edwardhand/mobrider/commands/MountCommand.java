/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2013, R. Ramos <http://github.com/mung3r/>
 * MobRider is licensed under the GNU Lesser General Public License.
 *
 * MobRider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobRider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.edwardhand.mobrider.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

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
                LivingEntity target = EntityUtils.getNearByTarget(player, (int) configManager.mountRange);
                if (player.getItemInHand().getType() == Material.SADDLE && RiderManager.canRide(player, target)) {
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
