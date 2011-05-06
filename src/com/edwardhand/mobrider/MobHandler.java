package com.edwardhand.mobrider;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MobHandler
{
  private static int maxHealth = 25;

  public static void speak(Player _player, Entity _mob, String _suffix)
  {
    String type = MobRider.mobType(_mob);

    _player.sendMessage("<" + healthString(_mob) + "§e" + type + "§f>" + (String)MobRider.noises.get(type) + _suffix);
  }

  public static int getMaxHealth()
  {
    return maxHealth;
  }

  private static String healthString(Entity _mob)
  {
    String col = "2";
    String r = "";
    int health = ((LivingEntity)_mob).getHealth();

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

  public static boolean isFood(Material _mat)
  {
    switch (_mat)
    {
    case YELLOW_FLOWER: //ordinal 29
    case RED_ROSE: //ordinal 30
    case BROWN_MUSHROOM: //ordinal 31
    case RED_MUSHROOM: //ordinal 32
    case PUMPKIN: //ordinal 78
    case APPLE: //ordinal 92
    case MUSHROOM_SOUP: //ordinal 114
    case SEEDS: //ordinal 127
    case WHEAT: //ordinal 128
    case BREAD: //ordinal 129
    case PORK: //ordinal 151
    case GRILLED_PORK: //ordinal 152
    case MILK_BUCKET: //ordinal 167
    case EGG: //ordinal 176
    case RAW_FISH: //ordinal 181
    case COOKED_FISH: //ordinal 182
    case SUGAR: //ordinal 185
    case CAKE: //ordinal 186
      return true;
    }
    return false;
  }

  public static void setAbsolute(Player _rider, Location _location)
  {
    setAbsolute(_rider, _location.getX(), _location.getY(), _location.getZ());
  }

  public static void setAbsolute(Player _rider, double _x, double _y, double _z)
  {
    while (MobRider.targets.containsKey(_rider))
      MobRider.targets.remove(_rider);
    MobTarget target = new MobTarget(new Location(_rider.getWorld(), _x, _y, _z));
    MobRider.targets.put(_rider, target);
  }

  public static void setFollowing(Player _rider, Entity _target, MobIntent _intent)
  {
    while (MobRider.targets.containsKey(_rider))
      MobRider.targets.remove(_rider);
    MobTarget target = new MobTarget(_target, _intent);
    MobRider.targets.put(_rider, target);
  }

  public static void setFollowing(Player _rider, Entity _target)
  {
    setFollowing(_rider, _target, MobIntent.PASSIVE);
  }

  public static void setRelative(Player _rider, double _x, double _y, double _z)
  {
    setAbsolute(_rider, _x + _rider.getLocation().getX(), _y + _rider.getLocation().getY(), _z + _rider.getLocation().getZ());
  }
}