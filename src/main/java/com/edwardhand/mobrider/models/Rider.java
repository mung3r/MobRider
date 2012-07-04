package com.edwardhand.mobrider.models;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;

import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

public class Rider
{
    public enum IntentType {
        PASSIVE, FOLLOW, ATTACK, MOUNT, PAUSE, STOP;
    }

    private static final double ATTACK_RANGE = Math.pow(MRConfig.ATTACK_RANGE, 2.0D);
    private static final double GOAL_RANGE = Math.pow(MRConfig.MOUNT_RANGE, 2.0D);
    private static final int HEALTH_BARS = 6;

    private String playerName;
    private BaseGoal goal;
    private IntentType intent;
    private float maxSpeed;
    private float speed;

    public Rider(String playerName)
    {
        this.playerName = playerName;
        stop();
    }

    public float getSpeed()
    {
        return Math.max(maxSpeed, speed * getMaxHealth() / getHealth());
    }

    public void setSpeed(float speed)
    {
        this.speed = Math.max(Math.min(speed, maxSpeed), 0.05F);
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

    public World getWorld()
    {
        World world = null;
        Player player = getPlayer();

        if (player != null) {
            world = player.getWorld();
        }

        return world;
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
            ((Creature) ride).setTarget(target);
        }
    }

    public boolean isValid()
    {
        Player player = getPlayer();
        return hasRide(player);
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

    public boolean hasWaterCreature()
    {
        return getRide() instanceof Squid;
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
        LivingEntity ride = getRide();

        if (hasGoal() && ride != null) {
            switch (intent) {
                case ATTACK:
                    LivingEntity goalEntity = ((EntityGoal) goal).getEntity();
                    if (ride.getLocation().distanceSquared(goalEntity.getLocation()) > ATTACK_RANGE) {
                        setPathEntity(((EntityGoal) goal).getEntity().getLocation());
                    }
                    else {
                        setTarget(((EntityGoal) goal).getEntity());
                    }
                    break;
                case FOLLOW:
                    setTarget(null);
                    if (ride.getLocation().distanceSquared(goal.getLocation()) < GOAL_RANGE) {
                        setPathEntity(ride.getLocation());
                        intent = IntentType.PAUSE;
                    }
                    else {
                        setPathEntity(((EntityGoal) goal).getEntity().getLocation());
                    }
                    break;
                case PAUSE:
                    setTarget(null);
                    if (ride.getLocation().distanceSquared(goal.getLocation()) > GOAL_RANGE) {
                        setPathEntity(((EntityGoal) goal).getEntity().getLocation());
                        intent = IntentType.FOLLOW;
                    }
                    else {
                        setPathEntity(ride.getLocation());
                    }
                    break;
                case MOUNT:
                    // TODO: do we need this case?
                    break;
                case PASSIVE:
                    if (ride.getLocation().distanceSquared(goal.getLocation()) < GOAL_RANGE) {
                        stop();
                    }
                    else {
                        setTarget(null);
                        setPathEntity(goal.getLocation());
                    }
                    break;
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
        if (entity != null) {
            goal = new EntityGoal(entity);
            intent = IntentType.ATTACK;
            message(MRConfig.attackConfirmedMessage);
        }
        else {
            message(MRConfig.attackConfusedMessage);
        }
    }

    public void follow(String entityName)
    {
        follow(findGoal(entityName));
    }

    public void follow(LivingEntity entity)
    {
        if (entity != null) {
            goal = new EntityGoal(entity);
            intent = IntentType.FOLLOW;
            message(MRConfig.followConfirmedMessage);
        }
        else {
            message(MRConfig.followConfusedMessage);
        }
    }

    public void feed()
    {
        setHealth(Math.min(getHealth() + 5, getMaxHealth()));
        message(MRConfig.creatureFedMessage);
    }

    public void stop()
    {
        LivingEntity ride = getRide();

        if (ride != null) {
            goal = new LocationGoal(ride.getLocation());
            intent = IntentType.STOP;
            message(MRConfig.stopConfirmedMessage);
        }
    }

    public void message(String suffix)
    {
        Player player = getPlayer();
        LivingEntity ride = getRide();

        if (player != null && ride != null) {
            player.sendMessage("<" + getHealthString() + "§e" + ride.getType().getName() + "§f> " + getRideType().getNoise() + suffix);
        }
    }

    public void setDirection(Vector direction)
    {
        setDirection(direction, MRConfig.MAX_TRAVEL_DISTANCE);
    }

    public void setDestination(Location location)
    {
        goal = new LocationGoal(location);
        intent = IntentType.PASSIVE;
        message(MRConfig.goConfirmedMessage);
    }

    public void setDirection(Vector direction, int distance)
    {
        if (direction != null) {
            goal = new LocationGoal(convertDirectionToLocation(direction.multiply(Math.min(2.5D, distance / (double) MRConfig.MAX_TRAVEL_DISTANCE))));
            intent = IntentType.PASSIVE;
            message(MRConfig.goConfirmedMessage);
        }
        else {
            message(MRConfig.goConfusedMessage);
        }
    }

    private boolean hasRide(Player player)
    {
        return player !=null && player.getVehicle() instanceof LivingEntity;
    }

    private int getHealth()
    {
        int health = 0;
        LivingEntity ride = getRide();
        if (ride != null) {
            health = ride.getHealth();
        }
        return health;
    }

    private int getMaxHealth()
    {
        int maxHealth = 0;
        LivingEntity ride = getRide();
        if (ride != null) {
            maxHealth = ride.getMaxHealth();
        }
        return maxHealth;
    }

    private void setHealth(int health)
    {
        LivingEntity ride = getRide();

        if (ride != null) {
            ride.setHealth(health);
        }
    }

    private RideType getRideType()
    {
        RideType rideType = null;
        LivingEntity ride = getRide();

        if (ride != null) {
            rideType = RideType.fromType(ride.getType());
        }

        return rideType;
    }

    private LivingEntity findGoal(String searchTerm)
    {
        LivingEntity foundEntity = null;
        LivingEntity player = getPlayer();

        if (player != null) {
            // find entity by entity ID
            if (MRUtil.isNumber(searchTerm)) {
                net.minecraft.server.Entity entity = ((CraftWorld) player.getWorld()).getHandle().getEntity(Integer.valueOf(searchTerm));
                if (entity instanceof LivingEntity) {
                    if (((LivingEntity) entity).getLocation().distanceSquared(player.getLocation()) < MRConfig.MAX_SEARCH_RANGE) {
                        foundEntity = (LivingEntity) entity;
                    }
                }
            }
            // find player by name
            else if (Bukkit.getPlayer(searchTerm) != null) {
                foundEntity = Bukkit.getPlayer(searchTerm);
            }
            // find mob by name
            else {
                double lastDistance = Double.MAX_VALUE;
    
                for (org.bukkit.entity.Entity entity : player.getNearbyEntities(2 * MRConfig.MAX_SEARCH_RANGE, 2 * MRConfig.MAX_SEARCH_RANGE, 2 * MRConfig.MAX_SEARCH_RANGE)) {
    
                    if (entity instanceof LivingEntity) {
                        EntityType creatureType = entity.getType();
    
                        if (creatureType != null && creatureType.name().equalsIgnoreCase(searchTerm)) {
                            double entityDistance = player.getLocation().distanceSquared(entity.getLocation());
    
                            if (lastDistance > entityDistance && entity.getEntityId() != getPlayer().getEntityId()) {
                                lastDistance = entityDistance;
                                foundEntity = (LivingEntity) entity;
                            }
                        }
                    }
                }
            }
        }

        return foundEntity;
    }

    private void updateSpeed()
    {
        LivingEntity ride = getRide();

        if (ride == null || intent == IntentType.STOP || intent == IntentType.PAUSE) {
            return;
        }

        if (RideType.fromType(ride.getType()) == null) {
            return;
        }

        Vector velocity = ride.getVelocity();
        double saveY = velocity.getY();
        velocity.normalize().multiply(getSpeed());
        velocity.setY(saveY);
        ride.setVelocity(velocity);
    }

    private String getHealthString()
    {
        double percentHealth = (getHealth() * 100) / (double) getMaxHealth();

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
        LivingEntity ride = getRide();
        if (ride != null) {
            ((CraftCreature) ride).getHandle().setPathEntity(new PathEntity(new PathPoint[] { new PathPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ()) }));
        }
    }

    private Location convertDirectionToLocation(Vector direction)
    {
        Location location = null;
        LivingEntity ride = getRide();

        if (ride != null) {
            Location rideLocation = ride.getLocation();
            location = getWorld().getHighestBlockAt(rideLocation.getBlockX() + direction.getBlockX(), rideLocation.getBlockZ() + direction.getBlockZ()).getLocation();
        }

        return location;
    }
}
