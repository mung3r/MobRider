package com.edwardhand.mobrider;

import com.edwardhand.mobrider.commands.CommandManager;
import com.edwardhand.mobrider.commands.AttackCommand;
import com.edwardhand.mobrider.commands.FollowCommand;
import com.edwardhand.mobrider.commands.GoCommand;
import com.edwardhand.mobrider.commands.GotoCommand;
import com.edwardhand.mobrider.commands.StopCommand;
import com.edwardhand.mobrider.listeners.RiderDamageListener;
import com.edwardhand.mobrider.listeners.RiderTargetListener;
import com.edwardhand.mobrider.listeners.RiderPlayerListener;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRLogger;
import com.edwardhand.mobrider.utils.MRHandler;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MobRider extends JavaPlugin
{
    public static Permission permission;

    private final RiderPlayerListener riderListener = new RiderPlayerListener(this);
    private final RiderTargetListener riderEntityListener = new RiderTargetListener();
    private final RiderDamageListener riderEntityActionlistener = new RiderDamageListener(this);

    private static MRLogger log = new MRLogger();
    private CommandManager commandManager;
    private MRHandler rideHandler;
    private MRConfig config;

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
        commandManager = new CommandManager();

        commandManager.addCommand(new AttackCommand(this));
        commandManager.addCommand(new FollowCommand(this));
        commandManager.addCommand(new GoCommand(this));
        commandManager.addCommand(new GotoCommand(this));
        commandManager.addCommand(new StopCommand(this));
    }

    private void registerEvents()
    {
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, riderEntityListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, riderEntityListener, Event.Priority.Normal, this);

        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, riderEntityActionlistener, Event.Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, riderListener, Event.Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, riderListener, Event.Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM_HELD, riderListener, Event.Priority.Monitor, this);
    }

    @Override
    public void onEnable()
    {
        log.setName(this.getDescription().getName());
        config = new MRConfig(this);
        rideHandler = new MRHandler(this);

        try {
            if (!setupDependencies())
                log.warning("Missing permissions - everything is allowed!");
        }
        catch (NoClassDefFoundError e) {
            log.warning("Vault not found - everything is allowed!");
        }

        registerCommands();
        registerEvents();

        if (getServer().getScheduler().scheduleSyncRepeatingTask(this, rideHandler, 5L, 1L) < 0) {
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
        return commandManager.dispatch(sender, cmd, commandLabel, args);
    }

    public MRLogger getLogger()
    {
        return log;
    }

    public MRHandler getRideHandler()
    {
        return rideHandler;
    }

    public static boolean hasPermission(Player player, String name)
    {
        if (permission != null) {
            return permission.has(player, name);
        }
        return player.hasPermission(name);
    }
}
