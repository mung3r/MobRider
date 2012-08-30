package com.edwardhand.mobrider.rider;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.getspout.spoutapi.keyboard.Keyboard;

import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.MRLogger;
import com.edwardhand.mobrider.goals.Goal;

public class Rider
{
    private static final float MIN_SPEED = 0.05F;

    private String playerName;
    private Goal goal;
    private final float maxSpeed;
    private float speed;
    private Set<Keyboard> keyPressedSet;

    public Rider(String playerName)
    {
        this.playerName = playerName;
        LivingEntity ride = getRide();
        maxSpeed = ride != null && getRideType() != null ? RideType.fromType(ride.getType()).getMaxSpeed() : MIN_SPEED;
        speed = maxSpeed;
        keyPressedSet = new HashSet<Keyboard>();
    }

    public Goal getGoal()
    {
        return goal;
    }

    public void setGoal(Goal goal)
    {
        this.goal = goal;
    }

    public float getSpeed()
    {
        return Math.min(maxSpeed, speed * getMaxHealth() / (float) getHealth());
    }

    public void setSpeed(float speed)
    {
        this.speed = Math.max(Math.min(speed, maxSpeed), MIN_SPEED);
    }

    public LivingEntity getTarget()
    {
        LivingEntity target = null;
        LivingEntity ride = getRide();

        if (ride instanceof Creature) {
            target = ((Creature) ride).getTarget();
        }

        return target;
    }

    public void setTarget(LivingEntity target)
    {
        LivingEntity ride = getRide();

        if (ride != null) {
            if (ride instanceof CraftCreature) {
                if (EntityUtils.hasNewAI(ride)) {
                    ((CraftCreature) ride).getHandle().b(target instanceof CraftLivingEntity ? ((CraftLivingEntity) target).getHandle() : null);
                }
                else {
                    ((CraftCreature) ride).setTarget(target);
                }
            }
            else if (ride instanceof CraftSlime) {
                // TODO: implement setTarget for slime
                MRLogger.getInstance().debug("setTaret not implemented for Slime");
            }
            else if (ride instanceof CraftGhast) {
                // TODO: implement setTarget for ghast
                MRLogger.getInstance().debug("setTaret not implemented for Ghast");
            }
            else if (ride instanceof CraftEnderDragon) {
                // TODO: implement setTarget for enderdragon
                MRLogger.getInstance().debug("setTaret not implemented for EnderDragon");
            }
        }
    }

    public int getHealth()
    {
        int health = 0;
        LivingEntity ride = getRide();
        if (ride != null) {
            health = ride.getHealth();
        }
        return health;
    }

    public void setHealth(int health)
    {
        LivingEntity ride = getRide();

        if (ride != null) {
            ride.setHealth(health);
        }
    }

    public int getMaxHealth()
    {
        int maxHealth = 0;
        LivingEntity ride = getRide();

        if (ride != null) {
            maxHealth = ride.getMaxHealth();
        }
        return maxHealth;
    }

    public boolean isFullHealth()
    {
        LivingEntity ride = getRide();

        return ride == null || (ride.getHealth() >= ride.getMaxHealth());
    }

    public boolean isValid()
    {
        Player player = getPlayer();
        return hasRide(player) && !player.isDead();
    }

    public Player getPlayer()
    {
        Player player = null;
        try {
            player = Bukkit.getPlayer(playerName);
        }
        catch (Exception e) {
            // do nothing, really
        }
        return player;
    }

    public LivingEntity getRide()
    {
        Player player = getPlayer();
        LivingEntity ride = null;

        if (hasRide(player)) {
            ride = (LivingEntity) player.getVehicle();
        }

        return ride;
    }

    public RideType getRideType()
    {
        RideType rideType = null;
        LivingEntity ride = getRide();

        if (ride != null) {
            rideType = RideType.fromType(ride.getType());
        }

        return rideType;
    }

    public World getWorld()
    {
        World world = null;
        Player player = getPlayer();

        if (player != null) {
            world = player.getWorld();
        }

        return world;
    }

    public boolean hasWaterCreature()
    {
        return getRide() instanceof Squid;
    }

    public boolean hasGoal()
    {
        return goal != null;
    }

    public void setKeyPressed(Keyboard key)
    {
        keyPressedSet.add(key);
    }

    public void setKeyReleased(Keyboard key)
    {
        keyPressedSet.remove(key);
    }

    public boolean isKeyPressed()
    {
        return !keyPressedSet.isEmpty();
    }

    private static boolean hasRide(Player player)
    {
        return player != null && player.getVehicle() instanceof LivingEntity;
    }
}
