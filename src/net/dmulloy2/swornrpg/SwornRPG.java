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

//Java Imports
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.Getter;

//Plugin imports
import net.dmulloy2.swornrpg.commands.*;
import net.dmulloy2.swornrpg.handlers.*;
import net.dmulloy2.swornrpg.listeners.*;
import net.dmulloy2.swornrpg.util.*;
import net.dmulloy2.swornrpg.data.*;
import net.milkbowl.vault.economy.Economy;

//Bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.kitteh.tag.TagAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author dmulloy2
 */

public class SwornRPG extends JavaPlugin
{
	//Getters
	private @Getter PlayerDataCache playerDataCache;
	private @Getter Economy economy;
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter ResourceHandler resourceHandler;
	
	//Private objects
	public static Logger log;
    private FileConfiguration tagsConfig = null;
    private File tagsConfigFile = null;
	private EntityListener entityListener = new EntityListener(this);
	private PlayerListener playerListener = new PlayerListener(this);
	private BlockListener blockListener = new BlockListener(this);
	private TagListener tagListener = new TagListener(this);
	private ExperienceListener experienceListener = new ExperienceListener(this);

	//Hash maps
    private HashMap<String, String> tagChanges;
    public HashMap<String, String> proposal = new HashMap<String, String>();
	public HashMap<String, HashMap<Integer, Integer>> salvageRef = new HashMap<String, HashMap<Integer, Integer>>();
    public Map<Integer, List<BlockDrop>> blockDropsMap = new HashMap<Integer, List<BlockDrop>>();
    
    //Configuration/Update Checking
	public boolean irondoorprotect, randomdrops, axekb, arrowfire, deathbook,
	frenzyenabled, onlinetime, playerkills, mobkills, xpreward, items, xplevel,
	money, update, spenabled, debug, salvaging, ammoenabled;
	public int frenzyd, basemoney, itemperlevel, itemreward, xplevelgain,
	killergain, killedloss, mobkillsxp, spbaseduration, frenzycd, frenzym, 
	superpickcd, superpickm, ammobaseduration, ammocooldown, ammomultiplier,
	campingrad;
	private double newVersion;
    private double currentVersion;
    public String salvage;
	
	public String prefix = ChatColor.GOLD + "[SwornRPG] ";
	public String noperm = ChatColor.RED + "Error, you do not have permission to perform this command";
	
	//What the plugin does when it is disabled
	public void onDisable()
	{
		outConsole(getDescription().getFullName() + " has been disabled");

		playerDataCache.save();
		
		getServer().getServicesManager().unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
	}

	//What the plugin does when it is enabled
	public void onEnable()
	{
		//Console logging
		log = Logger.getLogger("Minecraft");
		outConsole(getDescription().getFullName() + " has been enabled");
		
		//Version checker stuff
		currentVersion = Double.valueOf(getDescription().getVersion().replaceFirst("\\.", ""));
		
		//Configuration
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists())
        	saveDefaultConfig();
        
		loadConfig();
		reloadConfig();
		saveResource("messages.properties", true);
		
		updateBlockDrops();
    
		//Register Listener events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.experienceListener, this);
		
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);
		resourceHandler = new ResourceHandler(this, this.getClassLoader());
		
		//Check for TagAPI
		if (pm.isPluginEnabled("TagAPI"))
		{
			//If found, enable Tags
			pm.registerEvents(this.tagListener, this);
			outConsole("TagAPI found, enabling all Tag related features");
			for (Player player : this.getServer().getOnlinePlayers()) 
			{
				String oldName = player.getName();
				String newName = this.getDefinedName(oldName);
				if (!newName.equals(oldName)) 
				{
					try 
					{
						this.addTagChange(oldName, newName);
					} 
					catch (TooBigException e) 
					{
						this.getLogger().severe("Error while changing name from memory:");
						this.getLogger().severe(e.getMessage());
					}
					TagAPI.refreshPlayer(player);
				}
			}
		}
		else
		{
			outConsole("TagAPI not found, disabling all Tag related features");
		}
		
		/**Register Prefixed Commands**/
		commandHandler.setCommandPrefix("srpg");
		commandHandler.registerPrefixedCommand(new CmdLeaderboard (this));
		
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
		commandHandler.registerCommand(new CmdHelp (this));
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
		
		//Set permission messages
		getCommand("ride").setPermissionMessage(noperm);
		getCommand("unride").setPermissionMessage(noperm);
		getCommand("asay").setPermissionMessage(noperm);
		getCommand("a").setPermissionMessage(noperm);
		getCommand("hat").setPermissionMessage(noperm);
		getCommand("hc").setPermissionMessage(noperm);
		getCommand("unride").setPermissionMessage(noperm);
		getCommand("eject").setPermissionMessage(noperm);
		getCommand("match").setPermissionMessage(noperm);
		getCommand("tag").setPermissionMessage(noperm);
		getCommand("tagr").setPermissionMessage(noperm);
		getCommand("levelr").setPermissionMessage(noperm);
		getCommand("itemname").setPermissionMessage(noperm);
		getCommand("addxp").setPermissionMessage(noperm);

		
		//Initializes the Util class
		Util.Initialize(this);
	
		//Check for vault
		checkVault(pm);

		//Schedules player data cache saving
		playerDataCache = new PlayerDataCache(this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() 
		{	
			public void run() 
			{
				playerDataCache.save();
			}
		}, 12000L, 12000L);
		
		//Frenzy cooldown
		if (frenzyenabled == true)
		{
			getServer().getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() 
			{
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
			},0, 20);
		}
		
		//Superpick cooldown
		if (spenabled == true)
		{
			getServer().getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() 
			{
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
			},0, 20);
		}
		
		//Ammo cooldown
		if (pm.isPluginEnabled("PVPGunPlus"))
		{
			outConsole("PVPGunPlus found, enabling gun-related features");
			if (ammoenabled == true)
			{
				getServer().getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() 
				{
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
				},0, 20);
			}
		}
		else
		{
			outConsole("PVPGunPlus not found, disabling all gun-related features");
		}
		
		if (update == true)
		{
			//Update Checker
			this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() 
			{
				@Override
				public void run()
				{
					try
					{
						newVersion = updateCheck(currentVersion);
						if (newVersion > currentVersion) 
						{
							outConsole("A new version of SwornRPG is now available!");
							outConsole("Update SwornRPG at: http://dev.bukkit.org/server-mods/swornrpg/");
						}
					} 
					catch (Exception e) 
					{
					}
				}
				
			}, 0, 432000);
		}
	}
	
	//What the plugin does upon loading
	public void onLoad()
	{
		this.tagChanges = new HashMap<String, String>();
	}
	    
	//Console logging
	public void outConsole(String s)
	{
		log.log(Level.INFO, "[SwornRPG] " + s);
	}
	
	//Loads the configuration
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
	}
	
	//Tags Stuff
    public void addTagChange(final String oldName, final String newName)
    {
        this.tagChanges.put(oldName, newName);
        this.gettagsConfig().set("tags." + oldName, newName);
        this.savetagsConfig();
        final Player player = this.getServer().getPlayerExact(oldName);
        if (player != null) 
        {
            TagAPI.refreshPlayer(player);
        }
    }
    
    public void removeTagChange(final String oldName) 
    {
        this.tagChanges.remove(oldName);
        this.gettagsConfig().set("tags." + oldName, null);
        this.savetagsConfig();
        final Player player = this.getServer().getPlayerExact(oldName);
        if (player != null) 
        {
            TagAPI.refreshPlayer(player);
        }
    }
    
    public boolean hasChanged(final String name) 
    {
        return tagChanges.containsKey(name);
    }
    
    public String getName(final String name) 
    {
        return tagChanges.get(name);
    }
    
    public String getDefinedName(final String oldName)
    {
        final String newName = this.gettagsConfig().getString("tags." + oldName);
        return newName == null ? oldName : newName;
    }
    
    //Tags Configuration
    public void reloadtagsConfig() 
    {
        if (tagsConfigFile == null) 
        {
        	tagsConfigFile = new File(getDataFolder(), "tags.yml");
        }
        tagsConfig = YamlConfiguration.loadConfiguration(tagsConfigFile);
     
        // Look for defaults in the jar
        InputStream defConfigStream = this.getResource("tags.yml");
        if (defConfigStream != null) 
        {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            tagsConfig.setDefaults(defConfig);
        }
    }
    
    public FileConfiguration gettagsConfig() 
    {
        if (tagsConfig == null) 
        {
            this.reloadtagsConfig();
        }
        return tagsConfig;
    }
    
    public void savetagsConfig() 
    {
        if (tagsConfig == null || tagsConfigFile == null) 
        {
        	return;
        }
        try 
        {
        	gettagsConfig().save(tagsConfigFile);
        } 
        catch (IOException ex) 
        {
        	this.getLogger().log(Level.SEVERE, "Could not save config to " + tagsConfigFile, ex);
        }
    }
    
    //Checks for vault, for money rewards
	private void checkVault(PluginManager pm) 
	{
		if (pm.isPluginEnabled("Vault"))
		{
			setupEconomy();
			outConsole("Vault found, enabling money related features");
		} 
		else 
		{
			outConsole("Vault not found. Vault is required for money rewards");
			outConsole("Disabling all money related fetures");
		}
	}
	
    //Set up vault economy
    private boolean setupEconomy() 
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) 
		{
			economy = ((Economy)economyProvider.getProvider());
		}
 
		return economy != null;
	}

    //Update checker
    public double updateCheck(double currentVersion) throws Exception 
    {
        String pluginUrlString = "http://dev.bukkit.org/server-mods/swornrpg/files.rss";
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
        catch (Exception localException) 
        {
        	if (debug) localException.printStackTrace();
        }
        
        return currentVersion;
    }
    
    public boolean updateNeeded()
    {
    	if (newVersion > currentVersion)
    		return true;
    	else
    		return false;
    }
    
    //Get messages
	public String getMessage(String string) 
	{
		try
		{
			return resourceHandler.getMessages().getString(string);
		} 
		catch (MissingResourceException ex) 
		{
			outConsole("WARNING: Messages locale is missing key for: " + string);
			return null;
		}
	}
	
	//Reload
	public void reload()
	{
		reloadConfig();
		reloadtagsConfig();
		loadConfig();
	}
	
	@SuppressWarnings("unchecked")
	public void updateBlockDrops() 
	{
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
		if (debug) System.out.println(blockDropsMap.toString());
	}
}