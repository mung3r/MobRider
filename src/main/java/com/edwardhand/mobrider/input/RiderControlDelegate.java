/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
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
package com.edwardhand.mobrider.input;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.LoggerUtil;
import com.edwardhand.mobrider.goals.GoalManager;
import com.edwardhand.mobrider.rider.Rider;
import com.edwardhand.mobrider.rider.RiderManager;

public class RiderControlDelegate implements BindingExecutionDelegate
{
    private GoalManager goalManager;
    private RiderManager riderManager;

    public RiderControlDelegate(MobRider plugin)
    {
        goalManager = plugin.getGoalManager();
        riderManager = plugin.getRiderManager();

        SpoutManager.getKeyBindingManager().registerBinding("GoForward", Keyboard.KEY_UP, "Go forward", this, plugin);
        SpoutManager.getKeyBindingManager().registerBinding("TurnLeft", Keyboard.KEY_LEFT, "Turn left", this, plugin);
        SpoutManager.getKeyBindingManager().registerBinding("GoBackward", Keyboard.KEY_DOWN, "Go backward", this, plugin);
        SpoutManager.getKeyBindingManager().registerBinding("TurnRight", Keyboard.KEY_RIGHT, "Turn right", this, plugin);
    }

    @Override
    public void keyPressed(KeyBindingEvent event)
    {
        Rider rider = riderManager.getRider(event.getPlayer());

        if (event.getPlayer().getActiveScreen() == ScreenType.GAME_SCREEN && rider.isValid()) {
            rider.setKeyPressed(event.getBinding().getDefaultKey());
            goalManager.setDirection(rider, convertKeyToDirection(rider, event.getBinding().getDefaultKey()));
        }
    }

    @Override
    public void keyReleased(KeyBindingEvent event)
    {
        Rider rider = riderManager.getRider(event.getPlayer());

        if (event.getPlayer().getActiveScreen() == ScreenType.GAME_SCREEN && rider.isValid()) {
            rider.setKeyReleased(event.getBinding().getDefaultKey());
            if (!rider.isKeyPressed()) {
                goalManager.setStopGoal(rider);
            }
        }
    }

    private Vector convertKeyToDirection(Rider rider, Keyboard key)
    {
        Location location = rider.getRide().getLocation();
        float yaw = location.getYaw();

        switch (key) {
            case KEY_UP:
                yaw = rider.getPlayer().getLocation().getYaw();
                break;
            case KEY_LEFT:
                yaw -= 45;
                break;
            case KEY_DOWN:
                yaw += 180;
                break;
            case KEY_RIGHT:
                yaw += 45;
                break;
            default:
                LoggerUtil.getInstance().warning("Unrecognized key pressed");
                break;
        }

        location.setYaw(yaw % 360);
        return location.getDirection().normalize();
    }
}
