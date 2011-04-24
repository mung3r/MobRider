package com.edwardhand.mobrider;

import com.nijiko.permissions.PermissionHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RiderListener extends PlayerListener
{
  private MobRider plugin;

  public RiderListener(MobRider _plugin)
  {
    this.plugin = _plugin;
  }

  public void onPlayerInteract(PlayerInteractEvent e)
  {
    if ((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK))
      return;
    Material itemType = e.getPlayer().getInventory().getItemInHand().getType();

    CraftPlayer craftPlayer = (CraftPlayer)e.getPlayer();

    if (itemType.equals(Material.SADDLE))
    {
      if (craftPlayer.getHandle().vehicle != null)
      {
        craftPlayer.getHandle().setPassengerOf(null);
        return;
      }

      Location loc = e.getPlayer().getTargetBlock(null, 5).getLocation();
      CraftWorld craftWorld = (CraftWorld)e.getPlayer().getWorld();
      double x1 = loc.getX() + 0.5D;
      double y1 = loc.getY() + 0.5D;
      double z1 = loc.getZ() + 0.5D;

      List entities = new ArrayList();
      double r = 0.5D;
      while ((entities.size() == 0) && (r < 5.0D))
      {
        AxisAlignedBB bb = AxisAlignedBB.a(x1 - r, y1 - r, z1 - r, x1 + r, y1 + r, z1 + r);
        entities = craftWorld.getHandle().b(((CraftPlayer)e.getPlayer()).getHandle(), bb);
        r += 0.5D;
      }

      if ((entities.size() == 1) && ((entities.get(0) instanceof EntityLiving)))
      {
        Entity target = (EntityLiving)entities.get(0);
        if (!testPermissions(e.getPlayer(), target))
          return;
        ((CraftPlayer)e.getPlayer()).getHandle().setPassengerOf(target);
        MobHandler.setRelative(e.getPlayer(), 0.0D, 0.0D, 0.0D);
      }
    }
    else if (MobHandler.isFood(itemType))
    {
      Entity vehicle = craftPlayer.getHandle().vehicle;
      if ((vehicle != null) && ((vehicle instanceof EntityCreature)) && (((EntityCreature)vehicle).health < MobHandler.getMaxHealth()))
      {
        ItemStack stack = e.getPlayer().getInventory().getItemInHand();
        stack.setAmount(stack.getAmount() - 1);
        e.getPlayer().getInventory().setItem(e.getPlayer().getInventory().getHeldItemSlot(), stack);
        ((EntityCreature)vehicle).health += 5;
        ((EntityCreature)vehicle).health = Math.min(((EntityCreature)vehicle).health, MobHandler.getMaxHealth());
        MobHandler.speak(e.getPlayer(), vehicle.getBukkitEntity(), " :D");
      }
    }
  }

  public void onPlayerAnimation(PlayerAnimationEvent e)
  {
    if (e.getPlayer().getInventory().getItemInHand().getType().equals(Material.FISHING_ROD))
    {
      Entity vehicle = ((CraftPlayer)e.getPlayer()).getHandle().vehicle;

      if ((vehicle != null) && ((vehicle instanceof EntityLiving)))
      {
        Block target = e.getPlayer().getTargetBlock(null, 25);
        MobHandler.setAbsolute(e.getPlayer(), target.getLocation());
      }
    } else {
      e.getPlayer().getInventory().getItemInHand().getType().equals(Material.LEATHER);
    }
  }

  private boolean testPermissions(Player _player, Entity _entity)
  {
    if (!this.plugin.permissionsEnabled) {
      return true;
    }
    if (((_entity instanceof EntityAnimal)) && (!this.plugin.permissions.has(_player, "mobRider.animals")))
      return false;
    if (((_entity instanceof EntityMonster)) && (!this.plugin.permissions.has(_player, "mobRider.monsters"))) {
      return false;
    }
    return (!(_entity instanceof EntityPlayer)) || (this.plugin.permissions.has(_player, "mobRider.players"));
  }
}