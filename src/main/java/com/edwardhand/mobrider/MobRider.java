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
import com.edwardhand.mobrider.commands.ReloadCommand;
import com.edwardhand.mobrider.commands.StopCommand;
import com.edwardhand.mobrider.commons.DependencyUtils;
import com.edwardhand.mobrider.commons.MRLogger;
import com.edwardhand.mobrider.commons.UpdateTask;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.input.RiderControlDelegate;
import com.edwardhand.mobrider.listeners.RiderDamageListener;
import com.edwardhand.mobrider.listeners.RiderTargetListener;
import com.edwardhand.mobrider.listeners.RiderPlayerListener;
import com.edwardhand.mobrider.metrics.MetricsManager;
import com.edwardhand.mobrider.rider.RiderManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MobRider extends JavaPlugin
{
    private CommandHandler commandHandler;
    private RiderManager riderManager;
    private GoalManager goalManager;
    private ConfigManager config;
    private MetricsManager metrics;

    @Override
    public void onEnable()
    {
        DependencyUtils.init();
        initMetrics();

        config = new ConfigManager(this);
        goalManager = new GoalManager(this);
        riderManager = new RiderManager(this);

        addCommands();
        registerKeyBindings();
        registerEvents();

        new UpdateTask(this);

        MRLogger.getInstance().info(getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        config.save();
        MRLogger.getInstance().info(getDescription().getVersion() + " disabled.");
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

    public RiderManager getRiderManager()
    {
        return riderManager;
    }

    public GoalManager getGoalManager()
    {
        return goalManager;
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


    private void initMetrics()
    {
        try {
            metrics = new MetricsManager(this);
            metrics.setupGraphs();
            metrics.start();
        }
        catch (IOException e) {
            MRLogger.getInstance().warning("Metrics failed to load.");
        }
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
        if (DependencyUtils.hasSpout()) {
            new RiderControlDelegate(this);
        }
    }
}
