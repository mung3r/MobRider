package com.edwardhand.mobrider;

import com.edwardhand.mobrider.commands.MobAttackCommand;
import com.edwardhand.mobrider.commands.MobFollowCommand;
import com.edwardhand.mobrider.commands.MobGoCommand;
import com.edwardhand.mobrider.commands.MobGotoCommand;
import com.edwardhand.mobrider.commands.MobStopCommand;
import com.edwardhand.mobrider.listeners.RiderEntityActionListener;
import com.edwardhand.mobrider.listeners.RiderEntityListener;
import com.edwardhand.mobrider.listeners.RiderListener;
import com.edwardhand.mobrider.managers.CommandManager;
import com.edwardhand.mobrider.managers.MobManager;
import com.edwardhand.mobrider.models.MobTarget;
import com.edwardhand.mobrider.utils.IsDigits;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityLiving;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Monster;
import org.bukkit.event.Event;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MobRider extends JavaPlugin
{	
	private ConcurrentHashMap<String, MobTarget> _targets;
	public final static HashMap<String, Vector> directions = new HashMap<String, Vector>();
	final static HashMap<String, Float> topSpeeds = new HashMap<String, Float>();
	public final static HashMap<String, String> noises = new HashMap<String, String>();
    private CommandManager _commandManager;
    private Permission _permissions;
    private MobManager _mobManager;

    private Boolean setupPermission()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            _permissions = permissionProvider.getProvider();
        }

        return (_permissions != null);
    }

    public Permission getPermissions()
    {
        return _permissions;
    }
    
    public MobManager getMobManager()
    {
        return _mobManager;
    }

	static 
	{
		//init directions
		directions.put("NORTH", new Vector(-100, 0, 0));
		directions.put("SOUTH", new Vector(100, 0, 0));
		directions.put("EAST", new Vector(0, 0, -100));
		directions.put("WEST", new Vector(0, 0, 100));
		directions.put("NORTHEAST", new Vector(-71, 0, -71));
		directions.put("SOUTHEAST", new Vector(71, 0, -71));
		directions.put("NORTHWEST", new Vector(-71, 0, 71));
		directions.put("SOUTHWEST", new Vector(71, 0, 71));
		directions.put("N", new Vector(-100, 0, 0));
		directions.put("S", new Vector(100, 0, 0));
		directions.put("E", new Vector(0, 0, -100));
		directions.put("W", new Vector(0, 0, 100));
		directions.put("NE", new Vector(-71, 0, -71));
		directions.put("SE", new Vector(71, 0, -71));
		directions.put("NW", new Vector(-71, 0, 71));
		directions.put("SW", new Vector(71, 0, 71));
		
		//init speeds
		topSpeeds.put("Spider", Float.valueOf(0.4F));
		topSpeeds.put("Zombie", Float.valueOf(0.2F));
		topSpeeds.put("Skeleton", Float.valueOf(0.2F));
		topSpeeds.put("Creeper", Float.valueOf(0.6F));
		topSpeeds.put("Sheep", Float.valueOf(0.25F));
		topSpeeds.put("Cow", Float.valueOf(0.3F));
		topSpeeds.put("Pig", Float.valueOf(0.25F));
		topSpeeds.put("Chicken", Float.valueOf(0.2F));
		topSpeeds.put("Wolf", Float.valueOf(0.8F));
		topSpeeds.put("PigZombie", Float.valueOf(0.4F));
		topSpeeds.put("Ghast", Float.valueOf(0.6F));
		topSpeeds.put("Squid", Float.valueOf(1F));
		topSpeeds.put("Monster", Float.valueOf(0.4F));
		topSpeeds.put("Giant", Float.valueOf(0.6F));
        topSpeeds.put("Slime", Float.valueOf(0.2F));
        topSpeeds.put("CaveSpider", Float.valueOf(0.4F));
        topSpeeds.put("Enderman", Float.valueOf(0.2F));
        topSpeeds.put("Silverfish", Float.valueOf(0.4F));
		
		//init noises
		noises.put("Spider", "Cheeuuuk");
		noises.put("Zombie", "Mooooaaan");
		noises.put("Skeleton", "");
		noises.put("Creeper", "Hisssss");
		noises.put("Chicken", "Cluck");
		noises.put("Cow", "Mooooo");
		noises.put("Sheep", "Baaaaa");
		noises.put("Pig", "Oink");
		noises.put("Wolf", "Woof");
		noises.put("Squid", "Glub");
		noises.put("Ghast", "Hooooo");
		noises.put("PigZombie", "Mooooiiink");
		noises.put("Monster", "Rawr");
		noises.put("Giant", "MOOOOAAAN");
        noises.put("Slime", "Blluuurrrp");
        noises.put("CaveSpider", "Cheeuuuk");
        noises.put("Enderman", "Mooooaaan");
        noises.put("Silverfish", "Cheeuuuk");
	}
	
	public final static Logger log = Logger.getLogger("Minecraft");

    public void onEnable()
    {
        log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " enabled.");
        
        RiderListener riderListener = new RiderListener(this);
        RiderEntityListener riderEntityListener = new RiderEntityListener(this);
        RiderEntityActionListener riderEntityActionlistener = new RiderEntityActionListener(this);
        setupPermission();
        _mobManager = new MobManager(this);
        _targets = new ConcurrentHashMap<String, MobTarget>();
        _commandManager = new CommandManager();
        
        _commandManager.addCommand(new MobAttackCommand(this));
        _commandManager.addCommand(new MobFollowCommand(this));
        _commandManager.addCommand(new MobGoCommand(this));
        _commandManager.addCommand(new MobGotoCommand(this));
        _commandManager.addCommand(new MobStopCommand(this));
        
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_TARGET, riderEntityListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, riderEntityListener, Event.Priority.Normal, this);
        
        getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, riderEntityActionlistener, Event.Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, riderListener, Event.Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ANIMATION, riderListener, Event.Priority.Monitor, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM_HELD, riderListener, Event.Priority.Monitor, this);
        
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new MobTask(this), 5L, 1L);
    }
    
    public void onDisable()
    {       
        getServer().getScheduler().cancelTasks(this);
        _targets = null;
        _commandManager = null;
        _permissions = null;
        _mobManager = null;
        
        log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " disabled.");
    }
    
    public ConcurrentHashMap<String, MobTarget> getTargets()
    {
        return _targets;
    }
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player))
			return false;
		Player player = (Player)sender;

		String[] split = args;
		net.minecraft.server.Entity vehicle = ((CraftPlayer)player).getHandle().vehicle;
		
        if ((vehicle == null) || (!(vehicle instanceof EntityCreature))) 
        {
            player.sendMessage("You must be mounted on a mob to use a /mob command.");
            return true;
        }
        
        if (split.length < 1)
            return false;

        if(!getPermissions().playerHas(player, "mobRider.command." + split[0].toLowerCase()))
        {
            player.sendMessage("You do not have the necessary permissions.");
            return true;
        }


        return _commandManager.dispatch(sender, cmd, commandLabel, args);
	}

	public LivingEntity findEntity(String s, Player p)
	{
		org.bukkit.entity.Entity r = null;
		
		/*  //ready for Permissions 3.0
		Integer range = getPermissions().getInfoInteger(p.getWorld().getName().toLowerCase(), p.getName().toLowerCase(), "mobRider.range", false);
		if(range == null)
		    range = getPermissions().getInfoInteger(p.getWorld().getName().toLowerCase(), p.getName().toLowerCase(), "mobRider.range", true);
		*/
		/*@SuppressWarnings("deprecation") //getPermissionInteger is deprecated in Permissions 3.x, and I'm tired of hearing about it
        Integer range = getPermissions().getPermissionInteger(p.getWorld().getName().toLowerCase(), p.getName().toLowerCase(), "mobRider.range");*/
		Integer range = null;
		
		
		if(range == null || range <= 0)
		{
    		if(IsDigits.check(s))
    		{
    			net.minecraft.server.Entity ent = ((CraftWorld)p.getWorld()).getHandle().getEntity(Integer.parseInt(s));
    			if ((ent != null) && ((ent instanceof EntityLiving)))
    				r = ent.getBukkitEntity();
    		}
    		else
    		{
    			//either a player or a mob name
    			r = getServer().getPlayer(s);
    			
    			if(r == null)
    			{
    				//hope it's a mob
    				List<LivingEntity> entities = p.getWorld().getLivingEntities();
    				Location playerLocation = p.getLocation();
    				double distance = Double.MAX_VALUE;
    				net.minecraft.server.Entity vehicle = ((CraftPlayer)p).getHandle().vehicle;
    				
    				for(LivingEntity entity : entities)
    				{
    					if(stringFromMobEntity(entity).equalsIgnoreCase(s))
    					{
    						Location mobLocation = entity.getLocation();
    						double mobDistance = Math.sqrt(Math.pow(mobLocation.getX()-playerLocation.getX(), 2) + 
    							Math.pow(mobLocation.getY()-playerLocation.getY(), 2) + 
    							Math.pow(mobLocation.getZ()-playerLocation.getZ(), 2));
    						if(mobDistance < distance && (vehicle == null || vehicle.id != entity.getEntityId()))
    						{
    							distance = mobDistance;
    							r = entity;
    						}
    					}
    				}				
    			}
    		}
		}
		else
		{
            if(IsDigits.check(s))
            {
                net.minecraft.server.Entity ent = ((CraftWorld)p.getWorld()).getHandle().getEntity(Integer.parseInt(s));
                if ((ent != null) && ((ent instanceof EntityLiving)))
                {
                    r = ent.getBukkitEntity();
                    Location rLoc = r.getLocation();
                    Location pLoc = p.getLocation();
                    if(Math.abs(rLoc.getBlockX() - pLoc.getBlockX()) > range ||
                            Math.abs(rLoc.getBlockY() - pLoc.getBlockY()) > range ||
                            Math.abs(rLoc.getBlockZ() - pLoc.getBlockZ()) > range)
                        r = null;
                }
            }
            else
            {
                //either a player or a mob name
                r = getServer().getPlayer(s);
                
                if(r == null)
                {
                    //hope it's a mob
                    List<Entity> entities = p.getNearbyEntities((double)range, (double)range, (double)range);
                    Location playerLocation = p.getLocation();
                    double distance = Double.MAX_VALUE;
                    net.minecraft.server.Entity vehicle = ((CraftPlayer)p).getHandle().vehicle;
                    
                    for(Entity entity : entities)
                    {
                        if(entity instanceof LivingEntity && stringFromMobEntity(entity).equalsIgnoreCase(s))
                        {
                            Location mobLocation = entity.getLocation();
                            double mobDistance = Math.sqrt(Math.pow(mobLocation.getX()-playerLocation.getX(), 2) + 
                                Math.pow(mobLocation.getY()-playerLocation.getY(), 2) + 
                                Math.pow(mobLocation.getZ()-playerLocation.getZ(), 2));
                            if(mobDistance < distance && (vehicle == null || vehicle.id != entity.getEntityId()))
                            {
                                distance = mobDistance;
                                r = entity;
                            }
                        }
                    }               
                }
                else
                {
                    //it's a player!
                    Location rLoc = r.getLocation();
                    Location pLoc = p.getLocation();
                    if(Math.abs(rLoc.getBlockX() - pLoc.getBlockX()) > range ||
                            Math.abs(rLoc.getBlockY() - pLoc.getBlockY()) > range ||
                            Math.abs(rLoc.getBlockZ() - pLoc.getBlockZ()) > range)
                        r = null;
                }
            }
		}
		if ((r instanceof LivingEntity)) {
			return (LivingEntity)r;
		}
		return null;
	}

	public static String stringFromMobEntity(org.bukkit.entity.Entity _mob)
	{
		if ((_mob instanceof Spider))
			return "Spider";
		if ((_mob instanceof PigZombie))
			return "PigZombie";
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
		if ((_mob instanceof Wolf))
			return "Wolf";
		if ((_mob instanceof Ghast))
			return "Ghast";
		if ((_mob instanceof Giant))
			return "Giant";
        if ((_mob instanceof Slime))
            return "Slime";
        if ((_mob instanceof CaveSpider))
            return "CaveSpider";
        if ((_mob instanceof Enderman))
            return "Enderman";
        if ((_mob instanceof Silverfish))
            return "Silverfish";
        if ((_mob instanceof Monster))
            return "Monster";

		return "";
	}
}
