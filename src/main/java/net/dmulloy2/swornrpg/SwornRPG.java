/**
* SwornRPG - a bukkit plugin
* Copyright (C) 2013 dmulloy2
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package net.dmulloy2.swornrpg;

/**Java Imports**/
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**Plugin imports**/
import net.dmulloy2.swornrpg.commands.*;
import net.dmulloy2.swornrpg.handlers.*;
import net.dmulloy2.swornrpg.listeners.*;
import net.dmulloy2.swornrpg.util.*;
import net.dmulloy2.swornrpg.data.*;

/**Other Plugin Imports**/
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import net.milkbowl.vault.economy.Economy;
import lombok.Getter;

/**Bukkit imports**/
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class SwornRPG extends JavaPlugin
{
	/**Getters**/
	private @Getter Economy economy;
	private @Getter PluginManager pluginManager;
	
	private @Getter PlayerDataCache playerDataCache;
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter LogHandler logHandler;
	
	private @Getter PlayerHealthBar playerHealthBar;
	private @Getter AbilitiesManager abilitiesManager;
	private @Getter TagManager tagManager;
	private @Getter ExperienceManager experienceManager;

	/**Proposal Map**/
    public HashMap<String, String> proposal = new HashMap<String, String>();
    
    /**Configuration**/
	public boolean irondoorprotect, randomdrops, axekb, arrowfire, deathbook,
	frenzyenabled, onlinetime, playerkills, mobkills, xpreward, items, xplevel,
	money, update, spenabled, debug, salvaging, ammoenabled, healthtags, playerhealth,
	marriage, taming, confusion, fishing, herbalism, savecache, enchanting, blockredemption,
	speedboost, gracefulroll;
	
	public int frenzyd, basemoney, itemperlevel, itemreward, xplevelgain,
	killergain, killedloss, mobkillsxp, spbaseduration, frenzycd, frenzym, 
	superpickcd, superpickm, ammobaseduration, ammocooldown, ammomultiplier,
	campingrad, onlinegain, taminggain, confusionduration, fishinggain, herbalismgain,
	saveinterval, enchantbase, speedboostduration, speedboostodds, gracefulrollodds;
	
	public String salvage, tagformat;
    public List<String> disabledWorlds, redeemBlacklist;
    
    public HashMap<String, HashMap<Integer, Integer>> salvageRef = new HashMap<String, HashMap<Integer, Integer>>();
    public Map<Integer, List<BlockDrop>> blockDropsMap = new HashMap<Integer, List<BlockDrop>>();
    public Map<Integer, List<BlockDrop>> fishDropsMap = new HashMap<Integer, List<BlockDrop>>();
	
    /**Update Checking**/
	private double newVersion, currentVersion;
    
	/**Global Prefix**/
	public String prefix = ChatColor.GOLD + "[SwornRPG] ";

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();

		/**Register Handlers  /Managers**/
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler();
		logHandler = new LogHandler(this);
		playerDataCache = new PlayerDataCache(this);
		
		playerHealthBar = new PlayerHealthBar(this);
		abilitiesManager = new AbilitiesManager(this);
		tagManager = new TagManager(this);
		experienceManager = new ExperienceManager(this);
		
		pluginManager = getServer().getPluginManager();
		
		/**Resource Handler / Messages**/
		saveResource("messages.properties", true);
		resourceHandler = new ResourceHandler(this, getClassLoader());
		
		/**Update Checker**/
		currentVersion = Double.valueOf(getDescription().getVersion().replaceFirst("\\.", ""));
		
		/**Register Listeners**/
		pluginManager.registerEvents(new PlayerListener(this), this);
		pluginManager.registerEvents(new EntityListener(this), this);
		pluginManager.registerEvents(new BlockListener(this), this);
		pluginManager.registerEvents(new ExperienceListener(this), this);
		
		/**Check for PlayerData folder**/
		File playersFile = new File(getDataFolder(), "players");
		if (!playersFile.exists())
		{
			playersFile.mkdir();
		}

		/**Check for Config**/
        File conf = new File(getDataFolder(), "config.yml");
        if (!conf.exists())
        {
        	outConsole(getMessage("log_configuration"));
        	saveDefaultConfig();
        }
        
        /**Load Config**/
		loadConfig();
		reloadConfig();
		
		/**Update Block Tables**/
		updateBlockDrops();
		updateFishDrops();
		
		/**Initialize Tags**/
		tagManager.load();

		/**Register Prefixed Commands**/
		commandHandler.setCommandPrefix("srpg");
		commandHandler.registerPrefixedCommand(new CmdHelp (this));
		commandHandler.registerPrefixedCommand(new CmdLeaderboard (this));
		commandHandler.registerPrefixedCommand(new CmdVersion (this));
		commandHandler.registerPrefixedCommand(new CmdReload (this));
		
		/**Register Non-Prefixed Commands**/
		commandHandler.registerCommand(new CmdAChat (this));
		commandHandler.registerCommand(new CmdAddxp (this));
		commandHandler.registerCommand(new CmdASay (this));
		commandHandler.registerCommand(new CmdCoordsToggle (this));
		commandHandler.registerCommand(new CmdDeny (this));
		commandHandler.registerCommand(new CmdDivorce (this));
		commandHandler.registerCommand(new CmdEject (this));
		commandHandler.registerCommand(new CmdFrenzy (this));
		commandHandler.registerCommand(new CmdHat (this));
		commandHandler.registerCommand(new CmdHighCouncil (this));
		commandHandler.registerCommand(new CmdItemName (this));
		commandHandler.registerCommand(new CmdLevel (this));
		commandHandler.registerCommand(new CmdLevelr (this));
		commandHandler.registerCommand(new CmdMarry (this));
		commandHandler.registerCommand(new CmdMatch (this));
		commandHandler.registerCommand(new CmdMine (this));
		commandHandler.registerCommand(new CmdPropose (this));
		commandHandler.registerCommand(new CmdRide (this));
		commandHandler.registerCommand(new CmdSpouse (this));
		commandHandler.registerCommand(new CmdStandup (this));
		commandHandler.registerCommand(new CmdTag (this));
		commandHandler.registerCommand(new CmdTagr (this));
		commandHandler.registerCommand(new CmdUnride (this));
		commandHandler.registerCommand(new CmdStaffList (this));
		commandHandler.registerCommand(new CmdSitdown (this));
		commandHandler.registerCommand(new CmdUnlimitedAmmo (this));
		commandHandler.registerCommand(new CmdAbilities (this));

		/**Update Health if Reload**/
		for (Player player : getServer().getOnlinePlayers())
		{
			try 
			{
				playerHealthBar.updateHealth(player);
			}
			catch (Exception e)
			{
				if (debug) outConsole(Level.SEVERE, getMessage("log_health_error"), e.getMessage());
			}
		}
	
		/**Setup Vault**/
		setupVault();

		/**Schedule player data cache saving**/
		if (savecache)
		{
			int interval = 20 * 60 * saveinterval;
			new AutoSaveThread().runTaskTimer(this, interval, interval);
		}
		
		/**Frenzy cooldown**/
		if (frenzyenabled)
			new FrenzyCooldownThread().runTaskTimer(this, 0, 20);
		
		/**Superpick cooldown**/
		if (spenabled)
			new SuperPickCooldownThread().runTaskTimer(this, 0, 20);
		
		/**Ammo cooldown**/
		if (pluginManager.isPluginEnabled("SwornGuns"))
		{
			outConsole(getMessage("log_gun_found"));
			pluginManager.registerEvents(new SwornGunsListener(this), this);
			if (ammoenabled)
				new AmmoCooldownThread().runTaskTimer(this, 0, 20);
		}
		else
			outConsole(getMessage("log_gun_notfound"));
		
		/**Update Checker**/
		if (update)
			new UpdateCheckThread().runTaskTimer(this, 0, 432000);
		
		/**Online Time**/
		if (onlinetime)
			new OnlineGainThread().runTaskTimer(this, 0, 1200);

		long finish = System.currentTimeMillis();
		
		outConsole(getMessage("log_enabled"), getDescription().getFullName(), finish - start);
	}
	
	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();
		
		/**Save Data**/
		playerDataCache.save();
		
		/**Clear Memory**/
		clearMemory();

		/**Cancel tasks / services**/
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);
		
		long finish = System.currentTimeMillis();
		
		outConsole(getMessage("log_disabled"), getDescription().getFullName(), finish - start);
	}
	
	/**Clear Memory**/
	public void clearMemory()
	{
		playerHealthBar.clear();
		tagManager.clearMemory();
		proposal.clear();
		salvageRef.clear();
		blockDropsMap.clear();
		fishDropsMap.clear();
	}
	    
	/**Console logging**/
	public void outConsole(String string, Object... objects)
	{
		logHandler.log(string, objects);
	}
	
	public void outConsole(Level level, String string, Object... objects)
	{
		logHandler.log(level, string, objects);
	}
	
	public void debug(String string, Object... objects)
	{
		if (debug) outConsole("[Debug] " + string, objects);
	}
	
	/**Loads the configuration**/
	private void loadConfig() 
	{
		/**General Config Options**/
		irondoorprotect = getConfig().getBoolean("irondoorprotect");
		randomdrops = getConfig().getBoolean("randomdrops");
		axekb = getConfig().getBoolean("axekb");
		arrowfire = getConfig().getBoolean("arrowfire");
		deathbook = getConfig().getBoolean("deathbook");
		update = getConfig().getBoolean("updatechecker");
		debug = getConfig().getBoolean("debug");
		campingrad = getConfig().getInt("campingradius");
		healthtags = getConfig().getBoolean("healthtags.enabled");
		tagformat = getConfig().getString("healthtags.format");
		disabledWorlds = getConfig().getStringList("disabled-worlds");
		playerhealth = getConfig().getBoolean("playerhealth.enabled");
		marriage = getConfig().getBoolean("marriage");
		confusion = getConfig().getBoolean("confusion.enabled");
		confusionduration = getConfig().getInt("confusion.duration");
		savecache = getConfig().getBoolean("autosave.enabled");
		saveinterval = getConfig().getInt("autosave.interval");
		blockredemption = getConfig().getBoolean("redeem-enabled");
		redeemBlacklist = getConfig().getStringList("redeem-blacklist");

		/**Salvaging**/
		salvaging = getConfig().getBoolean("salvaging");
		salvage = getConfig().getString("salvage");

		salvageRef.put("Iron", new HashMap<Integer, Integer>());
		salvageRef.put("Gold", new HashMap<Integer, Integer>());
		salvageRef.put("Diamond", new HashMap<Integer, Integer>());
		String[] salvageArray = salvage.split("; ");
		for (String s: salvageArray) 
		{
			String[] subset = s.split(", ");
			salvageRef.get(subset[1]).put(Integer.parseInt(subset[0]), Integer.parseInt(subset[2]));
		}
		
		/**Frenzy**/
		frenzyenabled = getConfig().getBoolean("frenzy.enabled");
		frenzycd = getConfig().getInt("frenzy.cooldownmultiplier");
		frenzym = getConfig().getInt("frenzy.levelmultiplier");
		frenzyd = getConfig().getInt("frenzy.baseduration");
		
		/**Leveling**/
		xplevel = getConfig().getBoolean("levelingmethods.mcxpgain.enabled");
		xplevelgain = getConfig().getInt("levelingmethods.mcxpgain.xpgain");
		playerkills = getConfig().getBoolean("levelingmethods.playerkills.enabled");
		killergain = getConfig().getInt("levelingmethods.playerkills.xpgain");
		killedloss = getConfig().getInt("levelingmethods.playerkills.xploss");
		mobkills = getConfig().getBoolean("levelingmethods.mobkills.enabled");
		mobkillsxp = getConfig().getInt("levelingmethods.mobkills.xpgain");
		money = getConfig().getBoolean("levelingrewards.money.enabled");
		basemoney = getConfig().getInt("levelingrewards.money.amountperlevel");
		items = getConfig().getBoolean("levelingrewards.items.enabled");
		itemperlevel = getConfig().getInt("levelingrewards.items.amountperlevel");
		itemreward = getConfig().getInt("levelingrewards.items.itemid");
		xpreward = getConfig().getBoolean("levelingrewards.minecraft-xp");
		onlinetime = getConfig().getBoolean("levelingmethods.onlinetime.enabled");
		onlinegain = getConfig().getInt("levelingmethods.onlinetime.xpgain");
		taming = getConfig().getBoolean("levelingmethods.taming.enabled");
		taminggain = getConfig().getInt("levelingmethods.taming.xpgain");
		fishing = getConfig().getBoolean("levelingmethods.fishing.enabled");
		fishinggain = getConfig().getInt("levelingmethods.fishing.xpgain");
		herbalism = getConfig().getBoolean("levelingmethods.herbalism.enabled");
		herbalismgain = getConfig().getInt("levelingmethods.herbalism.xpgain");
		enchanting = getConfig().getBoolean("levelingmethods.enchanting.enabled");
		enchantbase = getConfig().getInt("levelingmethods.enchanting.xpgain");
		
		/**SuperPick**/
		spenabled = getConfig().getBoolean("superpickaxe.enabled");
		spbaseduration = getConfig().getInt("superpickaxe.baseduration");
		superpickcd = getConfig().getInt("superpickaxe.cooldownmultiplier");
		superpickm = getConfig().getInt("superpickaxe.levelmultiplier");
		
		/**Unlimited Ammo**/
		ammoenabled = getConfig().getBoolean("unlimitedammo.enabled");
		ammobaseduration = getConfig().getInt("unlimitedammo.baseduration");
		ammocooldown = getConfig().getInt("unlimitedammo.cooldownmultiplier");
		ammomultiplier = getConfig().getInt("unlimitedammo.levelmultiplier");
		
		/**Miscellaneous**/
		speedboost = getConfig().getBoolean("speedboost.enabled");
		speedboostodds = getConfig().getInt("speedboost.odds");
		speedboostduration = getConfig().getInt("speedboost.duration");
		gracefulroll = getConfig().getBoolean("gracefulroll.enabled");
		gracefulrollodds = getConfig().getInt("gracefulroll.odds");
	}
    
    /**Vault Check**/
	private void setupVault() 
	{
		if (pluginManager.isPluginEnabled("Vault"))
		{
			setupEconomy();
			outConsole(getMessage("log_vault_found"));
		} 
		else 
		{
			outConsole(getMessage("log_vault_notfound"));
		}
	}
	
    /**Set up the Economy**/
    private boolean setupEconomy() 
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) 
		{
			economy = ((Economy)economyProvider.getProvider());
		}
 
		return economy != null;
	}

    /**Update Checker**/
    public double updateCheck(double currentVersion)
    {
        String pluginUrlString = "http://dev.bukkit.org/bukkit-plugins/swornrpg/files.rss";
        try
        {
            URL url = new URL(pluginUrlString);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("item");
            Node firstNode = nodes.item(0);
            if (firstNode.getNodeType() == 1) 
            {
                Element firstElement = (Element)firstNode;
                NodeList firstElementTagName = firstElement.getElementsByTagName("title");
                Element firstNameElement = (Element) firstElementTagName.item(0);
                NodeList firstNodes = firstNameElement.getChildNodes();
                return Double.valueOf(firstNodes.item(0).getNodeValue().replaceAll("[a-zA-Z ]", "").replaceFirst("\\.", ""));
            }
        }
        catch (Exception e) 
        {
        	if (debug) outConsole(Level.SEVERE, getMessage("log_update_error"), e.getMessage());
        }
        
        return currentVersion;
    }
    
    public boolean updateNeeded()
    {
    	return (updateCheck(currentVersion) > currentVersion);
    }
    
    /**Get messages**/
	public String getMessage(String string) 
	{
		try
		{
			return resourceHandler.getMessages().getString(string);
		} 
		catch (MissingResourceException ex) 
		{
			outConsole(Level.WARNING, getMessage("log_message_null"),  string); //messageception :3
			return null;
		}
	}
	
	/**Reload the Configuration**/
	public void reload()
	{
		reloadConfig();
		tagManager.reloadtagsConfig();
		loadConfig();
		updateBlockDrops();
		updateFishDrops();
	}
	
	/**Update Block Drops**/
	@SuppressWarnings("unchecked")
	public void updateBlockDrops() 
	{
		blockDropsMap.clear();
		
		Map<String, ?> map = getConfig().getConfigurationSection("block-drops").getValues(true);
		
		for (Entry<String, ?> entry : map.entrySet()) 
		{
			List<String> values = (List<String>) entry.getValue();
			List<BlockDrop> blockDrops = new ArrayList<BlockDrop>();
			for (String value : values) 
			{
				String[] ss = value.split(":");
				int type = Integer.valueOf(ss[0]);
				short data = 0;
				int chance = 0;
				if (ss.length == 3)
				{
					data = Short.valueOf(ss[1]);
					chance = Integer.valueOf(ss[2]);
				} 
				else 
				{
					chance = Integer.valueOf(ss[1]);
				}
				blockDrops.add(new BlockDrop(new ItemStack(type, 1, data), chance));
			}
			
			blockDropsMap.put(Integer.valueOf(entry.getKey()), blockDrops);
		}
		if (debug) System.out.println("[SwornRPG] Block drops map: " + blockDropsMap.toString());
	}
	
	/**Update Fish Drops**/
	@SuppressWarnings("unchecked")
	public void updateFishDrops() 
	{
		fishDropsMap.clear();
		
		Map<String, ?> map = getConfig().getConfigurationSection("fish-drops").getValues(true);
		
		for (Entry<String, ?> entry : map.entrySet()) 
		{
			List<String> values = (List<String>) entry.getValue();
			List<BlockDrop> blockDrops = new ArrayList<BlockDrop>();
			for (String value : values) 
			{
				String[] ss = value.split(":");
				int type = Integer.valueOf(ss[0]);
				short data = 0;
				int chance = 0;
				if (ss.length == 3)
				{
					data = Short.valueOf(ss[1]);
					chance = Integer.valueOf(ss[2]);
				} 
				else 
				{
					chance = Integer.valueOf(ss[1]);
				}
				blockDrops.add(new BlockDrop(new ItemStack(type, 1, data), chance));
			}
			
			fishDropsMap.put(Integer.valueOf(entry.getKey()), blockDrops);
		}
		if (debug) System.out.println("[SwornRPG] Fish drops map: " + fishDropsMap.toString());
	}
	
	/**Mob Health Tags**/	
	public void updateHealthTag(Entity entity)
	{
		try
		{
			if (entity instanceof Player)
				return;
			
			if (entity instanceof LivingEntity)
			{
				if (healthtags == true)
				{
					LivingEntity lentity = (LivingEntity)entity;
					
					List<EntityType> blockedTypes = Arrays.asList(new EntityType[]{EntityType.VILLAGER, 
							EntityType.ENDER_DRAGON, EntityType.WITHER, EntityType.HORSE});
					
					if (blockedTypes.contains(entity.getType()))
					{
						if (!lentity.getCustomName().isEmpty())
						{
							if (lentity.getCustomName().contains("\u2764"))
							{
								lentity.setCustomNameVisible(false);
								lentity.setCustomName("");
							}
							else
							{
								lentity.setCustomNameVisible(true);
							}
						}
						
						return;
					}
					
					int health = (int) Math.round(lentity.getHealth() / 2);
					int maxhealth = (int)Math.round(lentity.getMaxHealth() / 2);
					int hearts = (int) Math.round((health * 10) / maxhealth);
					
					if (health == maxhealth)
					{
						lentity.setCustomNameVisible(false);
						return;
					}
					
					StringBuilder tag = new StringBuilder();
					for (int i=0; i<hearts; i++)
					{
						tag.append("\u2764");
					}

					String displayName = tag.toString();
					
					/**Determine Color**/
					ChatColor color = null;
					if (hearts >= 8) //health 8, 9, or full
						color = ChatColor.GREEN;
					else if (hearts <= 7 && health > 3) //health 4, 5, 6, or 7
						color = ChatColor.YELLOW;
					else if (hearts <= 3) //health 1, 2, or 3
						color = ChatColor.RED;
					else //health null? (default to yellow, white hearts are ugly)
						color = ChatColor.YELLOW;
					    
					lentity.setCustomNameVisible(true);
					lentity.setCustomName(color + displayName);
				}
			}
		}
		catch (Exception e)
		{
			if (debug) outConsole(getMessage("log_health_error"), e.getMessage());
		}
	}
	
	/**Camping Check**/
	public boolean checkCamper(Player player)
	{
		Location loc = player.getLocation();
		World world = loc.getWorld();
		int RADIUS = campingrad;
		for (int dx = -RADIUS; dx <= RADIUS; dx++) 
		{
			for (int dy = -RADIUS; dy <= RADIUS; dy++) 
			{
				for (int dz = -RADIUS; dz <= RADIUS; dz++) 
				{
					int id = world.getBlockTypeIdAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz);
					if (id == 52)
					{
						if (!isDisabledWorld(player)) 
							player.sendMessage(FormatUtil.format(prefix + getMessage("spawner_camper")));
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**WarZone/SafeZone Check**/
	public boolean checkFactions(Player player, boolean safeZoneCheck)
	{
		if (pluginManager.isPluginEnabled("Factions"))
		{
			Plugin pl = pluginManager.getPlugin("Factions");
			String version = pl.getDescription().getVersion();
			if (version.startsWith("1.6."))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
				return (safeZoneCheck ? (otherFaction.isWarZone() || otherFaction.isSafeZone()) : otherFaction.isWarZone());
			}
		}
		if (pluginManager.isPluginEnabled("SwornNations"))
		{
			Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
			return (safeZoneCheck ? (otherFaction.isWarZone() || otherFaction.isSafeZone()) : otherFaction.isWarZone());
		}
		return false;
	}
	
	/**DisabledWorld Checks**/
	public boolean isDisabledWorld(Player player)
	{
		return isDisabledWorld(player.getWorld());
	}
	
	public boolean isDisabledWorld(Entity entity)
	{
		return isDisabledWorld(entity.getWorld());
	}
	
	public boolean isDisabledWorld(Block block)
	{
		return isDisabledWorld(block.getWorld());
	}
	
	public boolean isDisabledWorld(World world)
	{
		return disabledWorlds.contains(world.getName());
	}
	
	/**Timers and Runnables**/
	public class AutoSaveThread extends BukkitRunnable
	{
		@Override
		public void run() 
		{
			playerDataCache.save();
		}
	}
	
	public class FrenzyCooldownThread extends BukkitRunnable
	{
		@Override
		public void run() 
		{
			for (Player player : getServer().getOnlinePlayers())
			{
				final PlayerData data = playerDataCache.getData(player.getName());
				if (data.isFcooldown())
				{
					data.setFrenzycd(data.getFrenzycd() - 1);
					if (data.getFrenzycd() <= 0)
					{
						data.setFcooldown(false);
						player.sendMessage(FormatUtil.format(prefix + getMessage("ability_refreshed"), "Frenzy"));
					}
				}
			}
		}
	}
	
	public class SuperPickCooldownThread extends BukkitRunnable
	{
		@Override
		public void run() 
		{
			for (Player player : getServer().getOnlinePlayers())
			{
				final PlayerData data = playerDataCache.getData(player.getName());
				if (data.isScooldown())
				{
					data.setSuperpickcd(data.getSuperpickcd() - 1);
					if (data.getSuperpickcd() <= 0)
					{
						data.setScooldown(false);
						player.sendMessage(FormatUtil.format(prefix + getMessage("ability_refreshed"), "Super pickaxe"));
					}
				}
			}
		}
	}
	
	public class AmmoCooldownThread extends BukkitRunnable
	{
		@Override
		public void run() 
		{
			for (Player player : getServer().getOnlinePlayers())
			{
				final PlayerData data = playerDataCache.getData(player.getName());
				if (data.isAmmocooling())
				{
					data.setAmmocd(data.getAmmocd() - 1);
					if (data.getAmmocd() <= 0)
					{
						data.setAmmocooling(false);
						player.sendMessage(FormatUtil.format(prefix + getMessage("ability_refreshed"), "Unlimited ammo"));
					}
				}
			}
		}
	}

	public class UpdateCheckThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			try
			{
				newVersion = updateCheck(currentVersion);
				if (newVersion > currentVersion) 
				{
					outConsole(getMessage("log_update"));
					outConsole(getMessage("log_update_url"), getMessage("update_url"));
				}
			} 
			catch (Exception e) 
			{
				if (debug) outConsole(Level.SEVERE, getMessage("log_update_error"), e.getMessage());
			}
		}
	}
	
	public class OnlineGainThread extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (Player player : getServer().getOnlinePlayers())
			{
				PlayerData data = playerDataCache.getData(player);
				data.setPlayerxp(data.getPlayerxp() + onlinegain);
				
				/**Levelup check**/
				int xp = data.getPlayerxp();
				int xpneeded = data.getXpneeded();
				int newlevel = (xp/xpneeded);
				int oldlevel = data.getLevel();
				
				if ((xp - xpneeded) >= 0)
				{
					/**If so, call levelup event**/
					experienceManager.onLevelup(player, oldlevel, newlevel);
				}
			}
		}
	}
}