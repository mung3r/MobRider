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
package com.edwardhand.mobrider.commons;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.rider.Rider;

public final class MessageUtils
{
    private static final int HEALTH_BARS = 6;
    private static final int GOOD_HEALTH_THRESHOLD = 66;
    private static final int BAD_HEALTH_THRESHOLD = 33;

    private MessageUtils()
    {
    }

    public static void sendMessage(Rider rider, String suffix)
    {
        Player player = rider.getPlayer();
        LivingEntity ride = rider.getRide();

        if (player != null && ride != null && rider.getRideType() != null && !(ride instanceof HumanEntity)) {
            player.sendMessage("<" + getHealthString(rider) + "§e" + ride.getType().getName() + "§f> " + rider.getRideType().getNoise() + suffix);
        }
    }

    private static String getHealthString(Rider rider)
    {
        double percentHealth = (rider.getHealth() * 100) / rider.getMaxHealth();

        ChatColor barColor;

        if (percentHealth > GOOD_HEALTH_THRESHOLD) {
            barColor = ChatColor.GREEN;
        }
        else if (percentHealth > BAD_HEALTH_THRESHOLD) {
            barColor = ChatColor.GOLD;
        }
        else {
            barColor = ChatColor.RED;
        }

        StringBuilder healthString = new StringBuilder();
        double colorSwitch = Math.ceil((percentHealth / 100D) * HEALTH_BARS);

        for (int i = 0; i < HEALTH_BARS; i++) {
            ChatColor color = i < colorSwitch ? barColor : ChatColor.GRAY;
            healthString.append(color).append("|");
        }

        return healthString.toString();
    }
}
