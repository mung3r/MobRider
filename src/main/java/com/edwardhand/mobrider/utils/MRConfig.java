package com.edwardhand.mobrider.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.CreatureType;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.RideType;

public class MRConfig
{
    public static int MAX_TRAVEL_DISTANCE;
    public static double MAX_SEARCH_RANGE;
    public static double ATTACK_RANGE;
    public static double MOUNT_RANGE;

    public static String attackConfirmedMessage;
    public static String attackConfusedMessage;
    public static String followConfirmedMessage;
    public static String followConfusedMessage;
    public static String goConfirmedMessage;
    public static String goConfusedMessage;
    public static String stopConfirmedMessage;
    public static String creatureFedMessage;

    private FileConfiguration config;
    private Set<Material> food;

    public MRConfig(MobRider plugin)
    {
        config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();

        ConfigurationSection range = plugin.getConfig().getConfigurationSection("range");
        MAX_TRAVEL_DISTANCE = Double.valueOf(range.getDouble("max_travel_distance", 100)).intValue();
        MAX_SEARCH_RANGE = range.getDouble("max_search_distance", 100);
        ATTACK_RANGE = range.getDouble("attack_range", 10);
        MOUNT_RANGE = range.getDouble("mount_range", 3);

        ConfigurationSection messageSuffix = plugin.getConfig().getConfigurationSection("noise_suffix");
        attackConfirmedMessage = messageSuffix.getString("attack_confirmed", "!");
        attackConfusedMessage = messageSuffix.getString("attack_confused", "?");
        followConfirmedMessage = messageSuffix.getString("follow_confirmed", "!");
        followConfusedMessage = messageSuffix.getString("follow_confused", "?");
        goConfirmedMessage = messageSuffix.getString("go_confirmed", "");
        goConfusedMessage = messageSuffix.getString("go_confused", "?");
        stopConfirmedMessage = messageSuffix.getString("stop_confirmed", "");
        creatureFedMessage = messageSuffix.getString("creature_fed", " :D");

        food = new HashSet<Material>();
        List<String> materials = plugin.getConfig().getStringList("food");
        for (String material : materials) {
            food.add(Material.matchMaterial(material));
        }

        ConfigurationSection mobs = plugin.getConfig().getConfigurationSection("mobs");
        for (String name : mobs.getKeys(false)) {
            ConfigurationSection mob = mobs.getConfigurationSection(name);
            new RideType(CreatureType.fromName(name), Double.valueOf(mob.getDouble("speed", 0.2)).floatValue(), mob.getString("noise", ""));
        }
    }

    public boolean isFood(Material material)
    {
        return food.contains(material);
    }
}
