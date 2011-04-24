package com.edwardhand.mobrider;

import java.util.HashMap;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TickHandler
  implements Runnable
{
  private MobRider plugin;

  public TickHandler(MobRider _plugin)
  {
    this.plugin = _plugin;
  }

  public void run()
  {
    Player[] players = this.plugin.getServer().getOnlinePlayers();
    for (Player p : players)
    {
      CraftPlayer craftPlayer = (CraftPlayer)p;
      net.minecraft.server.Entity vehicle = craftPlayer.getHandle().vehicle;

      if ((vehicle != null) && ((vehicle instanceof EntityCreature)) && (MobRider.targets.containsKey(p)))
      {
        MobTarget target = (MobTarget)MobRider.targets.get(p);
        Location loc = target.getLocation();

        if ((target.getType() == TargetType.ENTITY) && (vehicle.getBukkitEntity().getLocation().toVector().subtract(loc.toVector()).lengthSquared() < 25.0D))
        {
          switch ($SWITCH_TABLE$com$edwardhand$mobrider$MobIntent()[target.getIntent().ordinal()])
          {
          case 3:
            ((Creature)vehicle.getBukkitEntity()).setTarget((LivingEntity)target.getTarget());
            break;
          case 4:
            vehicle.setPassengerOf(((CraftEntity)target.getTarget()).getHandle());
          }

          if (target.getIntent() != MobIntent.FOLLOW) {
            MobHandler.setRelative(p, 0.0D, 0.0D, 0.0D);
          }
        }
        PathPoint[] pp = { new PathPoint(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()) };
        ((EntityCreature)vehicle).a = new PathEntity(pp);

        if (((EntityLiving)vehicle).health <= 20)
          continue;
        float m = (((EntityLiving)vehicle).health - 20) / 10.0F + 0.5F;
        String type = MobRider.mobType(vehicle.getBukkitEntity());
        if (!MobRider.topSpeeds.containsKey(type))
          continue;
        float topSpeed = ((Float)MobRider.topSpeeds.get(type)).floatValue();
        if ((getSpeed(vehicle) >= topSpeed) || (getSpeed(vehicle) <= topSpeed / 4.0F))
          continue;
        setSpeed(vehicle, topSpeed);
      }
      else
      {
        if (!MobRider.targets.containsKey(p))
          continue;
        MobRider.targets.remove(p);
      }
    }
  }

  private float getSpeed(net.minecraft.server.Entity _entity)
  {
    return (float)Math.sqrt(_entity.motX * _entity.motX + _entity.motZ * _entity.motZ);
  }

  private void setSpeed(net.minecraft.server.Entity _entity, float _speed)
  {
    float m = _speed / getSpeed(_entity);
    _entity.motX *= m;
    _entity.motZ *= m;
  }
}