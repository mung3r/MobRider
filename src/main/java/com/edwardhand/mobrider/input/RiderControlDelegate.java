package com.edwardhand.mobrider.input;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;
import org.getspout.spoutapi.keyboard.Keyboard;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.Rider;

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
                MobRider.getMRLogger().warning("Unrecognized key pressed");
                break;
        }

        location.setYaw(yaw % 360);
        return location.getDirection().normalize();
    }
}
