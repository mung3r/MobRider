/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2015, R. Ramos <http://github.com/mung3r/>
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

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.MessageUtils;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

public class GotoCommand extends BasicCommand
{
    private static final double INTERIM_DISTANCE_SQUARED = 64.0D;

    private ConfigManager configManager;
    private RiderManager riderManager;
    private GoalManager goalManager;

    public GotoCommand(MobRider plugin)
    {
        super("Goto");
        configManager = plugin.getConfigManager();
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
        setDescription("Go to a player, mob or location");
        setUsage("/mob goto §9<player|mob|location> | <x> <z>");
        setArgumentRange(1, 2);
        setIdentifiers("goto");
        setPermission("mobrider.command.goto");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (riderManager.isRider(player)) {
                Rider rider = riderManager.getRider(player);

                if (args.length == 1) {
                    goalManager.setGotoGoal(rider, args[0]);
                }
                else if (args.length == 2 && EntityUtils.isInteger(args[0]) && EntityUtils.isInteger(args[1])) {
                    goalManager.setDestination(rider, new Location(rider.getWorld(), Integer.parseInt(args[0]), INTERIM_DISTANCE_SQUARED, Integer.parseInt(args[1])));
                }
                else {
                    MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
                }
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
