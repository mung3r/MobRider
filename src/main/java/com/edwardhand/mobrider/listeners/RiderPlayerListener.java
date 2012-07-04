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
import com.edwardhand.mobrider.models.Rider;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRHandler;
import com.edwardhand.mobrider.utils.MRUtil;

public class RiderPlayerListener implements Listener
{
    private MRConfig config;
    private MRHandler riderHandler;

    public RiderPlayerListener(MobRider plugin)
    {
        config = plugin.getMRConfig();
        riderHandler = plugin.getRiderHandler();
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
                LivingEntity target = MRUtil.getTargetLivingEntity(player);
                if (target != null && MRUtil.canRide(player, target)) {
                    target.setPassenger(player);
                    Rider rider = riderHandler.addRider(player);
                    rider.stop();
                }
            }
        }
        else if (vehicle instanceof LivingEntity && config.isFood(player.getItemInHand().getType())) {
            riderHandler.getRider(player).feed();
            MRUtil.removeItemInHand(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInHand();

        if (itemInHand != null && itemInHand.getType() == Material.FISHING_ROD) {

            Rider rider = riderHandler.getRider(player);

            rider.setDestination(player.getTargetBlock(null, MRConfig.MAX_TRAVEL_DISTANCE).getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        Rider rider = riderHandler.getRiders().get(player.getName());

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
