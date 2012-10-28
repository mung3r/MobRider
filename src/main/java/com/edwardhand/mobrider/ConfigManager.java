/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package com.edwardhand.mobrider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.edwardhand.mobrider.commons.MRLogger;
import com.edwardhand.mobrider.rider.RideType;

public class ConfigManager
{
    public final Material controlItem;
    public final long updatePeriod;

    public final int maxTravelDistance;
    public final double maxSearchRange;
    public final double attackRange;
    public final double mountRange;
    public final double goalRange;

    public final String attackConfirmedMessage;
    public final String attackConfusedMessage;
    public final String followConfirmedMessage;
    public final String followConfusedMessage;
    public final String goConfirmedMessage;
    public final String goConfusedMessage;
    public final String stopConfirmedMessage;
    public final String fedConfirmedMessage;
    public final String fedConfusedMessage;

    private static final String CONFIG_FILE = "config.yml";

    private FileConfiguration config;
    private File configFile;
    private MobRider plugin;
    private Set<Material> food;

    public ConfigManager(MobRider plugin)
    {
        this.plugin = plugin;

        configFile = new File(plugin.getDataFolder(), CONFIG_FILE);
        config = getConfig(configFile);

        ConfigurationSection general = config.getConfigurationSection("general");
        MRLogger.getInstance().setDebug(general.getBoolean("debug", false));
        controlItem = Material.matchMaterial(general.getString("control_item"));
        updatePeriod = general.getLong("update_period");

        ConfigurationSection range = config.getConfigurationSection("range");
        maxTravelDistance = Double.valueOf(range.getDouble("max_travel_distance")).intValue();
        maxSearchRange = range.getDouble("max_search_distance");
        attackRange = range.getDouble("attack_range");
        mountRange = range.getDouble("mount_range");
        goalRange = range.getDouble("goal_range");

        ConfigurationSection messageSuffix = config.getConfigurationSection("noise_suffix");
        attackConfirmedMessage = messageSuffix.getString("attack_confirmed");
        attackConfusedMessage = messageSuffix.getString("attack_confused");
        followConfirmedMessage = messageSuffix.getString("follow_confirmed");
        followConfusedMessage = messageSuffix.getString("follow_confused");
        goConfirmedMessage = messageSuffix.getString("go_confirmed");
        goConfusedMessage = messageSuffix.getString("go_confused");
        stopConfirmedMessage = messageSuffix.getString("stop_confirmed");
        fedConfirmedMessage = messageSuffix.getString("fed_confirmed");
        fedConfusedMessage = messageSuffix.getString("fed_confused");

        food = new HashSet<Material>();
        List<String> materials = config.getStringList("food");
        for (String material : materials) {
            food.add(Material.matchMaterial(material));
        }

        ConfigurationSection mobs = config.getConfigurationSection("mobs");
        for (String name : mobs.getKeys(false)) {
            ConfigurationSection mob = mobs.getConfigurationSection(name);
            new RideType(EntityType.fromName(name), Double.valueOf(mob.getDouble("speed")).floatValue(), mob.getString("noise"), mob.getDouble("chance"), mob.getDouble("cost"));
        }
    }

    public void save()
    {
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            MRLogger.getInstance().severe(e.getMessage());
        }
    }

    public boolean isFood(Material material)
    {
        return food.contains(material);
    }

    private FileConfiguration getConfig(File file)
    {
        FileConfiguration newConfig = null;

        try {
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
                InputStream inputStream = plugin.getResource(file.getName());
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[8192];
                int length = 0;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                MRLogger.getInstance().info("Default config created successfully!");
            }

            newConfig = plugin.getConfig();
            newConfig.setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(file.getName())));
            newConfig.options().copyDefaults(true);
        }
        catch (Exception e) {
            MRLogger.getInstance().warning("Default config could not be created!");
        }

        return newConfig;
    }
}
