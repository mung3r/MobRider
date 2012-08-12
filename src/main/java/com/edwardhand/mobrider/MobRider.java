package com.edwardhand.mobrider;

import java.io.IOException;

import com.bekvon.bukkit.residence.Residence;
import com.edwardhand.mobrider.commands.BuckCommand;
import com.edwardhand.mobrider.commands.CommandHandler;
import com.edwardhand.mobrider.commands.AttackCommand;
import com.edwardhand.mobrider.commands.FollowCommand;
import com.edwardhand.mobrider.commands.GoCommand;
import com.edwardhand.mobrider.commands.GotoCommand;
import com.edwardhand.mobrider.commands.HelpCommand;
import com.edwardhand.mobrider.commands.MountCommand;
import com.edwardhand.mobrider.commands.ReloadCommand;
import com.edwardhand.mobrider.commands.StopCommand;
import com.edwardhand.mobrider.commons.MRLogger;
import com.edwardhand.mobrider.commons.UpdateTask;
import com.edwardhand.mobrider.input.RiderControlDelegate;
import com.edwardhand.mobrider.listeners.RiderDamageListener;
import com.edwardhand.mobrider.listeners.RiderTargetListener;
import com.edwardhand.mobrider.listeners.RiderPlayerListener;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.MessageManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.metrics.MetricsManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.destination.DestinationFactory;
import com.palmergames.bukkit.towny.Towny;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

import couk.Adamki11s.Regios.API.RegiosAPI;

import net.citizensnpcs.Citizens;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MobRider extends JavaPlugin
{
    private static final MRLogger log = new MRLogger();

    private Permission permission;
    private Economy economy;
    private CommandHandler commandHandler;
    private RiderManager riderManager;
    private GoalManager goalManager;
    private MessageManager messageManager;
    private ConfigManager config;
    private MetricsManager metrics;
    private WorldGuardPlugin worldGuardPlugin;
    private DestinationFactory destinationFactory;
    private Residence residencePlugin;
    private RegiosAPI regiosAPI;
    private Towny townyPlugin;
    private Plugin factionsPlugin;
    private Citizens citizensPlugin;
    private Plugin spoutPlugin;

    @Override
    public void onEnable()
    {
        log.setName(this.getDescription().getName());

        initVault();
        initMetrics();
        initPlugins();

        config = new ConfigManager(this);
        messageManager = new MessageManager();
        goalManager = new GoalManager(this);
        riderManager = new RiderManager(this);

        addCommands();
        registerKeyBindings();
        registerEvents();

        new UpdateTask(this);

        log.info(getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        config.save();
        log.info(getDescription().getVersion() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return commandHandler.dispatch(sender, cmd, commandLabel, args);
    }

    @Override
    public void reloadConfig()
    {
        super.reloadConfig();
        config = new ConfigManager(this);
    }

    public Permission getPermission()
    {
        return permission;
    }

    public Economy getEconomy()
    {
        return economy;
    }

    public RiderManager getRiderManager()
    {
        return riderManager;
    }

    public GoalManager getGoalManager()
    {
        return goalManager;
    }

    public MessageManager getMessageManager()
    {
        return messageManager;
    }

    public ConfigManager getConfigManager()
    {
        return config;
    }

    public MetricsManager getMetricsManager()
    {
        return metrics;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public boolean hasCitizens()
    {
        return citizensPlugin != null;
    }

    public boolean hasResidence()
    {
        return residencePlugin != null;
    }

    public boolean hasRegios()
    {
        return regiosAPI != null;
    }

    public RegiosAPI getRegiosAPI()
    {
        return regiosAPI;
    }

    public boolean hasWorldGuard()
    {
        return worldGuardPlugin != null;
    }

    public RegionManager getRegionManager(World world)
    {
        return worldGuardPlugin.getRegionManager(world);
    }

    public boolean hasMultiverse()
    {
        return destinationFactory != null;
    }

    public DestinationFactory getMVDestinationFactory()
    {
        return destinationFactory;
    }

    public boolean hasSpout()
    {
        return spoutPlugin != null;
    }

    public boolean hasTowny()
    {
        return townyPlugin != null;
    }

    public boolean hasFactions()
    {
        return factionsPlugin != null;
    }

    public static MRLogger getMRLogger()
    {
        return log;
    }

    private void initVault()
    {
        Plugin vaultPlugin = getPlugin("Vault", "net.milkbowl.vault.Vault");

        if (vaultPlugin != null) {
            RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
                log.info("Found permission provider " + permission.getName());
            }

            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                log.info("Found economy provider " + economy.getName());
            }
        }

        if (permission == null) {
            log.warning("Did not find permission provider");
        }

        if (economy == null) {
            log.warning("Did not find economy provider");
        }
    }

    private void initMetrics()
    {
        try {
            metrics = new MetricsManager(this);
            metrics.setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            log.warning("Metrics failed to load.");
        }
    }

    private void initPlugins()
    {
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

    private Plugin getPlugin(String pluginName, String className)
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);
        try {
            Class<?> testClass = Class.forName(className);
            if (testClass.isInstance(plugin)) {
                log.info("Found plugin " + plugin.getDescription().getName());
                return plugin;
            }
        }
        catch (ClassNotFoundException e) {
            log.warning("Did not find plugin " + pluginName);
        }
        return null;
    }

    private void addCommands()
    {
        commandHandler = new CommandHandler(this);

        commandHandler.addCommand(new AttackCommand(this));
        commandHandler.addCommand(new FollowCommand(this));
        commandHandler.addCommand(new GoCommand(this));
        commandHandler.addCommand(new GotoCommand(this));
        commandHandler.addCommand(new StopCommand(this));
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new MountCommand(this));
        commandHandler.addCommand(new BuckCommand(this));
        commandHandler.addCommand(new ReloadCommand(this));
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new RiderPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RiderTargetListener(), this);
        Bukkit.getPluginManager().registerEvents(new RiderDamageListener(this), this);
    }

    private void registerKeyBindings()
    {
        if (hasSpout()) {
            new RiderControlDelegate(this);
        }
    }
}
