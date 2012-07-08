package com.edwardhand.mobrider.models;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;

import com.edwardhand.mobrider.goals.Goal;
import com.edwardhand.mobrider.utils.MRUtil;

public class Rider
{
    private static final float MIN_SPEED = 0.05F;

    private String playerName;
    private Goal goal;
    private final float maxSpeed;
    private float speed;

    public Rider(String playerName)
    {
        this.playerName = playerName;
        LivingEntity ride = getRide();
        maxSpeed = ride != null && getRideType() != null ? RideType.fromType(ride.getType()).getMaxSpeed() : MIN_SPEED;
        speed = maxSpeed;
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

        if (ride != null) {
            target = ((Creature) ride).getTarget();
        }

        return target;
    }

    public void setTarget(LivingEntity target)
    {
        LivingEntity ride = getRide();

        if (ride != null) {
            if (ride instanceof Creature) {
                if (MRUtil.hasNewAI(ride) && target instanceof CraftLivingEntity) {
                    ((CraftCreature) ride).getHandle().b(((CraftLivingEntity) target).getHandle());
                }
                else {
                    ((Creature) ride).setTarget(target);
                }
            }
            else if (ride instanceof Slime) {
                // TODO: implement setTarget for slime
            }
            else if (ride instanceof Ghast) {
                // TODO: implement setTarget for ghast
            }
            else if (ride instanceof EnderDragon) {
                // TODO: implement setTarget for enderdragon
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
        return hasRide(player);
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

    private boolean hasRide(Player player)
    {
        return player != null && player.getVehicle() instanceof LivingEntity;
    }
}