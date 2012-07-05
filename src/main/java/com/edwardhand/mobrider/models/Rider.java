package com.edwardhand.mobrider.models;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;

import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

public class Rider
{
    public enum IntentType {
        PASSIVE, FOLLOW, ATTACK, PAUSE, STOP;
    }

    private static final double ATTACK_RANGE = Math.pow(MRConfig.ATTACK_RANGE, 2.0D);
    private static final double GOAL_RANGE = Math.pow(MRConfig.MOUNT_RANGE, 2.0D);
    private static final int HEALTH_BARS = 6;
    private static final float MIN_SPEED = 0.05F;

    private String playerName;
    private BaseGoal goal;
    private IntentType intent;
    private final float maxSpeed;
    private float speed;

    public Rider(String playerName)
    {
        this.playerName = playerName;
        LivingEntity ride = getRide();
        maxSpeed = ride != null ? RideType.fromType(ride.getType()).getMaxSpeed() : MIN_SPEED;
        speed = maxSpeed;
    }

    public float getSpeed()
    {
        return Math.min(maxSpeed, speed * getMaxHealth() / (float) getHealth());
    }

    public void setSpeed(float speed)
    {
        this.speed = Math.max(Math.min(speed, maxSpeed), MIN_SPEED);
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
            if (ride instanceof Creature) {
                ((Creature) ride).setTarget(target);
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
        attack(findGoal(entityName, MRConfig.ATTACK_RANGE));
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
        follow(findGoal(entityName, MRConfig.MAX_SEARCH_RANGE));
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
        if (isFullHealth()) {
            message(MRConfig.fedConfusedMessage);
        }
        else {
            setHealth(Math.min(getHealth() + 5, getMaxHealth()));
            message(MRConfig.fedConfirmedMessage);
        }
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
        return player != null && player.getVehicle() instanceof LivingEntity;
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

    private boolean isFullHealth()
    {
        LivingEntity ride = getRide();

        return ride == null || (ride.getHealth() >= ride.getMaxHealth());
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

    private LivingEntity findGoal(String searchTerm, double searchRange)
    {
        LivingEntity foundEntity = null;
        LivingEntity player = getPlayer();
        LivingEntity ride = getRide();
        Player foundPlayer = Bukkit.getPlayer(searchTerm);

        if (player != null) {
            // find entity by entity ID
            if (MRUtil.isNumber(searchTerm)) {
                net.minecraft.server.Entity entity = ((CraftWorld) player.getWorld()).getHandle().getEntity(Integer.valueOf(searchTerm));
                if (entity instanceof LivingEntity) {
                    if (((LivingEntity) entity).getLocation().distanceSquared(player.getLocation()) < searchRange * searchRange) {
                        foundEntity = (LivingEntity) entity;
                    }
                }
            }
            // find player by name
            else if (foundPlayer != null && !foundPlayer.equals(player) && foundPlayer.getWorld().equals(player.getWorld()) && foundPlayer.getLocation().distanceSquared(player.getLocation()) < searchRange * searchRange) {
                foundEntity = foundPlayer;
            }
            // find mob by name
            else if (ride != null) {
                double lastDistance = Double.MAX_VALUE;

                for (Entity entity : player.getNearbyEntities(2 * searchRange, 2 * searchRange, 2 * searchRange)) {

                    if (entity instanceof LivingEntity) {
                        EntityType creatureType = entity.getType();

                        if (creatureType != null && creatureType.name().equalsIgnoreCase(searchTerm)) {
                            double entityDistance = player.getLocation().distanceSquared(entity.getLocation());

                            if (lastDistance > entityDistance && entity.getEntityId() != player.getEntityId() && entity.getEntityId() != ride.getEntityId()) {
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

    private boolean hasNewAI()
    {
        LivingEntity ride = getRide();

        return ride != null && (ride.getType() == EntityType.CHICKEN
          || ride.getType() == EntityType.COW
          || ride.getType() == EntityType.CREEPER
          || ride.getType() == EntityType.IRON_GOLEM
          || ride.getType() == EntityType.MUSHROOM_COW
          || ride.getType() == EntityType.OCELOT
          || ride.getType() == EntityType.PIG
          || ride.getType() == EntityType.SHEEP
          || ride.getType() == EntityType.SKELETON
          || ride.getType() == EntityType.SNOWMAN
          || ride.getType() == EntityType.VILLAGER
          || ride.getType() == EntityType.WOLF
          || ride.getType() == EntityType.ZOMBIE);
    }

    private void setPathEntity(Location location)
    {
        LivingEntity ride = getRide();

        if (ride instanceof CraftCreature) {
            CraftCreature livingEntity = (CraftCreature) ride;

            if (hasNewAI()) {
                Location interimLocation = getInterimLocation(location);
                livingEntity.getHandle().al().a(interimLocation.getX(), interimLocation.getY(), interimLocation.getZ(), getSpeed());
            }
            else {
                ((CraftCreature) ride).getHandle().setPathEntity(new PathEntity(new PathPoint[] { new PathPoint(location.getBlockX(), location.getBlockY(), location.getBlockZ()) }));
            }
        }
        else if (ride instanceof CraftSlime) {
            // TODO: implement setPathEntity for slime
        }
        else if (ride instanceof CraftGhast) {
            // TODO: implement setPathEntity for ghast
        }
        else if (ride instanceof CraftEnderDragon) {
            // TODO: implement setPathEntity for enderdragon
        }
    }

    private Location getInterimLocation(Location target)
    {
        Location interimTarget = null;

        if (target != null) {
            LivingEntity ride = getRide();

            if (ride != null && ride.getLocation().distanceSquared(target) > 64.0D) {
                interimTarget = ride.getLocation().clone().add(new Vector(target.getX() - ride.getLocation().getX(), target.getY() - ride.getLocation().getY(), target.getZ() - ride.getLocation().getZ()).normalize().multiply(8));
            }
            else {
                interimTarget = target;
            }
        }

        return interimTarget;
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
