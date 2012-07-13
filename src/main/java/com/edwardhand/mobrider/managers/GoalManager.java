package com.edwardhand.mobrider.managers;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCList;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.goals.AttackGoal;
import com.edwardhand.mobrider.goals.FollowGoal;
import com.edwardhand.mobrider.goals.Goal;
import com.edwardhand.mobrider.goals.GotoGoal;
import com.edwardhand.mobrider.goals.LocationGoal;
import com.edwardhand.mobrider.goals.StopGoal;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRUtil;

public class GoalManager
{
    private static final long HYSTERESIS_THRESHOLD = 250; // quarter second
                                                          // in milliseconds
    private MobRider plugin;
    private ConfigManager configManager;
    private MessageManager messageManager;

    public GoalManager(MobRider plugin)
    {
        this.plugin = plugin;
        configManager = plugin.getConfigManager();
        messageManager = plugin.getMessageManager();
    }

    public void update(Rider rider)
    {
        Goal goal = rider.getGoal();
        if (goal != null) {
            goal.executeUpdate(rider);
        }
    }

    public void setStopGoal(Rider rider)
    {
        if (!(rider.getGoal() instanceof StopGoal)) {
            rider.setGoal(new StopGoal(plugin, rider.getRide().getLocation()));
            messageManager.sendMessage(rider, configManager.stopConfirmedMessage);
        }
    }

    public void setFollowGoal(Rider rider, String entityName)
    {
        LivingEntity entity = findGoal(rider, entityName, configManager.MAX_SEARCH_RANGE);

        if (entity != null) {
            rider.setGoal(new FollowGoal(plugin, entity));
            messageManager.sendMessage(rider, configManager.followConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.followConfusedMessage);
        }
    }

    public void setGotoGoal(Rider rider, String entityName)
    {
        LivingEntity entity = findGoal(rider, entityName, configManager.MAX_SEARCH_RANGE);

        if (entity != null) {
            rider.setGoal(new GotoGoal(plugin, entity));
            messageManager.sendMessage(rider, configManager.followConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.followConfusedMessage);
        }
    }

    public void setAttackGoal(Rider rider, String entityName)
    {
        setAttackGoal(rider, findGoal(rider, entityName, configManager.ATTACK_RANGE));
    }

    public void setAttackGoal(Rider rider, LivingEntity entity)
    {
        if (entity != null) {
            rider.setGoal(new AttackGoal(plugin, entity));
            messageManager.sendMessage(rider, configManager.attackConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.attackConfusedMessage);
        }
    }

    public void setDirection(Rider rider, Vector direction)
    {
        setDirection(rider, direction, configManager.MAX_TRAVEL_DISTANCE);
    }

    public void setDirection(Rider rider, Vector direction, int distance)
    {
        if (direction != null) {
            setDestination(rider, convertDirectionToLocation(rider, direction.multiply(Math.min(configManager.MAX_TRAVEL_DISTANCE, distance))));
        }
        else {
            messageManager.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setDestination(Rider rider, Location location)
    {
        if (location.getWorld().equals(rider.getWorld())) {
            rider.setGoal(new LocationGoal(plugin, location));
            messageManager.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setPathEntity(Rider rider, Location destination)
    {
        LivingEntity ride = rider.getRide();

        if (ride instanceof CraftCreature) {
            CraftCreature creature = (CraftCreature) ride;

            if (MRUtil.hasNewAI(ride)) {
                Location interimLocation = getInterimLocation(ride, destination);
                creature.getHandle().al().a(interimLocation.getX(), interimLocation.getY(), interimLocation.getZ(), rider.getSpeed());
            }
            else {
                ((CraftCreature) ride).getHandle().setPathEntity(new PathEntity(new PathPoint[] { new PathPoint(destination.getBlockX(), destination.getBlockY(), destination.getBlockZ()) }));
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

    public void updateSpeed(Rider rider)
    {
        if (rider != null) {
            LivingEntity ride = rider.getRide();

            if (ride != null && rider.getRideType() != null) {
                Vector velocity = ride.getVelocity();
                double saveY = velocity.getY();
                velocity.normalize().multiply(rider.getSpeed());
                velocity.setY(saveY);
                ride.setVelocity(velocity);
            }
        }
    }

    public boolean isWithinRange(Location currentLocation, Location destination, double range)
    {
        return currentLocation.distanceSquared(destination) < range;
    }

    public boolean isWithinHysteresisThreshold(Goal goal)
    {
        return goal.getTimeCreated() < HYSTERESIS_THRESHOLD;
    }

    private Location convertDirectionToLocation(Rider rider, Vector direction)
    {
        Location location = null;
        LivingEntity ride = rider.getRide();

        if (ride != null) {
            Location rideLocation = ride.getLocation();
            location = rider.getWorld().getHighestBlockAt(rideLocation.getBlockX() + direction.getBlockX(), rideLocation.getBlockZ() + direction.getBlockZ()).getLocation();
        }

        return location;
    }

    private LivingEntity findGoal(Rider rider, String searchTerm, double searchRange)
    {
        LivingEntity foundEntity = null;
        LivingEntity player = rider.getPlayer();
        LivingEntity ride = rider.getRide();
        Player foundPlayer = Bukkit.getPlayer(searchTerm);

        if (player != null) {
            // find entity by entity ID
            if (MRUtil.isNumber(searchTerm)) {
                net.minecraft.server.Entity entity = ((CraftWorld) player.getWorld()).getHandle().getEntity(Integer.valueOf(searchTerm));
                if (entity instanceof LivingEntity) {
                    if (isEntityWithinRange((LivingEntity) entity, player, searchRange)) {
                        foundEntity = (LivingEntity) entity;
                    }
                }
            }
            // find player by name
            else if (isEntityWithinRange(foundPlayer, player, searchRange)) {
                foundEntity = foundPlayer;
            }
            // find npc citizen by name
            else if (plugin.hasCitizens()) {
                NPCList npcList = CitizensManager.getList();
                for (HumanNPC npc : npcList.values()) {
                    if (npc.getName().equalsIgnoreCase(searchTerm)) {
                        if (isEntityWithinRange(player, npc.getPlayer(), searchRange)) {
                            foundEntity = npc.getPlayer();
                            break;
                        }
                    }
                }
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

    private boolean isEntityWithinRange(LivingEntity from, LivingEntity to, double range)
    {
        return from != null && to != null && !from.equals(to) && from.getWorld().equals(to.getWorld()) && from.getLocation().distanceSquared(to.getLocation()) < range * range;
    }

    private Location getInterimLocation(LivingEntity ride, Location destination)
    {
        Location interimTarget = null;

        if (ride != null && ride.getLocation().distanceSquared(destination) > 64.0D) {
            interimTarget = ride.getLocation().clone().add(new Vector(destination.getX() - ride.getLocation().getX(), destination.getY() - ride.getLocation().getY(), destination.getZ() - ride.getLocation().getZ()).normalize().multiply(8));
        }
        else {
            interimTarget = destination;
        }

        return interimTarget;
    }
}
