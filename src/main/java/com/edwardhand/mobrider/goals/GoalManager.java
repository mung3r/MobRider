/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2012, R. Ramos <http://github.com/mung3r/>
 * MobRider is licensed under the GNU Lesser General Public License.
 *
 * MobRider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobRider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    private static final List<LivingEntitySearch> findEntityStrategies = new ArrayList<LivingEntitySearch>();
    private static final List<LocationSearch> findLocationStrategies = new ArrayList<LocationSearch>();

    static {
        findEntityStrategies.add(new EntityIdSearchStrategy());
        findEntityStrategies.add(new PlayerSearchStrategy());
        findEntityStrategies.add(new CitizensSearchStrategy());
        findEntityStrategies.add(new Citizens2SearchStrategy());
        findEntityStrategies.add(new MobSearchStrategy());

        findLocationStrategies.add(new PortalSearchStrategy());
        findLocationStrategies.add(new ResidenceSearchStrategy());
        findLocationStrategies.add(new RegionSearchStrategy());
        findLocationStrategies.add(new RegiosSearchStrategy());
        findLocationStrategies.add(new TownSearchStrategy());
        findLocationStrategies.add(new FactionSearchStrategy());
    }

    public GoalManager(MobRider plugin)
    {
        configManager = plugin.getConfigManager();
    }

    public void update(Rider rider)
    {
        if (rider.hasGoal()) {
            if (rider.getGoal().isGoalDone()) {
                setStopGoal(rider);
            }
            rider.getGoal().update(rider, configManager.goalRange);
        }
    }

    public void setStopGoal(Rider rider)
    {
        if (!(rider.getGoal() instanceof StopGoal)) {
            rider.setGoal(new StopGoal());
            MessageUtils.sendMessage(rider, configManager.stopConfirmedMessage);
        }
    }

    public void setFollowGoal(Rider rider, String entityName)
    {
        LivingEntity entity = findLivingEntity(rider, entityName, configManager.maxSearchRange);

        if (entity != null) {
            rider.setGoal(new FollowGoal(entity));
            MessageUtils.sendMessage(rider, configManager.followConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.followConfusedMessage);
        }
    }

    public void setGotoGoal(Rider rider, String goalName)
    {
        LivingEntity entity = findLivingEntity(rider, goalName, configManager.maxSearchRange);

        if (entity != null) {
            rider.setGoal(new LivingEntityGoal(entity));
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
        LivingEntity entity = findLivingEntity(rider, entityName, configManager.attackRange);

        if (entity != null) {
            setAttackGoal(rider, entity);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.attackConfusedMessage);
        }
    }

    public void setAttackGoal(Rider rider, LivingEntity entity)
    {
        if (EntityUtils.isAggressive(rider.getRide())) {
            rider.setGoal(new AttackGoal(entity));
            MessageUtils.sendMessage(rider, configManager.attackConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.attackConfusedMessage);
        }
    }

    public void setDirection(Rider rider, Vector direction)
    {
        setDirection(rider, direction, configManager.maxTravelDistance);
    }

    public void setDirection(Rider rider, Vector direction, int distance)
    {
        if (direction != null) {
            setDestination(rider, convertDirectionToLocation(rider, direction.normalize().multiply(Math.min(configManager.maxTravelDistance, distance))));
        }
        else {
            MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setDestination(Rider rider, Location location)
    {
        if (location.getWorld().equals(rider.getWorld())) {
            rider.setGoal(new LocationGoal(location));
            // MessageUtils.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else {
            MessageUtils.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    private static Location convertDirectionToLocation(Rider rider, Vector direction)
    {
        Location location = null;
        LivingEntity ride = rider.getRide();

        if (ride != null) {
            Location rideLocation = ride.getLocation();
            location = rider.getWorld().getHighestBlockAt(rideLocation.getBlockX() + direction.getBlockX(), rideLocation.getBlockZ() + direction.getBlockZ())
                    .getLocation();
        }

        return location;
    }

    private static LivingEntity findLivingEntity(Rider rider, String searchTerm, double searchRange)
    {
        LivingEntity foundEntity = null;

        for (LivingEntitySearch strategy : findEntityStrategies) {
            foundEntity = strategy.find(rider, searchTerm, searchRange);

            if (foundEntity != null) {
                return foundEntity;
            }
        }

        return foundEntity;
    }

    private static boolean findLocation(Rider rider, String locationName)
    {
        for (LocationSearch strategy : findLocationStrategies) {
            if (strategy.find(rider, locationName)) {
                return true;
            }
        }

        return false;
    }
}
