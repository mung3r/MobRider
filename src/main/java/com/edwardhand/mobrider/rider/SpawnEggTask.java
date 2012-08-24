package com.edwardhand.mobrider.rider;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

public class SpawnEggTask implements Runnable
{
    private static final int EFFECT_TICKS = 20;

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
                target.getWorld().playEffect(target.getLocation(), Effect.EXTINGUISH, 4, 10);
                target.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 4, 10);
                ticks++;
            }
            else {
                target.getWorld().dropItemNaturally(target.getLocation(), getSpawnEgg((LivingEntity) target));
                target.remove();
                Bukkit.getScheduler().cancelTask(taskId);
            }
        }
    }

    private ItemStack getSpawnEgg(LivingEntity entity)
    {
        SpawnEgg egg = new SpawnEgg(entity.getType());
        return new ItemStack(egg.getItemType(), 1, egg.getData());
    }
}
