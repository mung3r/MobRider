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
package com.edwardhand.mobrider.commons;

import java.util.logging.Logger;

public final class LoggerUtil
{
    private static final String LOG_NAME = "MobRider";
    private static LoggerUtil instance = new LoggerUtil();

    private Logger logger;
    private String name;
    private boolean isDebug;

    public static LoggerUtil getInstance()
    {
        return instance;
    }

    private LoggerUtil()
    {
        logger = Logger.getLogger("Minecraft");
        isDebug = false;
        name = LOG_NAME;
    }

    public boolean isDebug()
    {
        return isDebug;
    }

    public void setDebug(boolean isDebug)
    {
        this.isDebug = isDebug;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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

    public void debug(String msg)
    {
        if (isDebug) {
            logger.info(format("DEBUG: " + msg));
        }
    }

    public String format(String msg)
    {
        return String.format("[%s] %s", name, msg);
    }
}
