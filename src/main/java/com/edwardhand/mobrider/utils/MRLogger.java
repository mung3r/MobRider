package com.edwardhand.mobrider.utils;

import java.util.logging.Logger;

import com.edwardhand.mobrider.MobRider;

public class MRLogger
{
    private MobRider plugin;
    private Logger logger;

    public MRLogger(MobRider plugin)
    {
        this.plugin = plugin;
        logger = Logger.getLogger("Minecraft");
    }

    public void info(String msg)
    {
        logger.info(format(msg));
    }

    public void warning(String msg)
    {
        logger.warning(format(msg));
    }

    public void severe(String msg)
    {
        logger.severe(format(msg));
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", plugin.getDescription().getName(), msg);
    }
}
