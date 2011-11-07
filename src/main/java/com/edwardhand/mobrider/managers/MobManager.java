package com.edwardhand.mobrider.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edwardhand.mobrider.MobRider;
import com.edwardhand.mobrider.models.MobEntityTarget;
import com.edwardhand.mobrider.models.MobIntent;
import com.edwardhand.mobrider.models.MobLocationTarget;

public class MobManager
{
	private static int maxHealth = 25;
	private MobRider _plugin;
	
	public MobManager(MobRider plugin)
	{
	    _plugin = plugin;
	}

	public static void speak(Player player, Entity mob, String suffix)
	{
		String type = MobRider.stringFromMobEntity(mob);

		player.sendMessage("<" + healthString(mob) + "§e" + type + "§f>" + (String)MobRider.noises.get(type) + suffix);
	}

	public static int getMaxHealth()
	{
		return maxHealth;
	}

	private static String healthString(Entity mob)
	{
		String col = "2";
		String r = "";
		int health = ((LivingEntity)mob).getHealth();

		if (health <= 6)
		{
			col = "6";
		}
		else if (health <= 12)
		{
			col = "4";
		}

		for (int i = 0; i < 5; i++)
		{
			String col2 = i < 5 * health / maxHealth ? col : "8";
			r = r + "§" + col2 + "|";
		}
		return r;
	}

	public static boolean isFood(Material mat)
    {
            switch (mat)
            {
            case BROWN_MUSHROOM:
            case CACTUS:
            case CAKE:
            case EGG:
            case ICE:
            case INK_SACK:
            case LEATHER:
            case RED_MUSHROOM:
            case RED_ROSE:
            case YELLOW_FLOWER:
            case SAPLING:
            case SEEDS:
            case SLIME_BALL:
            case WHEAT:
            case SUGAR:
            case SUGAR_CANE:
                    return true;
            }
            return false;
    }

	public void setAbsolute(Player rider, Location location, MobIntent intent)
	{
        _plugin.getTargets().put(rider.getName(), new MobLocationTarget(location, intent));
	}

	public void setAbsolute(Player rider, double x, double y, double z, MobIntent intent)
	{
	    setAbsolute(rider, new Location(rider.getWorld(), x, y, z), intent);
	}

	public void setFollowing(Player rider, Entity target, MobIntent intent)
	{
		_plugin.getTargets().put(rider.getName(), new MobEntityTarget(target, intent));
	}
	
	public void setRelative(Player rider, double x, double y, double z, MobIntent intent)
	{
	    Location riderLoc = rider.getLocation();
		setAbsolute(rider, x + riderLoc.getX(), y + riderLoc.getY(), z + riderLoc.getZ(), intent);
	}
}