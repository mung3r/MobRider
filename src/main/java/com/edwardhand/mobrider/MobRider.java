package com.edwardhand.mobrider;

import java.io.IOException;

import com.edwardhand.mobrider.commands.BuckCommand;
import com.edwardhand.mobrider.commands.CommandHandler;
import com.edwardhand.mobrider.commands.AttackCommand;
import com.edwardhand.mobrider.commands.FollowCommand;
import com.edwardhand.mobrider.commands.GoCommand;
import com.edwardhand.mobrider.commands.GotoCommand;
import com.edwardhand.mobrider.commands.HelpCommand;
import com.edwardhand.mobrider.commands.MountCommand;
import com.edwardhand.mobrider.commands.StopCommand;
import com.edwardhand.mobrider.listeners.RiderDamageListener;
import com.edwardhand.mobrider.listeners.RiderTargetListener;
import com.edwardhand.mobrider.listeners.RiderPlayerListener;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.MessageManager;
import com.edwardhand.mobrider.managers.MetricsManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.utils.MRLogger;
import com.edwardhand.mobrider.utils.MRUpdate;

import net.citizensnpcs.Citizens;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MobRider extends JavaPlugin
{
    private static final MRLogger log = new MRLogger();
    private static final String DEV_BUKKIT_URL = "http://dev.bukkit.org/server-mods/mobrider";
    private static final long CHECK_DELAY = 0;
    private static final long CHECK_PERIOD = 432000;

    private Permission permission;
    private CommandHandler commandHandler;
    private RiderManager riderManager;
    private GoalManager goalManager;
    private MessageManager messageManager;
    private ConfigManager config;
    private MetricsManager metrics;
    private boolean hasCitizens;

    @Override
    public void onEnable()
    {
        log.setName(this.getDescription().getName());

        setupPermission();
        setupMetrics();
        setupCitizens();

        config = new ConfigManager(this);
        messageManager = new MessageManager();
        goalManager = new GoalManager(this);
        riderManager = new RiderManager(this);

        registerCommands();
        registerEvents();

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new MRUpdate(this, DEV_BUKKIT_URL), CHECK_DELAY, CHECK_PERIOD);

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

    public Permission getPermission()
    {
        return permission;
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

    public boolean hasCitizens()
    {
        return hasCitizens;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public static MRLogger getMRLogger()
    {
        return log;
    }

    private void setupPermission()
    {
        try {
            RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
            if (permissionProvider != null) {
                permission = permissionProvider.getProvider();
            }

            if (permission == null) {
                log.warning("Missing permissions - everything is allowed!");
            }
        }
        catch (NoClassDefFoundError e) {
            log.warning("Vault not found - everything is allowed!");
        }
    }

    private void setupMetrics()
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

    private void setupCitizens()
    {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Citizens");
        if (plugin instanceof Citizens) {
            hasCitizens = true;
            log.info("Successfully hooked " + plugin.getDescription().getName());
        }
    }

    private void registerCommands()
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
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new RiderPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RiderTargetListener(), this);
        Bukkit.getPluginManager().registerEvents(new RiderDamageListener(this), this);
    }
}
