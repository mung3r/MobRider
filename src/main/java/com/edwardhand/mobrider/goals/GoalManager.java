package com.edwardhand.mobrider.goals;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.edwardhand.mobrider.ConfigManager;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.EntityUtils;
import com.edwardhand.mobrider.commons.MessageUtils;
import com.edwardhand.mobrider.goals.search.LivingEntitySearch;
import com.edwardhand.mobrider.goals.search.LocationSearch;
import com.edwardhand.mobrider.goals.search.strategies.Citizens2SearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.CitizensSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.EntityIdSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.FactionSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.MobSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.PlayerSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.PortalSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.RegionSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.RegiosSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.ResidenceSearchStrategy;
import com.edwardhand.mobrider.goals.search.strategies.TownSearchStrategy;
import com.edwardhand.mobrider.rider.Rider;

public class GoalManager
{
    private ConfigManager configManager;
    private List<LivingEntitySearch> findEntityStrategies;
    private List<LocationSearch> findLocationStrategies;

    public GoalManager(MobRider plugin)
    {
        configManager = plugin.getConfigManager();

        findEntityStrategies = new ArrayList<LivingEntitySearch>();
        findEntityStrategies.add(new EntityIdSearchStrategy());
        findEntityStrategies.add(new PlayerSearchStrategy());
        findEntityStrategies.add(new CitizensSearchStrategy());
        findEntityStrategies.add(new Citizens2SearchStrategy());
        findEntityStrategies.add(new MobSearchStrategy());

        findLocationStrategies = new ArrayList<LocationSearch>();
        findLocationStrategies.add(new PortalSearchStrategy(configManager));
        findLocationStrategies.add(new ResidenceSearchStrategy(configManager));
        findLocationStrategies.add(new RegionSearchStrategy(configManager));
        findLocationStrategies.add(new RegiosSearchStrategy(configManager));
        findLocationStrategies.add(new TownSearchStrategy(configManager));
        findLocationStrategies.add(new FactionSearchStrategy(configManager));
    }

    public void update(Rider rider)
    {
        if (rider.hasGoal()) {
            if (rider.getGoal().isGoalDone()) {
                setStopGoal(rider);
            }
            rider.getGoal().update(rider);
        }
    }

    public void setStopGoal(Rider rider)
    {
        if (!(rider.getGoal() instanceof StopGoal)) {
            rider.setGoal(new StopGoal(configManager));
            MessageUtils.sendMessage(rider, configManager.stopConfirmedMessage);
        }
    }

    public void setFollowGoal(Rider rider, String entityName)
    {
        LivingEntity entity = findLivingEntity(rider, entityName, configManager.MAX_SEARCH_RANGE);

        if (entity != null) {
            rider.setGoal(new FollowGoal(configManager, entity));
            MessageUtils.sendMessage(rider, configManager.followConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.followConfusedMessage);
        }
    }

    public void setGotoGoal(Rider rider, String goalName)
    {
        LivingEntity entity;

        if ((entity = findLivingEntity(rider, goalName, configManager.MAX_SEARCH_RANGE)) != null) {
            rider.setGoal(new GotoGoal(configManager, entity));
            MessageUtils.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else if (findLocation(rider, goalName)) {
            MessageUtils.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setAttackGoal(Rider rider, String entityName)
    {
        setAttackGoal(rider, findLivingEntity(rider, entityName, configManager.ATTACK_RANGE));
    }

    public void setAttackGoal(Rider rider, LivingEntity entity)
    {
        if (EntityUtils.isAggressive(rider.getRide())) {
            rider.setGoal(new AttackGoal(configManager, entity));
            MessageUtils.sendMessage(rider, configManager.attackConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.attackConfusedMessage);
        }
    }

    public void setDirection(Rider rider, Vector direction)
    {
        setDirection(rider, direction, configManager.MAX_TRAVEL_DISTANCE);
    }

    public void setDirection(Rider rider, Vector direction, int distance)
    {
        if (direction != null) {
            setDestination(rider, convertDirectionToLocation(rider, direction.normalize().multiply(Math.min(configManager.MAX_TRAVEL_DISTANCE, distance))));
        }
        else {
            MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setDestination(Rider rider, Location location)
    {
        if (location.getWorld().equals(rider.getWorld())) {
            rider.setGoal(new LocationGoal(configManager, location));
            //MessageUtils.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
        }
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

    private LivingEntity findLivingEntity(Rider rider, String searchTerm, double searchRange)
    {
        LivingEntity foundEntity = null;

        for (LivingEntitySearch strategy : findEntityStrategies) {
            if ((foundEntity = strategy.find(rider, searchTerm, searchRange)) != null) {
                return foundEntity;
            }
        }

        return foundEntity;
    }

    private boolean findLocation(Rider rider, String locationName)
    {
        for (LocationSearch strategy : findLocationStrategies) {
            if (strategy.find(rider, locationName)) {
                return true;
            }
        }

        return false;
    }
}
