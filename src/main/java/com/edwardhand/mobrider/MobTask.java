package com.edwardhand.mobrider;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.edwardhand.mobrider.models.MobEntityTarget;
import com.edwardhand.mobrider.models.MobTarget;

public class MobTask implements Runnable
{
	private MobRider _plugin;

	MobTask(MobRider plugin)
	{
		_plugin = plugin;
	}

	public void run()
	{
	    for (String playerName : _plugin.getTargets().keySet())
	    {
            CraftPlayer player = (CraftPlayer)_plugin.getServer().getPlayer(playerName);
            if (player == null)
            {
                _plugin.getTargets().remove(playerName);
                continue;
            }
            
            Entity vehicle = player.getHandle().vehicle;
            if ((vehicle != null) && ((vehicle instanceof EntityCreature)) && (_plugin.getTargets().containsKey(player.getName())))
            {
                MobTarget target = _plugin.getTargets().get(player.getName());
                Location targetLocation = target.getLocation();
                CraftWorld playerWorld = (CraftWorld)player.getWorld();
                
                switch(target.getType())
                {
                case ENTITY:
                    switch (target.getIntent())
                    {
                    case ATTACK:
                        ((Creature)vehicle.getBukkitEntity()).setTarget((LivingEntity)((MobEntityTarget)target).getTarget());
                        break;
                    case MOUNT:
                        if(vehicle.getBukkitEntity().getLocation().toVector().subtract(targetLocation.toVector()).lengthSquared() < 9.0D)
                            vehicle.setPassengerOf(((CraftEntity)((MobEntityTarget)target).getTarget()).getHandle());
                        break;
                    }
                    
                    if(vehicle.getBukkitEntity().getLocation().toVector().subtract(targetLocation.toVector()).lengthSquared() >= 9.0D)
                        ((EntityCreature)vehicle).pathEntity = playerWorld.getHandle().findPath(vehicle, ((CraftEntity)(((MobEntityTarget)target).getTarget())).getHandle(), 16.0f);
                    break;
                case LOCATION:
                    switch(target.getIntent())
                    {
                    case STOP:
                        {
                            if(vehicle.getBukkitEntity().getLocation().toVector().subtract(targetLocation.toVector()).lengthSquared() < 4.0D)
                            {
                                vehicle.getBukkitEntity().teleport(targetLocation);
                            }
                            else
                            {
                                ((EntityCreature)vehicle).setPathEntity(new PathEntity(new PathPoint[]{ new PathPoint(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ()) }));
                            }
                        }
                        break;
                    default:
                        {
                            ((EntityCreature)vehicle).setPathEntity(new PathEntity(new PathPoint[]{ new PathPoint(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ()) }));
                        }
                        break;
                    }
                    break;
                }

                //if (((EntityLiving)vehicle).health <= 20)
                //    continue;
                String type = MobRider.stringFromMobEntity(vehicle.getBukkitEntity());
                if (!MobRider.topSpeeds.containsKey(type))
                    continue;
                float topSpeed = ((Float)MobRider.topSpeeds.get(type)).floatValue();
                float m = ((((EntityLiving)vehicle).health/25.0F) * 0.5F + 0.5F) * topSpeed * target.getSpeed();
                
                //if ((getSpeed(vehicle) >= topSpeed) || (getSpeed(vehicle) <= topSpeed / 4.0F))
                float currentSpeed = getSpeed(vehicle);
                if (currentSpeed >= m)
                    continue;
                //setSpeed(vehicle, m, currentSpeed);
            }
	    }
	}

	private float getSpeed(Entity entity)
	{
		return (float)Math.sqrt(entity.motX * entity.motX + entity.motZ * entity.motZ);
	}

	private void setSpeed(net.minecraft.server.Entity entity, float speed, float currentSpeed)
	{
		float m = speed / currentSpeed;
		entity.motX *= m;
		entity.motZ *= m;
	}
}