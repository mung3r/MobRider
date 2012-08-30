package com.edwardhand.mobrider.commons;

import net.citizensnpcs.Citizens;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bekvon.bukkit.residence.Residence;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import couk.Adamki11s.Regios.API.RegiosAPI;

public class DependencyUtils
{
    private static Plugin vaultPlugin;
    private static WorldGuardPlugin worldGuardPlugin;
    private static DestinationFactory destinationFactory;
    private static Residence residencePlugin;
    private static RegiosAPI regiosAPI;
    private static Towny townyPlugin;
    private static Plugin factionsPlugin;
    private static Citizens citizensPlugin;
    private static Plugin spoutPlugin;

    private static Permission permission;
    private static Economy economy;

    private DependencyUtils()
    {
    }

    public static void init()
    {
        initPlugins();
        initVault();
    }

    private static void initPlugins()
    {
        vaultPlugin = getPlugin("Vault", "net.milkbowl.vault.Vault");
        worldGuardPlugin = (WorldGuardPlugin) getPlugin("WorldGuard", "com.sk89q.worldguard.bukkit.WorldGuardPlugin");
        residencePlugin = (Residence) getPlugin("Residence", "com.bekvon.bukkit.residence.Residence");
        citizensPlugin = (Citizens) getPlugin("Citizens", "net.citizensnpcs.Citizens");
        townyPlugin = (Towny) getPlugin("Towny", "com.palmergames.bukkit.towny.Towny");
        factionsPlugin = getPlugin("Factions", "com.massivecraft.factions.P");
        spoutPlugin = getPlugin("Spout", "org.getspout.spout.Spout");

        Plugin regiosPlugin = getPlugin("Regios", "couk.Adamki11s.Regios.Main.Regios");
        if (regiosPlugin != null) {
            regiosAPI = new RegiosAPI();
        }

        Plugin multiversePlugin = getPlugin("Multiverse-Core", "com.onarandombox.MultiverseCore.MultiverseCore");
        if (multiversePlugin != null) {
            destinationFactory = ((MultiverseCore) multiversePlugin).getDestFactory();
        }
    }

    private static void initVault()
    {
        if (hasVault()) {
            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
                MRLogger.getInstance().info("Found permission provider " + permission.getName());
            }

            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                MRLogger.getInstance().info("Found economy provider " + economy.getName());
            }
        }

        if (!hasPermission()) {
            MRLogger.getInstance().warning("Did not find permission provider");
        }

        if (!hasEconomy()) {
            MRLogger.getInstance().warning("Did not find economy provider");
        }
    }

    public static boolean hasPermission(Player player, String perm)
    {
        if (hasPermission()) {
            return permission.has(player.getWorld(), player.getName(), perm);
        }
        else {
            return player.hasPermission(perm);
        }
    }

    public static boolean hasPermission()
    {
        return permission != null;
    }

    public static Permission getPermission()
    {
        return permission;
    }

    public static boolean hasEconomy()
    {
        return economy != null;
    }

    public static Economy getEconomy()
    {
        return economy;
    }

    private static boolean hasVault()
    {
        return vaultPlugin != null;
    }

    public static boolean hasCitizens()
    {
        boolean hasCitizens = false;

        try {
            hasCitizens = citizensPlugin != null && Double.parseDouble(citizensPlugin.getDescription().getVersion()) < 2.0;
        }
        catch (NumberFormatException e) {
            // do nothing
        }

        return hasCitizens;
    }

    public static boolean hasCitizens2()
    {
        boolean hasCitizens2 = false;

        try {
            hasCitizens2 = citizensPlugin != null && Double.parseDouble(citizensPlugin.getDescription().getVersion()) >= 2.0;
        }
        catch (NumberFormatException e) {
            // do nothing
        }

        return hasCitizens2;
    }

    public static Plugin getCitizens()
    {
        return citizensPlugin;
    }

    public static boolean hasResidence()
    {
        return residencePlugin != null;
    }

    public static boolean hasRegios()
    {
        return regiosAPI != null;
    }

    public static RegiosAPI getRegiosAPI()
    {
        return regiosAPI;
    }

    public static boolean hasWorldGuard()
    {
        return worldGuardPlugin != null;
    }

    public static RegionManager getRegionManager(World world)
    {
        return worldGuardPlugin.getRegionManager(world);
    }

    public static boolean hasMultiverse()
    {
        return destinationFactory != null;
    }

    public static DestinationFactory getMVDestinationFactory()
    {
        return destinationFactory;
    }

    public static boolean hasSpout()
    {
        return spoutPlugin != null;
    }

    public static boolean hasTowny()
    {
        return townyPlugin != null;
    }

    public static boolean hasFactions()
    {
        return factionsPlugin != null;
    }

    private static Plugin getPlugin(String pluginName, String className)
    {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        try {
            Class<?> testClass = Class.forName(className);
            if (testClass.isInstance(plugin) && plugin.isEnabled()) {
                MRLogger.getInstance().info("Found plugin " + plugin.getDescription().getName());
                return plugin;
            }
        }
        catch (ClassNotFoundException e) {
            MRLogger.getInstance().warning("Did not find plugin " + pluginName);
        }
        return null;
    }
}