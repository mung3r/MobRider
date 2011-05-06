package com.edwardhand.mobrider;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntitySquid;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

public class RiderEntityListener extends EntityListener
{
  private MobRider plugin;

  public RiderEntityListener(MobRider _plugin)
  {
    this.plugin = _plugin;
  }

  public void onEntityTarget(EntityTargetEvent e)
  {
    Entity passenger = ((CraftEntity)e.getEntity()).getHandle().passenger;
    if ((passenger != null) && (e.getTarget() != null) && (e.getTarget().equals(passenger.getBukkitEntity())))
    {
      e.setCancelled(true);
    }
  }

  public void onEntityDamage(EntityDamageEvent e)
  {
    Entity vehicle = ((CraftEntity)e.getEntity()).getHandle().vehicle;
    if ((vehicle != null) && ((vehicle instanceof EntityCreature)))
    {
      switch (e.getCause())
      {
      case SUFFOCATION: //ordinal 3
        e.setCancelled(true);
        break;
      case ENTITY_ATTACK: //ordinal 2
        if (!(((EntityDamageByEntityEvent)e).getDamager() instanceof LivingEntity))
          return;
        LivingEntity damager = (LivingEntity)((EntityDamageByEntityEvent)e).getDamager();
        if ((damager.equals(vehicle.getBukkitEntity())) || (damager.equals(((Creature)vehicle.getBukkitEntity()).getTarget())))
          break;
        ((Creature)vehicle.getBukkitEntity()).setTarget(damager);
        MobHandler.speak((Player)(CraftEntity)e.getEntity(), vehicle.getBukkitEntity(), "!");

        break;
      case DROWNING: //ordinal 8
        if (!(vehicle instanceof EntitySquid)) break;
        e.setCancelled(true);
      case FALL: //ordinal 4
      case FIRE: //ordinal 5
      case FIRE_TICK: //ordinal 6
      case LAVA: //ordinal 7
      } return;
    }
    if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
    {
      try {
        vehicle = ((CraftEntity)((EntityDamageByEntityEvent)e).getDamager()).getHandle().vehicle;
      } catch (Exception localException) {
      }
      if ((vehicle != null) && ((vehicle instanceof EntityCreature)))
      {
        Creature e1 = (Creature)vehicle.getBukkitEntity();
        LivingEntity e2 = (LivingEntity)e.getEntity();
        if (e1 != null && (!e1.equals(e2)) && (!e1.getTarget().equals(e2)))
        {
          e1.setTarget(e2);
          MobHandler.speak((Player)(CraftEntity)((EntityDamageByEntityEvent)e).getDamager(), vehicle.getBukkitEntity(), "!");
        }
      }
    }
  }
}