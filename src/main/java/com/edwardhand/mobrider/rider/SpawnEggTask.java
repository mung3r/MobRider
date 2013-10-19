/*
 * This file is part of MobRider.
 *
 * Copyright (c) 2011-2013, R. Ramos <http://github.com/mung3r/>
 * MobRider is licensed under the GNU Lesser General Public License.
 *
 * MobRider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MobRider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.edwardhand.mobrider.rider;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

public class SpawnEggTask implements Runnable
{
    private static final int EFFECT_TICKS = 20;
    private static final int EFFECT_DATA = 4;
    private static final int EFFECT_RADIUS = 10;

    private LivingEntity target;
    private int ticks;
    private int taskId;

    public SpawnEggTask(LivingEntity target)
    {
        this.target = target;
        ticks = 0;
    }

    public int getTaskId()
    {
        return taskId;
    }

    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    @Override
    public void run()
    {
        if (target == null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        else {
            if (ticks < EFFECT_TICKS) {
                target.getWorld().playEffect(target.getLocation(), Effect.EXTINGUISH, EFFECT_DATA, EFFECT_RADIUS);
                target.getWorld().playEffect(target.getLocation(), Effect.SMOKE, EFFECT_DATA, EFFECT_RADIUS);
                ticks++;
            }
            else {
                target.getWorld().dropItemNaturally(target.getLocation(), getSpawnEgg((LivingEntity) target));
                target.remove();
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
    }

    private static ItemStack getSpawnEgg(LivingEntity entity)
    {
        SpawnEgg egg = new SpawnEgg(entity.getType());
        return new ItemStack(egg.getItemType(), 1, egg.getData());
    }
}
