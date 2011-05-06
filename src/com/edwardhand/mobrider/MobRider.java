package com.edwardhand.mobrider;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.PrintStream;
import java.util.HashMap;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.WorldServer;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class MobRider extends JavaPlugin
{
  public static HashMap<Player, MobTarget> targets = initTargets();
  public static HashMap<String, Vector> directions = initDirections();
  public static HashMap<String, Float> topSpeeds = initSpeeds();
  public static HashMap<String, String> noises = initNoises();
  public boolean permissionsEnabled;
  public PermissionHandler permissions = null;

  public boolean setupPermissions()
  {
    Plugin test = getServer().getPluginManager().getPlugin("Permissions");

    if ((this.permissions == null) && 
      (test != null)) {
      this.permissions = ((Permissions)test).getHandler();
      return true;
    }

    return false;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    if (!(sender instanceof Player))
      return false;
    Player player = (Player)sender;

    String[] split = args;
    net.minecraft.server.Entity vehicle = ((CraftPlayer)player).getHandle().vehicle;

    if ((vehicle == null) || (!(vehicle instanceof EntityCreature))) {
      return false;
    }
    if ((cmd.getName().equalsIgnoreCase("mob")) && (split.length >= 1) && ((!this.permissionsEnabled) || (this.permissions.has(player, "mobRider.command." + cmd.getName().toLowerCase()))))
    {
      if (split[0].equalsIgnoreCase("stop"))
      {
        MobHandler.setRelative(player, 0.0D, 0.0D, 0.0D);
        ((Creature)vehicle.getBukkitEntity()).setTarget(null);
        MobHandler.speak(player, vehicle.getBukkitEntity(), "");
      }
      else if (split[0].equalsIgnoreCase("go"))
      {
        if (split.length >= 2)
        {
          Vector v = (Vector)directions.get(split[1].toUpperCase());
          int dist = 100;
          if (split.length >= 3)
            try {
              dist = Integer.parseInt(split[2]); } catch (NumberFormatException localNumberFormatException) {
            }
          if (v != null)
          {
            v.multiply(Math.min(2.5D, dist / 100.0D));
            MobHandler.setRelative(player, v.getX(), player.getWorld().getHighestBlockYAt((int)v.getX(), (int)v.getZ()), v.getZ());
            MobHandler.speak(player, vehicle.getBukkitEntity(), "");
          }
          else
          {
            MobHandler.speak(player, vehicle.getBukkitEntity(), "?");
          }
        }
      }
      else if (split[0].equalsIgnoreCase("goto"))
      {
        if (split.length == 2)
        {
          LivingEntity target = findEntity(split[1], player.getWorld());
          if (target != null)
          {
            MobHandler.setFollowing(player, target);
            MobHandler.speak(player, vehicle.getBukkitEntity(), "");
          }
          else
          {
            MobHandler.speak(player, vehicle.getBukkitEntity(), "?");
          }
        }
        else if (split.length == 3)
        {
          try
          {
            Location loc = new Location(player.getWorld(), Integer.parseInt(split[1]), 64.0D, Integer.parseInt(split[2]));
            MobHandler.setAbsolute(player, loc);
          }
          catch (NumberFormatException localNumberFormatException1)
          {
          }

        }

      }
      else if (split[0].equalsIgnoreCase("follow"))
      {
        if (split.length >= 2)
        {
          LivingEntity target = findEntity(split[1], player.getWorld());
          if (target != null)
          {
            MobHandler.setFollowing(player, target, MobIntent.FOLLOW);
            MobHandler.speak(player, vehicle.getBukkitEntity(), "");
          }
          else
          {
            MobHandler.speak(player, vehicle.getBukkitEntity(), "?");
          }
        }
      }
      else if (split[0].equalsIgnoreCase("attack"))
      {
        if (split.length >= 2)
        {
          LivingEntity target = findEntity(split[1], player.getWorld());
          if (target != null)
          {
            MobHandler.setFollowing(player, target, MobIntent.ATTACK);
            MobHandler.speak(player, vehicle.getBukkitEntity(), "!");
          }
          else
          {
            MobHandler.speak(player, vehicle.getBukkitEntity(), "?");
          }
        }
      }
      else if (split[0].equalsIgnoreCase("mount"))
      {
        if (split.length >= 2)
        {
          LivingEntity target = findEntity(split[1], player.getWorld());
          if ((target.equals(player)) || (target.equals(vehicle.getBukkitEntity()))) {
            return true;
          }
          if (target != null)
          {
            vehicle.setPassengerOf(null);
            MobHandler.setFollowing(player, target, MobIntent.MOUNT);
            MobHandler.speak(player, vehicle.getBukkitEntity(), "");
          }
          else
          {
            MobHandler.speak(player, vehicle.getBukkitEntity(), "?");
          }
        }
      }
      else if (split[0].equalsIgnoreCase("unmount"))
      {
        vehicle.setPassengerOf(null);
      }
      else
      {
        MobHandler.speak(player, vehicle.getBukkitEntity(), "?");
      }
    }
    return true;
  }

  private LivingEntity findEntity(String _s, World _w)
  {
    org.bukkit.entity.Entity r = null;
    try {
      net.minecraft.server.Entity ent = ((CraftWorld)_w).getHandle().getEntity(Integer.parseInt(_s));
      if ((ent != null) && ((ent instanceof EntityLiving)))
        r = ent.getBukkitEntity();
    }
    catch (NumberFormatException X)
    {
      r = getServer().getPlayer(_s);
    }
    if ((r instanceof LivingEntity)) {
      return (LivingEntity)r;
    }
    return null;
  }

  public void onDisable()
  {
  }

  public void onEnable()
  {
    RiderListener listener = new RiderListener(this);
    RiderEntityListener listener2 = new RiderEntityListener(this);

    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, listener, Event.Priority.Highest, this);
    getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, listener, Event.Priority.Monitor, this);

    getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, listener2, Event.Priority.Highest, this);
    getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, listener2, Event.Priority.Highest, this);

    TickHandler tickHandler = new TickHandler(this);
    getServer().getScheduler().scheduleSyncRepeatingTask(this, tickHandler, 5L, 1L);

    this.permissionsEnabled = setupPermissions();

    System.out.println("MobRider enabled");
    if (this.permissionsEnabled)
    {
      System.out.println("MobRider - Permissions Plugin Detected");
    }
    else
    {
      System.out.println("MobRider - Permissions Plugin Not Detected (enabling for all users)");
    }
  }

  private static HashMap<Player, MobTarget> initTargets()
  {
    HashMap map = new HashMap();
    return map;
  }

  private static HashMap<String, Float> initSpeeds()
  {
    HashMap map = new HashMap();
    map.put("Spider", Float.valueOf(0.4F));
    map.put("Zombie", Float.valueOf(0.2F));
    map.put("Skeleton", Float.valueOf(0.2F));
    map.put("Creeper", Float.valueOf(0.6F));
    map.put("Sheep", Float.valueOf(0.25F));
    map.put("Cow", Float.valueOf(0.3F));
    map.put("Pig", Float.valueOf(0.25F));
    map.put("Chicken", Float.valueOf(0.2F));
    return map;
  }

  private static HashMap<String, Vector> initDirections()
  {
    HashMap map = new HashMap();
    map.put("NORTH", new Vector(-100, 0, 0));
    map.put("SOUTH", new Vector(100, 0, 0));
    map.put("EAST", new Vector(0, 0, -100));
    map.put("WEST", new Vector(0, 0, 100));
    map.put("NORTHEAST", new Vector(-71, 0, -71));
    map.put("SOUTHEAST", new Vector(71, 0, -71));
    map.put("NORTHWEST", new Vector(-71, 0, 71));
    map.put("SOUTHWEST", new Vector(71, 0, 71));
    return map;
  }

  private static HashMap<String, String> initNoises()
  {
    HashMap map = new HashMap();
    map.put("Spider", "Cheeuuuk");
    map.put("zombie", "mooooaaan");
    map.put("Skeleton", "");
    map.put("Creeper", "hisssss");
    map.put("Chicken", "cluck");
    map.put("Cow", "mooooo");
    map.put("Sheep", "baaaaa");
    map.put("Pig", "oink");
    map.put("Squid", "Spy's sappin mah sentry!");
    map.put("Ghast", "hooooo");
    return map;
  }

  public static String mobType(org.bukkit.entity.Entity _mob)
  {
    if ((_mob instanceof Spider))
      return "Spider";
    if ((_mob instanceof Zombie))
      return "Zombie";
    if ((_mob instanceof Skeleton))
      return "Skeleton";
    if ((_mob instanceof Creeper))
      return "Creeper";
    if ((_mob instanceof Chicken))
      return "Chicken";
    if ((_mob instanceof Cow))
      return "Cow";
    if ((_mob instanceof Sheep))
      return "Sheep";
    if ((_mob instanceof Pig))
      return "Pig";
    if ((_mob instanceof Squid))
      return "Squid";
    if ((_mob instanceof Ghast)) {
      return "Ghast";
    }
    return "";
  }
}