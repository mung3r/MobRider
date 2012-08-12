package com.edwardhand.mobrider.managers;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.commons.Utils;
import com.edwardhand.mobrider.goals.AttackGoal;
import com.edwardhand.mobrider.goals.FactionGoal;
import com.edwardhand.mobrider.goals.FollowGoal;
import com.edwardhand.mobrider.goals.GotoGoal;
import com.edwardhand.mobrider.goals.LocationGoal;
import com.edwardhand.mobrider.goals.RegionGoal;
import com.edwardhand.mobrider.goals.RegiosGoal;
import com.edwardhand.mobrider.goals.ResidenceGoal;
import com.edwardhand.mobrider.goals.StopGoal;
import com.edwardhand.mobrider.goals.TownyGoal;
import com.edwardhand.mobrider.models.Rider;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import couk.Adamki11s.Regios.Regions.Region;

public class GoalManager
{
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
            rider.setGoal(new StopGoal(plugin));
            messageManager.sendMessage(rider, configManager.stopConfirmedMessage);
        }
    }

    public void setFollowGoal(Rider rider, String entityName)
    {
        LivingEntity entity = findLivingEntity(rider, entityName, configManager.MAX_SEARCH_RANGE);

        if (entity != null) {
            rider.setGoal(new FollowGoal(plugin, entity));
            messageManager.sendMessage(rider, configManager.followConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.followConfusedMessage);
        }
    }

    public void setGotoGoal(Rider rider, String goalName)
    {
        LivingEntity entity;

        if ((entity = findLivingEntity(rider, goalName, configManager.MAX_SEARCH_RANGE)) != null) {
            rider.setGoal(new GotoGoal(plugin, entity));
            messageManager.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else if (foundPortal(rider, goalName) || foundResidence(rider, goalName) || foundRegion(rider, goalName) || foundRegios(rider, goalName) || foundTown(rider, goalName) || foundFaction(rider, goalName)) {
            messageManager.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setAttackGoal(Rider rider, String entityName)
    {
        setAttackGoal(rider, findLivingEntity(rider, entityName, configManager.ATTACK_RANGE));
    }

    public void setAttackGoal(Rider rider, LivingEntity entity)
    {
        if (Utils.isAggressive(rider.getRide())) {
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
            setDestination(rider, convertDirectionToLocation(rider, direction.normalize().multiply(Math.min(configManager.MAX_TRAVEL_DISTANCE, distance))));
        }
        else {
            messageManager.sendMessage(rider, configManager.goConfusedMessage);
        }
    }

    public void setDestination(Rider rider, Location location)
    {
        if (location.getWorld().equals(rider.getWorld())) {
            rider.setGoal(new LocationGoal(plugin, location));
            //messageManager.sendMessage(rider, configManager.goConfirmedMessage);
        }
        else {
            messageManager.sendMessage(rider, configManager.goConfusedMessage);
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
        LivingEntity player = rider.getPlayer();

        if (player != null) {
            // find entity by entity ID
            if (foundEntity == null && Utils.isInteger(searchTerm)) {
                net.minecraft.server.Entity entity = ((CraftWorld) player.getWorld()).getHandle().getEntity(Integer.valueOf(searchTerm));
                if (entity instanceof LivingEntity) {
                    if (isEntityWithinRange((LivingEntity) entity, player, searchRange)) {
                        foundEntity = (LivingEntity) entity;
                    }
                }
            }

            // find player by name
            Player foundPlayer = Bukkit.getPlayer(searchTerm);
            if (foundEntity == null && isEntityWithinRange(foundPlayer, player, searchRange)) {
                foundEntity = foundPlayer;
            }

            // find npc citizen by name
            if (foundEntity == null && plugin.hasCitizens()) {
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
            LivingEntity ride = rider.getRide();
            if (foundEntity == null && ride != null) {
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

    private boolean foundPortal(Rider rider, String portalName)
    {
        boolean foundPortal = false;

        if (plugin.hasMultiverse()) {
            MVDestination portalTest = plugin.getMVDestinationFactory().getDestination("p:" + portalName);

            if (portalTest.getType().equals("Portal")) {
                Location portalLocation = portalTest.getLocation(null);
                if (portalLocation.getWorld().equals(rider.getWorld())) {
                    rider.setGoal(new LocationGoal(plugin, portalLocation));
                    foundPortal = true;
                }
            }
        }

        return foundPortal;
    }

    private boolean foundResidence(Rider rider, String residenceName)
    {
        boolean foundResidence = false;

        if (plugin.hasResidence()) {
            ClaimedResidence residence = Residence.getResidenceManager().getByName(residenceName);
            if (residence != null && residence.getWorld().equals(rider.getWorld().getName())) {
                rider.setGoal(new ResidenceGoal(plugin, residence, rider.getWorld()));
                foundResidence = true;
            }
        }

        return foundResidence;
    }

    private boolean foundRegion(Rider rider, String regionName)
    {
        boolean foundRegion = false;

        if (plugin.hasWorldGuard()) {
            ProtectedRegion region = plugin.getRegionManager(rider.getWorld()).getRegion(regionName);
            if (region != null) {
                rider.setGoal(new RegionGoal(plugin, region, rider.getWorld()));
                foundRegion = true;
            }
        }

        return foundRegion;
    }

    private boolean foundRegios(Rider rider, String regionName)
    {
        boolean foundRegion = false;

        if (plugin.hasRegios()) {
            Region region = plugin.getRegiosAPI().getRegion(regionName);
            if (region != null && region.getWorld().equals(rider.getWorld())) {
                rider.setGoal(new RegiosGoal(plugin, region, rider.getWorld()));
                foundRegion = true;
            }
        }

        return foundRegion;
    }

    private boolean foundTown(Rider rider, String townName)
    {
        boolean foundTown = false;

        if (plugin.hasTowny()) {
            for (Town town : TownyUniverse.getDataSource().getTowns()) {
                if (town.getName().equals(townName)) {
                    rider.setGoal(new TownyGoal(plugin, town));
                    foundTown = true;
                }
            }
        }

        return foundTown;
    }

    private boolean foundFaction(Rider rider, String factionTag)
    {
        boolean foundFaction = false;

        if (plugin.hasFactions()) {
            Faction faction = Factions.i.getByTag(factionTag);
            if (faction != null) {
                rider.setGoal(new FactionGoal(plugin, faction));
                foundFaction = true;
            }
        }

        return foundFaction;
    }

    private boolean isEntityWithinRange(LivingEntity from, LivingEntity to, double range)
    {
        return from != null && to != null && !from.equals(to) && from.getWorld().equals(to.getWorld()) && from.getLocation().distanceSquared(to.getLocation()) < range * range;
    }
}
