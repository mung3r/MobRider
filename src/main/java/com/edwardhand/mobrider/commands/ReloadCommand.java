package com.edwardhand.mobrider.commands;

import org.bukkit.command.CommandSender;

import com.edwardhand.mobrider.MobRider;

public class ReloadCommand extends BasicCommand
{
    private MobRider plugin;

    public ReloadCommand(MobRider plugin)
    {
        super("Reload");
        this.plugin = plugin;
        setDescription("Reload configuration");
        setUsage("/mob reload");
        setArgumentRange(0, 0);
        setIdentifiers("reload");
        setPermission("mobrider.admin.reload");
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args)
    {
        plugin.reloadConfig();
        sender.sendMessage("MobRider config reloaded.");
        return true;
    }
}
