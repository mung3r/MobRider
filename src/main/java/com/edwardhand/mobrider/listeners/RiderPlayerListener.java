package com.edwardhand.mobrider.listeners;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.Ride;
import com.edwardhand.mobrider.utils.MRConfig;
import com.edwardhand.mobrider.utils.MRUtil;
import com.edwardhand.mobrider.utils.MRHandler;

public class RiderPlayerListener extends PlayerListener
{
    private MobRider plugin;

    public RiderPlayerListener(MobRider plugin)
    {
        this.plugin = plugin;
    }

    // This method must run even if it was canceled.
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK))
            return;

        Material itemInHand = event.getPlayer().getInventory().getItemInHand().getType();

        Player player = event.getPlayer();
        EntityPlayer minecraftPlayer = ((CraftPlayer) player).getHandle();
        Entity vehicle = minecraftPlayer.vehicle;

        if (itemInHand == Material.SADDLE) {
            if (vehicle != null) {
                minecraftPlayer.setPassengerOf(null);
                return;
            }

            Location loc = player.getTargetBlock(null, 5).getLocation();
            CraftWorld craftWorld = (CraftWorld) player.getWorld();
            double x1 = loc.getX() + 0.5D;
            double y1 = loc.getY() + 0.5D;
            double z1 = loc.getZ() + 0.5D;

            @SuppressWarnings("rawtypes")
            List entities = new ArrayList();
            double r = 0.5D;
            while ((entities.size() == 0) && (r < 5.0D)) {
                AxisAlignedBB bb = AxisAlignedBB.a(x1 - r, y1 - r, z1 - r, x1 + r, y1 + r, z1 + r);
                entities = craftWorld.getHandle().b(minecraftPlayer, bb);
                r += 0.5D;
            }

            if ((entities.size() == 1) && ((entities.get(0) instanceof EntityLiving))) {
                Entity target = (EntityLiving) entities.get(0);
                if (!MRUtil.canRide(player, target.getBukkitEntity()))
                    return;

                minecraftPlayer.setPassengerOf(target);
                plugin.getRideHandler().getRide(player).stop();
            }
        }
        else if (MRUtil.isFood(itemInHand)) {
            if ((vehicle != null) && (vehicle instanceof EntityCreature)) {
                EntityCreature creatureVehicle = (EntityCreature) vehicle;
                if (creatureVehicle.getHealth() < creatureVehicle.getMaxHealth()) {
                    PlayerInventory inv = player.getInventory();
                    ItemStack stack = inv.getItemInHand();
                    if (stack.getAmount() > 1) {
                        stack.setAmount(stack.getAmount() - 1);
                        inv.setItem(inv.getHeldItemSlot(), stack);
                    }
                    else {
                        inv.setItem(inv.getHeldItemSlot(), null);
                    }
                    plugin.getRideHandler().getRide(player).feed();
                }
            }
        }
    }

    public void onPlayerAnimation(PlayerAnimationEvent event)
    {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInHand();

        if (itemInHand != null && itemInHand.getType() == Material.FISHING_ROD) {

            Ride vehicle = plugin.getRideHandler().getRide(player);

            vehicle.setDestination(player.getTargetBlock(null, MRConfig.MAX_DISTANCE).getLocation());
        }
    }

    public void onItemHeldChange(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        Ride ride = plugin.getRideHandler().getRides().get(player.getName());

        // Holding down SHIFT(sneak) and mouse scrolling adjusts speed
        if (ride != null && ride.hasGoal() && player.isSneaking()) {

            int newSlot = event.getNewSlot();
            int prevSlot = event.getPreviousSlot();
            boolean increase = (prevSlot - newSlot) > 0;

            if (((prevSlot == 0) && (newSlot == 8)) || ((prevSlot == 8) && (newSlot == 0)))
                increase = !increase;

            ride.setSpeed(increase ? ride.getSpeed() + 0.05F : ride.getSpeed() - 0.05F);
        }
    }
}
