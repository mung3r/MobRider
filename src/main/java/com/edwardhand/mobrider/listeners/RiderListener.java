package com.edwardhand.mobrider.listeners;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityGhast;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntityWaterAnimal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.MobManager;
import com.edwardhand.mobrider.models.MobIntent;
import com.edwardhand.mobrider.models.MobTarget;

public class RiderListener extends PlayerListener
{
	private MobRider plugin;

	public RiderListener(MobRider plugin)
	{
		this.plugin = plugin;
	}

	//This method must run even if it was cancelled.
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if ((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK))
			return;

		Material itemType = e.getPlayer().getInventory().getItemInHand().getType();

		CraftPlayer craftPlayer = (CraftPlayer)e.getPlayer();
        EntityPlayer minecraftPlayer = craftPlayer.getHandle();
		Entity vehicle = minecraftPlayer.vehicle;

		if (itemType.equals(Material.SADDLE))
		{
			if (vehicle != null)
			{
			    minecraftPlayer.setPassengerOf(null);
				return;
			}

			Location loc = craftPlayer.getTargetBlock(null, 5).getLocation();
			CraftWorld craftWorld = (CraftWorld)craftPlayer.getWorld();
			double x1 = loc.getX() + 0.5D;
			double y1 = loc.getY() + 0.5D;
			double z1 = loc.getZ() + 0.5D;

			@SuppressWarnings("rawtypes")
			List entities = new ArrayList();
			double r = 0.5D;
			while ((entities.size() == 0) && (r < 5.0D))
			{
				AxisAlignedBB bb = AxisAlignedBB.a(x1 - r, y1 - r, z1 - r, x1 + r, y1 + r, z1 + r);
				entities = craftWorld.getHandle().b(minecraftPlayer, bb);
				r += 0.5D;
			}

			if ((entities.size() == 1) && ((entities.get(0) instanceof EntityLiving)))
			{
				Entity target = (EntityLiving)entities.get(0);
				if (!testPermissions(craftPlayer, target))
					return;

				minecraftPlayer.setPassengerOf(target);
				if (target instanceof EntityCreature) {
				    ((Creature)target.getBukkitEntity()).setTarget(null);
				}
				plugin.getMobManager().setRelative(craftPlayer, 0.0D, 0.0D, 0.0D, MobIntent.STOP);
			}
		}
        else if (MobManager.isFood(itemType))
        {
            if ((vehicle != null) && (vehicle instanceof EntityCreature))
            {
                EntityCreature creatureVehicle = (EntityCreature)vehicle;
                if(creatureVehicle.health < MobManager.getMaxHealth())
                {
                    PlayerInventory inv = craftPlayer.getInventory();
                    ItemStack stack = inv.getItemInHand();
                    if(stack.getAmount() > 1)
                    {
                        stack.setAmount(stack.getAmount() - 1);
                        inv.setItem(inv.getHeldItemSlot(), stack);
                    }
                    else
                    {
                        inv.setItem(inv.getHeldItemSlot(), null);
                    }
                    creatureVehicle.health += 5;
                    creatureVehicle.health = Math.min(creatureVehicle.health, MobManager.getMaxHealth());
                    MobManager.speak(craftPlayer, vehicle.getBukkitEntity(), " :D");
                }
            }
        }
	}

	public void onPlayerAnimation(PlayerAnimationEvent e)
	{		
	    CraftPlayer player = (CraftPlayer)e.getPlayer();
		if (player.getInventory().getItemInHand().getType().equals(Material.FISHING_ROD))
		{
			Entity vehicle = player.getHandle().vehicle;

			if ((vehicle != null) && ((vehicle instanceof EntityLiving)))
			{
				Block target = player.getTargetBlock(null, 25);
				plugin.getMobManager().setAbsolute(player, target.getLocation(), MobIntent.PASSIVE);
			}
		}
	}

    public void onItemHeldChange(PlayerItemHeldEvent e)
    {
        Player player = e.getPlayer();
        MobTarget target = plugin.getTargets().get(player);
        if(target != null && player.isSneaking())
        {
            int newSlot = e.getNewSlot();
            int prevSlot = e.getPreviousSlot();
            boolean reverse = (prevSlot - newSlot) > 0;
            if (((prevSlot == 0) && (newSlot == 8)) || ((prevSlot == 8) && (newSlot == 0)))
                reverse = !reverse;
            
            float newSpeed = reverse ? target.getSpeed() - 0.05F : target.getSpeed() + 0.05F;
            if(newSpeed > 1.0F)
                newSpeed = 1.0F;
            else if (newSpeed < 0.05F)
                newSpeed = 0.05F;
            target.setSpeed(newSpeed);
        }
    }
	
	private boolean testPermissions(Player player, Entity entity)
	{
		if (plugin.getPermissions() == null) 
		{
			return true;
		}
		
		if (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)
		{
			if(plugin.getPermissions().playerHas(player, "mobRider.animals") 
			        || plugin.getPermissions().playerHas(player, "mobRider.animals." + MobRider.stringFromMobEntity(entity.getBukkitEntity()).toLowerCase()))
                return true;
            else
			{
				player.sendMessage("You do not have permission to ride animals.");
				return false;
			}
		}
		
		if (entity instanceof EntityMonster) 
		{
			if(plugin.getPermissions().playerHas(player, "mobRider.monsters")
                || plugin.getPermissions().playerHas(player, "mobRider.monsters." + MobRider.stringFromMobEntity(entity.getBukkitEntity()).toLowerCase()))
                return true;
			else
			{
				player.sendMessage("You do not have permission to ride monsters.");
				return false;
			}
		}
		
		if(entity instanceof EntityPlayer)
		{
			if(plugin.getPermissions().playerHas(player, "mobRider.players")
	                || plugin.getPermissions().playerHas(player, "mobRider.players." + ((Player)entity.getBukkitEntity()).getName().toLowerCase()))
                return true;
			else
			{
				player.sendMessage("You do not have permission to ride players.");
				return false;
			}
		}
		
        if(entity instanceof EntityGhast || entity instanceof EntitySlime)
        {
            //Silently fail; no support for these two yet.
            return false;
        }

		MobRider.log.info("! Mobrider Error: Unknown entity type.");
		return false;
	}
}
