package com.edwardhand.mobrider.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.models.Rider;

public class MessageManager
{
    private static final int HEALTH_BARS = 6;

    public void sendMessage(Rider rider, String suffix)
    {
        Player player = rider.getPlayer();
        LivingEntity ride = rider.getRide();

        if (player != null && ride != null && !(ride instanceof HumanEntity)) {
            player.sendMessage("<" + getHealthString(rider) + "§e" + ride.getType().getName() + "§f> " + rider.getRideType().getNoise() + suffix);
        }
    }

    private String getHealthString(Rider rider)
    {
        double percentHealth = (rider.getHealth() * 100) / (double) rider.getMaxHealth();

        ChatColor barColor;

        if (percentHealth > 66) {
            barColor = ChatColor.GREEN;
        }
        else if (percentHealth > 33) {
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
