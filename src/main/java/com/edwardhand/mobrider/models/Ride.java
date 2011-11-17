package com.edwardhand.mobrider.models;

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
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntitySquid;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

public class Ride
{
    public enum IntentType {
        PASSIVE, FOLLOW, ATTACK, MOUNT, STOP;
    }

    private static final int MAX_HEALTH = 25;
    private static final double ATTACK_RANGE = Math.pow(MRConfig.ATTACK_RANGE, 2.0D);
    private static final double MOUNT_RANGE = Math.pow(MRConfig.MOUNT_RANGE, 2.0D);

    private Entity vehicle;
    private BaseGoal goal;
    private IntentType intent;
    private float speed;

    public Ride(Entity vehicle)
    {
        this.vehicle = vehicle;
        if (vehicle != null) {
            goal = new LocationGoal(getBukkitEntity().getLocation());
            intent = IntentType.STOP;
            speed = RideType.fromType(MRUtil.getCreatureType(vehicle.getBukkitEntity())).getSpeed();
        }
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        if (speed > 1.0F)
            speed = 1.0F;
        else if (speed < 0.05F)
            speed = 0.05F;

        this.speed = speed;
        System.out.println("DEBUG: speed set to " + speed);
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
        return ((Creature) vehicle.getBukkitEntity()).getTarget();
    }

    public void setTarget(LivingEntity target)
    {
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
        if (hasGoal()) {
            switch (intent)
            {
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
                    if (vehicle.getBukkitEntity().getLocation().distanceSquared(goal.getLocation()) < MOUNT_RANGE)
                        vehicle.setPassengerOf(((CraftEntity) ((EntityGoal) goal).getEntity()).getHandle());
                    break;
                case PASSIVE:
                case STOP:
                    setTarget(null);
                    setPathEntity(((LocationGoal) goal).getLocation());
                    break;
            }
            // TODO: fix speed update algorithm
            //updateSpeed();
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
        setHealth(getHealth() + 5);
        setHealth(Math.min(getHealth(), MAX_HEALTH));
        speak(MRConfig.CreatureFedMessage);
    }

    public void stop()
    {
        goal = new LocationGoal(getBukkitEntity().getLocation());
        intent = IntentType.STOP;
        speak(MRConfig.StopConfirmedMessage);
    }

    public void speak(String suffix)
    {
        Player player = (Player) getRider().getBukkitEntity();
        player.sendMessage("<" + getHealthString(getBukkitEntity()) + "§e" + getCreatureType().getName() + "§f>" + getVehicleType().getNoise() + suffix);
    }

    public void setDestination(Location location)
    {
        goal = new LocationGoal(location);
        intent = IntentType.PASSIVE;
    }

    public void setDirection(Vector direction)
    {
        setDirection(direction, MRConfig.MAX_DISTANCE);
    }

    public void setDirection(Vector direction, int distance)
    {
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

    private void setHealth(int health)
    {
        if (isCreature()) {
            ((LivingEntity) vehicle.getBukkitEntity()).setHealth(health);
        }
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

        System.out.println("DEBUG: distance is " + vehicle.getBukkitEntity().getLocation().distance(foundEntity.getLocation()));
        return (LivingEntity) foundEntity;
    }

    private void updateSpeed()
    {
        //if (!isCreature() || getHealth() <= 20) {
        if (!isCreature()) {
            return;
        }

        CreatureType type = MRUtil.getCreatureType(vehicle.getBukkitEntity());

        if (RideType.fromType(type) != null) {
            return;
        }

        float topSpeed = RideType.fromType(type).getSpeed();
        float newSpeed = ((((EntityLiving) vehicle).health / MAX_HEALTH) * 0.5F + 0.5F) * topSpeed * speed;

        //if (getCurrentSpeed() >= topSpeed || getCurrentSpeed() <= topSpeed / 4.0F) {
        if (getCurrentSpeed() >= newSpeed) {
            return;
        }

        setCurrentSpeed(newSpeed);
    }

    private float getCurrentSpeed()
    {
        return (float) Math.sqrt(vehicle.motX * vehicle.motX + vehicle.motZ * vehicle.motZ);
    }

    private void setCurrentSpeed(float speed)
    {
        float m = speed / getCurrentSpeed();
        vehicle.motX *= m;
        vehicle.motZ *= m;
        System.out.println("DEBUG: speed set to " + m);
    }

    private String getHealthString(org.bukkit.entity.Entity entity)
    {
        int col = 2;
        StringBuilder healthString = new StringBuilder();

        if (getHealth() <= 6) {
            col = 6;
        }
        else if (getHealth() <= 12) {
            col = 4;
        }

        for (int i = 0; i < 5; i++) {
            int col2 = i < 5 * getHealth() / MAX_HEALTH ? col : 8;
            healthString.append("§").append(col2).append("|");
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
