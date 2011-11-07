package com.edwardhand.mobrider.listeners;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntitySquid;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

import com.edwardhand.mobrider.MobRider;

public class RiderEntityListener extends EntityListener
{
	@SuppressWarnings("unused") //Might want this around eventually
    private MobRider plugin;

	public RiderEntityListener(MobRider plugin)
	{
		this.plugin = plugin;
	}

	public void onEntityTarget(EntityTargetEvent e)
	{
		if(e.isCancelled())
			return;
		
		Entity passenger = ((CraftEntity)e.getEntity()).getHandle().passenger;
		org.bukkit.entity.Entity target = e.getTarget();
		if ((passenger != null) && (target != null) && (target.equals(passenger.getBukkitEntity())))
			e.setCancelled(true);
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
				case SUFFOCATION:
					e.setCancelled(true);
					break;
				case DROWNING:
					if (vehicle instanceof EntitySquid)
						e.setCancelled(true);
				} 
				return;
			}
		}
	}
}