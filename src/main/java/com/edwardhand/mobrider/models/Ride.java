package com.edwardhand.mobrider.models;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntitySquid;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

public class Ride
{
    public enum IntentType {
        PASSIVE, FOLLOW, ATTACK, MOUNT, STOP;
    }

    private static final double ATTACK_RANGE = Math.pow(MRConfig.ATTACK_RANGE, 2.0D);
    private static final double GOAL_RANGE = Math.pow(MRConfig.MOUNT_RANGE, 2.0D);
    private static final int HEALTH_BARS = 6;

    private Entity vehicle;
    private BaseGoal goal;
    private IntentType intent;
    private float maxSpeed;
    private float speed;

    public Ride(Entity vehicle)
    {
        this.vehicle = vehicle;
        if (vehicle != null) {
            goal = new LocationGoal(getBukkitEntity().getLocation());
            intent = IntentType.STOP;
            if (isCreature()) {
                maxSpeed = RideType.fromType(MRUtil.getCreatureType(vehicle.getBukkitEntity())).getSpeed();
                speed = maxSpeed;
            }
        }
    }

    public float getSpeed()
    {
        return Math.max(maxSpeed, speed * getMaxHealth() / getHealth());
    }

    public void setSpeed(float speed)
    {
        this.speed = Math.max(Math.min(speed, maxSpeed), 0.05F);
    }

    public Entity getRider()
    {
        return vehicle.passenger;
    }

    public org.bukkit.World getWorld()
    {
        return ((Creature) vehicle.getBukkitEntity()).getWorld();
    }

    public LivingEntity getTarget()
    {
        if (!isCreature())
            return null;

        return ((Creature) vehicle.getBukkitEntity()).getTarget();
    }

    public void setTarget(LivingEntity target)
    {
        if (!isCreature())
            return;

        ((Creature) vehicle.getBukkitEntity()).setTarget(target);
    }

    public org.bukkit.entity.Entity getBukkitEntity()
    {
        return vehicle.getBukkitEntity();
    }

    public Boolean isValid()
    {
        return vehicle != null;
    }

    public Boolean isCreature()
    {
        return vehicle instanceof EntityCreature;
    }

    public Boolean isWaterCreature()
    {
        return vehicle instanceof EntitySquid;
    }

    public Boolean hasRider()
    {
        return (vehicle != null && vehicle.passenger != null && vehicle.passenger.vehicle != null);
    }

    public boolean hasGoal()
    {
        return goal != null;
    }

    public void resetGoal()
    {
        goal = null;
    }

    public void updateGoal()
    {
        if (!isCreature())
            return;

        if (hasGoal()) {
            switch (intent) {
                case ATTACK:
                    LivingEntity goalEntity = ((EntityGoal) goal).getEntity();
                    if (vehicle.getBukkitEntity().getLocation().distanceSquared(goalEntity.getLocation()) > ATTACK_RANGE) {
                        setPathEntity(((EntityGoal) goal).getEntity().getLocation());
                    }
                    else {
                        setTarget(((EntityGoal) goal).getEntity());
                    }
                    break;
                case FOLLOW:
                    setTarget(null);
                    setPathEntity(((EntityGoal) goal).getEntity().getLocation());
                    break;
                case MOUNT:
                    // TODO: do we need this case?
                    break;
                case PASSIVE:
                    if (vehicle.getBukkitEntity().getLocation().distanceSquared(goal.getLocation()) < GOAL_RANGE)
                        stop();
                case STOP:
                    setTarget(null);
                    setPathEntity(((LocationGoal) goal).getLocation());
                    break;
            }

            updateSpeed();
        }
    }

    public void attack(String entityName)
    {
        attack(findGoal(entityName));
    }

    public void attack(LivingEntity entity)
    {
        if (!isCreature())
            return;

        if (entity != null) {
            goal = new EntityGoal(entity);
            intent = IntentType.ATTACK;
            speak(MRConfig.AttackConfirmedMessage);
        }
        else {
            speak(MRConfig.AttackConfusedMessage);
        }
    }

    public void follow(String entityName)
    {
        follow(findGoal(entityName));
    }

    public void follow(LivingEntity entity)
    {
        if (!isCreature())
            return;

        if (entity != null) {
            goal = new EntityGoal(entity);
            intent = IntentType.FOLLOW;
            speak(MRConfig.FollowConfirmedMessage);
        }
        else {
            speak(MRConfig.FollowConfusedMessage);
        }
    }

    public void feed()
    {
        if (!isCreature())
            return;

        setHealth(Math.min(getHealth() + 5, getMaxHealth()));
        speak(MRConfig.CreatureFedMessage);
    }

    public void stop()
    {
        if (!isCreature())
            return;

        goal = new LocationGoal(getBukkitEntity().getLocation());
        intent = IntentType.STOP;
        speak(MRConfig.StopConfirmedMessage);
    }

    public void speak(String suffix)
    {
        if (!isCreature())
            return;

        Player player = (Player) getRider().getBukkitEntity();
        player.sendMessage("<" + getHealthString(getBukkitEntity()) + "§e" + getCreatureType().getName() + "§f> " + getVehicleType().getNoise() + suffix);
    }

    public void setDirection(Vector direction)
    {
        setDirection(direction, MRConfig.MAX_DISTANCE);
    }

    public void setDestination(Location location)
    {
        if (!isCreature())
            return;

        goal = new LocationGoal(location);
        intent = IntentType.PASSIVE;
        speak(MRConfig.GoConfirmedMessage);
    }

    public void setDirection(Vector direction, int distance)
    {
        if (!isCreature())
            return;

        if (direction != null) {
            goal = new LocationGoal(convertDirectionToLocation(direction.multiply(Math.min(2.5D, distance / MRConfig.MAX_DISTANCE))));
            intent = IntentType.PASSIVE;
            speak(MRConfig.GoConfirmedMessage);
        }
        else {
            speak(MRConfig.GoConfusedMessage);
        }
    }

    private int getHealth()
    {
        int health = 0;
        if (isCreature()) {
            health = ((LivingEntity) vehicle.getBukkitEntity()).getHealth();
        }
        return health;
    }

    private int getMaxHealth()
    {
        int maxHealth = 0;
        if (isCreature()) {
            maxHealth = ((LivingEntity) vehicle.getBukkitEntity()).getMaxHealth();
        }
        return maxHealth;
    }

    private void setHealth(int health)
    {
        if (!isCreature())
            return;

        ((LivingEntity) vehicle.getBukkitEntity()).setHealth(health);
    }

    private RideType getVehicleType()
    {
        return RideType.fromType(MRUtil.getCreatureType(vehicle.getBukkitEntity()));
    }

    private CreatureType getCreatureType()
    {
        return MRUtil.getCreatureType(vehicle.getBukkitEntity());
    }

    private LivingEntity findGoal(String searchTerm)
    {
        LivingEntity foundEntity = null;

        // find entity by entity ID
        if (MRUtil.isNumber(searchTerm)) {
            Entity entity = ((CraftWorld) vehicle.getBukkitEntity().getWorld()).getHandle().getEntity(Integer.valueOf(searchTerm));
            if (entity != null && entity instanceof LivingEntity) {
                if (((LivingEntity) entity).getLocation().distanceSquared(vehicle.getBukkitEntity().getLocation()) < MRConfig.MAX_FIND_RANGE) {
                    foundEntity = (LivingEntity) entity;
                }
            }
        }
        // find player by name
        else if (vehicle.getBukkitEntity().getServer().getPlayer(searchTerm) != null) {
            foundEntity = vehicle.getBukkitEntity().getServer().getPlayer(searchTerm);
        }
        // find mob by name
        else {
            double lastDistance = Double.MAX_VALUE;

            for (org.bukkit.entity.Entity entity : vehicle.getBukkitEntity().getNearbyEntities(2 * MRConfig.MAX_FIND_RANGE, 2 * MRConfig.MAX_FIND_RANGE, 2 * MRConfig.MAX_FIND_RANGE)) {

                if (entity instanceof LivingEntity) {
                    CreatureType creatureType = MRUtil.getCreatureType(entity);

                    if (creatureType != null && creatureType.name().equalsIgnoreCase(searchTerm)) {
                        double entityDistance = vehicle.getBukkitEntity().getLocation().distanceSquared(entity.getLocation());

                        if (lastDistance > entityDistance && entity.getEntityId() != getRider().id) {
                            lastDistance = entityDistance;
                            foundEntity = (LivingEntity) entity;
                        }
                    }
                }
            }
        }

        return (LivingEntity) foundEntity;
    }

    private void updateSpeed()
    {
        if (!isCreature() || intent == IntentType.STOP) {
            return;
        }

        CreatureType type = MRUtil.getCreatureType(vehicle.getBukkitEntity());
        if (RideType.fromType(type) == null) {
            return;
        }

        Vector velocity = vehicle.getBukkitEntity().getVelocity();
        double saveY = velocity.getY();
        velocity.normalize().multiply(getSpeed());
        velocity.setY(saveY);
        vehicle.getBukkitEntity().setVelocity(velocity);
    }

    private String getHealthString(org.bukkit.entity.Entity entity)
    {
        double percentHealth = (getHealth() * 100) / getMaxHealth();

        ChatColor barColor;

        if (percentHealth > 66) {
            barColor = ChatColor.GREEN;
        }
        else if (percentHealth > 33) {
            barColor = ChatColor.GOLD;
        }
        else {
            barColor = ChatColor.RED;
        }

        StringBuilder healthString = new StringBuilder();
        double colorSwitch = Math.ceil((percentHealth / 100D) * HEALTH_BARS);

        for (int i = 0; i < HEALTH_BARS; i++) {
            ChatColor color = i < colorSwitch ? barColor : ChatColor.GRAY;
            healthString.append(color).append("|");
        }

        return healthString.toString();
    }

    private void setPathEntity(Location location)
    {
        ((EntityCreature) vehicle).setPathEntity(new PathEntity(new PathPoint[] { new PathPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ()) }));
    }

    private Location convertDirectionToLocation(Vector direction)
    {
        Location location = ((Creature) vehicle.getBukkitEntity()).getLocation();
        return getWorld().getHighestBlockAt(location.getBlockX() + direction.getBlockX(), location.getBlockZ() + direction.getBlockZ()).getLocation();
    }
}
