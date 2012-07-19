package com.edwardhand.mobrider.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;

public class KeyPressedListener implements Listener
{
    private static final Set<Keyboard> validKeys = new HashSet<Keyboard>();

    static {
        validKeys.add(Keyboard.KEY_RIGHT);
        validKeys.add(Keyboard.KEY_LEFT);
        validKeys.add(Keyboard.KEY_UP);
        validKeys.add(Keyboard.KEY_DOWN);
    }

    private GoalManager goalManager;
    private RiderManager riderManager;

    public KeyPressedListener(MobRider plugin)
    {
        goalManager = plugin.getGoalManager();
        riderManager = plugin.getRiderManager();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onKeyPressed(KeyPressedEvent event)
    {
        if (event.getPlayer().getActiveScreen() != ScreenType.GAME_SCREEN && validKeys.contains(event.getKey()) && riderManager.isRider(event.getPlayer())) {
            goalManager.setDirection(riderManager.getRider(event.getPlayer()), event.getKey());
        }
    }
}
