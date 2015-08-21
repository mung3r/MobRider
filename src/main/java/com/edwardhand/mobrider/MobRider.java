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
package com.edwardhand.mobrider;

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
import com.edwardhand.mobrider.commons.LoggerUtil;
import com.edwardhand.mobrider.commons.UpdateTask;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.listeners.RiderDamageListener;
import com.edwardhand.mobrider.listeners.RiderTargetListener;
import com.edwardhand.mobrider.listeners.RiderPlayerListener;
import com.edwardhand.mobrider.metrics.RideMetrics;
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
    private RideMetrics metrics;

    @Override
    public void onEnable()
    {
        DependencyUtils.init();

        metrics = new RideMetrics(this);
        config = new ConfigManager(this);
        goalManager = new GoalManager(this);
        riderManager = new RiderManager(this);

        addCommands();
        registerEvents();

        new UpdateTask(this);

        LoggerUtil.getInstance().info(getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        config.save();
        LoggerUtil.getInstance().info(getDescription().getVersion() + " disabled.");
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

    public RideMetrics getMetricsManager()
    {
        return metrics;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    private void addCommands()
    {
        commandHandler = new CommandHandler();

        commandHandler.addCommand(new AttackCommand(this));
        commandHandler.addCommand(new FollowCommand(this));
        commandHandler.addCommand(new GoCommand(this));
        commandHandler.addCommand(new GotoCommand(this));
        commandHandler.addCommand(new StopCommand(this));
        commandHandler.addCommand(new HelpCommand(this));
        commandHandler.addCommand(new MountCommand(this));
        commandHandler.addCommand(new BuckCommand());
        commandHandler.addCommand(new ReloadCommand(this));
    }

    private void registerEvents()
    {
        Bukkit.getPluginManager().registerEvents(new RiderPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RiderTargetListener(), this);
        Bukkit.getPluginManager().registerEvents(new RiderDamageListener(this), this);
    }
}
