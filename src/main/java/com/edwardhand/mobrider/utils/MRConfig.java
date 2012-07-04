package com.edwardhand.mobrider.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

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
    public static String fedConfirmedMessage;
    public static String fedConfusedMessage;

    private static final String CONFIG_FILE = "config.yml";

    private FileConfiguration config;
    private MobRider plugin;
    private Set<Material> food;

    public MRConfig(MobRider plugin)
    {
        this.plugin = plugin;

        config = getConfig(new File(plugin.getDataFolder(), CONFIG_FILE));

        ConfigurationSection range = config.getConfigurationSection("range");
        MAX_TRAVEL_DISTANCE = Double.valueOf(range.getDouble("max_travel_distance", 100)).intValue();
        MAX_SEARCH_RANGE = range.getDouble("max_search_distance", 100);
        ATTACK_RANGE = range.getDouble("attack_range", 10);
        MOUNT_RANGE = range.getDouble("mount_range", 3);

        ConfigurationSection messageSuffix = config.getConfigurationSection("noise_suffix");
        attackConfirmedMessage = messageSuffix.getString("attack_confirmed", "!");
        attackConfusedMessage = messageSuffix.getString("attack_confused", "?");
        followConfirmedMessage = messageSuffix.getString("follow_confirmed", "!");
        followConfusedMessage = messageSuffix.getString("follow_confused", "?");
        goConfirmedMessage = messageSuffix.getString("go_confirmed", "");
        goConfusedMessage = messageSuffix.getString("go_confused", "?");
        stopConfirmedMessage = messageSuffix.getString("stop_confirmed", "");
        fedConfirmedMessage = messageSuffix.getString("fed_confirmed", " :D");
        fedConfusedMessage = messageSuffix.getString("fed_confused", "?");

        food = new HashSet<Material>();
        List<String> materials = config.getStringList("food");
        for (String material : materials) {
            food.add(Material.matchMaterial(material));
        }

        ConfigurationSection mobs = config.getConfigurationSection("mobs");
        for (String name : mobs.getKeys(false)) {
            ConfigurationSection mob = mobs.getConfigurationSection(name);
            new RideType(EntityType.fromName(name), Double.valueOf(mob.getDouble("speed", 0.2)).floatValue(), mob.getString("noise", ""));
        }
    }

    public boolean isFood(Material material)
    {
        return food.contains(material);
    }

    private FileConfiguration getConfig(File file)
    {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                InputStream inputStream = MRConfig.class.getResourceAsStream("/" + file.getName());
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = inputStream.read(buffer)) > 0)
                    outputStream.write(buffer, 0, length);

                inputStream.close();
                outputStream.close();

                MobRider.getMRLogger().info("Default config created successfully!");
            }
            catch (Exception e) {
                MobRider.getMRLogger().warning("Default config could not be created!");
            }
        }

        return plugin.getConfig();
    }
}
