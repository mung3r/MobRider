package com.edwardhand.mobrider;

import java.io.IOException;

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
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRLogger;
import com.edwardhand.mobrider.utils.MRHandler;
import com.edwardhand.mobrider.utils.MRMetrics;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MobRider extends JavaPlugin
{
    public static Permission permission;

    private static MRLogger log = new MRLogger();
    private static CommandHandler commandHandler = new CommandHandler();
    private MRHandler riderHandler;
    private MRConfig config;
    private MRMetrics metrics;

    private Boolean setupDependencies()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        return (permission != null);
    }

    private void registerCommands()
    {
        commandHandler = new CommandHandler();

        commandHandler.addCommand(new AttackCommand(this));
        commandHandler.addCommand(new FollowCommand(this));
        commandHandler.addCommand(new GoCommand(this));
        commandHandler.addCommand(new GotoCommand(this));
        commandHandler.addCommand(new StopCommand(this));
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new MountCommand(this));
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new RiderPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RiderTargetListener(), this);
        Bukkit.getPluginManager().registerEvents(new RiderDamageListener(this), this);
    }

    @Override
    public void onEnable()
    {
        log.setName(this.getDescription().getName());

        try {
            metrics = new MRMetrics(this);
            metrics.setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            log.warning("Metrics failed to load.");
        }

        config = new MRConfig(this);
        riderHandler = new MRHandler(this);

        try {
            if (!setupDependencies())
                log.warning("Missing permissions - everything is allowed!");
        }
        catch (NoClassDefFoundError e) {
            log.warning("Vault not found - everything is allowed!");
        }

        registerCommands();
        registerEvents();

        if (getServer().getScheduler().scheduleSyncRepeatingTask(this, riderHandler, 5L, 1L) < 0) {
            getServer().getPluginManager().disablePlugin(this);
            log.severe("Failed to schedule task.");
        }

        log.info(getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        log.info(getDescription().getVersion() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        return commandHandler.dispatch(sender, cmd, commandLabel, args);
    }

    public MRHandler getRiderHandler()
    {
        return riderHandler;
    }

    public MRConfig getMRConfig()
    {
        return config;
    }

    public MRMetrics getMetrics()
    {
        return metrics;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public static MRLogger getMRLogger()
    {
        return log;
    }

    public static boolean hasPermission(Player player, String name)
    {
        if (permission != null) {
            return permission.has(player, name);
        }
        return player.hasPermission(name);
    }
}
