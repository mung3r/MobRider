package com.edwardhand.mobrider.listeners;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.managers.MobManager;

public class RiderEntityActionListener  extends EntityListener
{
    private MobRider plugin;
    
	public RiderEntityActionListener(MobRider plugin)
	{
		this.plugin = plugin;
	}
	
	public void onEntityDamage(EntityDamageEvent e)
	{
		if(e.isCancelled())
			return;
	
		if(e.getEntity() instanceof Player)
		{
			Entity vehicle = ((CraftEntity)e.getEntity()).getHandle().vehicle;
			if ((vehicle != null) && ((vehicle instanceof EntityCreature)))
			{
				switch (e.getCause())
				{
				case ENTITY_ATTACK:
					if(e instanceof EntityDamageByEntityEvent)
					{
						if (!(((EntityDamageByEntityEvent)e).getDamager() instanceof LivingEntity))
							return;
						LivingEntity damager = (LivingEntity)((EntityDamageByEntityEvent)e).getDamager();
						if ((damager.equals(vehicle.getBukkitEntity())) || (damager.equals(((Creature)vehicle.getBukkitEntity()).getTarget())))
							break;
						((Creature)vehicle.getBukkitEntity()).setTarget(damager);
						MobManager.speak((Player)e.getEntity(), vehicle.getBukkitEntity(), "!");
					}
					else
					{
						MobRider.log.info("? MobRider Warning: Unexpected damage cause in onEntityDamage, case ENTITY_ATTACK.");
					}
					break;
				} 
				
				return;
			}
			
			if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
			{
				//If an entity is attacked by a mounted player, the mount joins in the attack.
				try 
				{
				    CraftEntity damager = (CraftEntity)((EntityDamageByEntityEvent)e).getDamager();
				    if(damager != null)
				        vehicle = damager.getHandle().vehicle;
				} 
				catch (Exception localException) 
				{
				    MobRider.log.info("? MobRider Warning: Catching local exception at RiderEntityActionListener.");
				}
				
				if ((vehicle != null) && ((vehicle instanceof EntityCreature)))
				{
					Creature damagerVehicle = (Creature)vehicle.getBukkitEntity();
					LivingEntity damagedEntity = (LivingEntity)e.getEntity(); //Might be a SKELETON
					
					if ((!damagerVehicle.equals(damagedEntity)) && (damagerVehicle.getTarget() == null || (!damagerVehicle.getTarget().equals(damagedEntity))))
					{
						damagerVehicle.setTarget(damagedEntity);
						MobManager.speak((Player)(CraftEntity)((EntityDamageByEntityEvent)e).getDamager(), vehicle.getBukkitEntity(), "!");
					}
				}
			}
		}
	}
}
