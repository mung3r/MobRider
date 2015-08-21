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

import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.edwardhand.mobrider.MobRider;

public class UpdateTask implements Runnable
{
    private static final String DEV_BUKKIT_URL = "http://dev.bukkit.org/server-mods/mobrider";
    private static final long CHECK_DELAY = 0;
    private static final long CHECK_PERIOD = 432000;

    private String pluginName;
    private String pluginVersion;
    private String latestVersion;

    public UpdateTask(MobRider plugin)
    {
        pluginName = plugin.getName();
        pluginVersion = plugin.getDescription().getVersion().split("-")[0];
        latestVersion = pluginVersion;

        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, CHECK_DELAY, CHECK_PERIOD);
        if (task.getTaskId() < 0) {
            LoggerUtil.getInstance().warning("Failed to schedule UpdateTask task.");
        }
    }

    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
    }

    public String getPluginName()
    {
        return pluginName;
    }

    private void getLatestVersion()
    {
        try {
            URL url = new URL(DEV_BUKKIT_URL + "/files.rss");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) {
                Element firstElement = (Element) firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                latestVersion = firstNodes.item(0).getNodeValue().replace(pluginName, "").replaceFirst("v", "").trim();
            }
        }
        catch (Exception e) {
            LoggerUtil.getInstance().warning(e.getMessage());
        }
    }

    private boolean isOutOfDate()
    {
        boolean isOutOfDate = false;

        getLatestVersion();
        try {
            isOutOfDate = Double.parseDouble(pluginVersion.replaceFirst("\\.", "")) < Double.parseDouble(latestVersion.replaceFirst("\\.", ""));
        }
        catch (NumberFormatException e) {
            LoggerUtil.getInstance().warning(e.getMessage());
        }

        return isOutOfDate;
    }

    @Override
    public void run()
    {
        if (isOutOfDate()) {
            LoggerUtil.getInstance().warning(pluginName + " " + latestVersion + " is out! You are running: " + pluginName + " " + pluginVersion);
            LoggerUtil.getInstance().warning("Update MobRider at: " + DEV_BUKKIT_URL);
        }
    }
}
