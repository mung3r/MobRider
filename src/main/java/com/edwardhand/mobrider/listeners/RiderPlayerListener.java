package com.edwardhand.mobrider.listeners;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.ConfigManager;
import com.edwardhand.mobrider.managers.GoalManager;
import com.edwardhand.mobrider.managers.RiderManager;
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRUtil;

public class RiderPlayerListener implements Listener
{
    private ConfigManager config;
    private RiderManager riderManager;
    private GoalManager goalManager;

    public RiderPlayerListener(MobRider plugin)
    {
        config = plugin.getConfigManager();
        riderManager = plugin.getRiderManager();
        goalManager = plugin.getGoalManager();
    }

    // This method must run even if it was canceled.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();

        if (player.getItemInHand().getType() == Material.SADDLE) {
            if (vehicle instanceof LivingEntity) {
                ((CraftPlayer) player).getHandle().setPassengerOf(null);
            }
            else {
                LivingEntity target = MRUtil.getNearByTarget(player);
                if (target != null && riderManager.canRide(player, target)) {
                    target.setPassenger(player);
                    Rider rider = riderManager.addRider(player);
                    goalManager.setStopGoal(rider);
                }
            }
        }
        else if (vehicle instanceof LivingEntity && config.isFood(player.getItemInHand().getType())) {
            riderManager.feedRide(riderManager.getRider(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInHand();

        if (itemInHand != null && itemInHand.getType() == Material.FISHING_ROD) {

            Rider rider = riderManager.getRider(player);

            goalManager.setDestination(rider, player.getTargetBlock(null, ConfigManager.MAX_TRAVEL_DISTANCE).getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        Rider rider = riderManager.getRiders().get(player.getName());

        // Holding down SHIFT(sneak) and mouse scrolling adjusts speed
        if (rider != null && rider.hasGoal() && player.isSneaking()) {

            int newSlot = event.getNewSlot();
            int prevSlot = event.getPreviousSlot();
            boolean increase = (prevSlot - newSlot) > 0;

            if (((prevSlot == 0) && (newSlot == 8)) || ((prevSlot == 8) && (newSlot == 0)))
                increase = !increase;

            rider.setSpeed(increase ? rider.getSpeed() + 0.05F : rider.getSpeed() - 0.05F);
        }
    }
}
