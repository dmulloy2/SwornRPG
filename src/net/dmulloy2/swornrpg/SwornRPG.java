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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.Getter;

//Plugin imports
import net.dmulloy2.swornrpg.commands.*;
import net.dmulloy2.swornrpg.listeners.*;
import net.dmulloy2.swornrpg.util.*;
import net.dmulloy2.swornrpg.data.*;
import net.dmulloy2.swornrpg.permissions.*;
import net.milkbowl.vault.economy.Economy;

//Bukkit imports
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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
	
	//Private objects
	private static Logger log;
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

    //Configuration/Update Checking
	public boolean irondoorprotect, randomdrops, axekb, arrowfire, deathbook,
	frenzyenabled, onlinetime, playerkills, mobkills, xpreward, items, xplevel,
	money, update;
	public int frenzyduration, basemoney, itemperlevel, itemreward, xplevelgain,
	killergain, killedloss, mobkillsxp;
	private double newVersion;
    private double currentVersion;


	//Permission Strings
	public String adminChatPerm = "srpg.adminchat";
	public String adminRidePerm = "srpg.ride";
	public String adminSayPerm = "srpg.asay";
	public String adminResetPerm = "srpg.levelr";
	public String councilChatPerm = "srpg.council";
	public String adminReloadPerm = "srpg.reload";
	public String hatPerm = "srpg.hat";
	public String matchPerm = "srpg.match";
	public String tagPerm = "srpg.tag";
	public String tagresetPerm = "srpg.tagr";
	public String adminItemPerm = "srpg.iname";
	public String adminMatchPerm = "srpg.match";
	
	//General command strings
	public String prefix = ChatColor.GOLD + "[SwornRPG] ";
	public String invalidargs = prefix + ChatColor.RED + "Invalid arguments count ";
	public String mustbeplayer = prefix + ChatColor.RED + "You must be a player to use this command";
	public String noperm = ChatColor.RED + "You do not have permission to perform this command";
	public String noplayer = prefix + ChatColor.RED + "Error, player not found";

	//What the plugin does when it is disabled
	public void onDisable()
	{
		outConsole(getDescription().getFullName() + " has been disabled");

		playerDataCache.save();
		
		getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
	}

	//What the plugin does when it is enabled
	public void onEnable()
	{
		//Console logging
		log = Logger.getLogger("Minecraft");
		outConsole(getDescription().getFullName() + " has been enabled");
		currentVersion = Double.valueOf(getDescription().getVersion().replaceFirst("\\.", ""));
    
		//Registers Listener events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.blockListener, this);
		pm.registerEvents(this.experienceListener, this);
		
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);
		
		//Check for TagAPI
		if (pm.isPluginEnabled("TagAPI"))
		{
			//If found, enable Tags
			pm.registerEvents(this.tagListener, this);
			outConsole("TagAPI found, enabling all Tag related features");
		}
		else
		{
			//If not, give link for TagAPI
			outConsole("TagAPI not found, disabling all Tag related features");
			outConsole("Get it here: http://dev.bukkit.org/server-mods/tag/");
		}
		
		//Initializes all SwornRPG commands
		commandHandler.registerCommand(new CmdAChat (this));
		commandHandler.registerCommand(new CmdAddxp (this));
		commandHandler.registerCommand(new CmdASay (this));
		commandHandler.registerCommand(new CmdBookToggle (this));
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
//		commandHandler.registerCommand(new CmdMine (this));
		commandHandler.registerCommand(new CmdPropose (this));
		commandHandler.registerCommand(new CmdRide (this));
		commandHandler.registerCommand(new CmdSpouse (this));
		commandHandler.registerCommand(new CmdStandup (this));
		commandHandler.registerCommand(new CmdTag (this));
		commandHandler.registerCommand(new CmdTagr (this));
		commandHandler.registerCommand(new CmdUnride (this));
		
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
		
		//Configuration
		loadConfig();
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		savetagsConfig();
		
		//Check for vault
		checkVault(pm);
		
		//Makes sure files exist
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		//Schedules player data cache saving
		playerDataCache = new PlayerDataCache(this);
		getServer().getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() {
			
			public void run() {
				playerDataCache.save();
			}
			
		}, 12000L, 12000L);
		
		//Initialize Tags
		if (pm.getPlugin("TagAPI") != null)
		{
			for (final Player player : this.getServer().getOnlinePlayers()) 
			{
				final String oldName = player.getName();
				final String newName = this.getDefinedName(oldName);
				if (!newName.equals(oldName)) 
				{
					try 
					{
						this.addTagChange(oldName, newName);
					} 
					catch (final TooBigException e) 
					{
						this.getLogger().severe("Error while changing name from memory:");
						this.getLogger().severe(e.getMessage());
					}
					TagAPI.refreshPlayer(player);
				}
			}
		}
		
		if (update)
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
							log.info("[SwornRPG] A new version of SwornRPG is now available!");
							log.info("[SwornRPG] Update SwornRPG at: http://dev.bukkit.org/server-mods/swornrpg/");
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
		irondoorprotect = getConfig().getBoolean("irondoorprotect");
		randomdrops = getConfig().getBoolean("randomdrops");
		axekb = getConfig().getBoolean("axekb");
		arrowfire = getConfig().getBoolean("arrowfire");
		deathbook = getConfig().getBoolean("deathbook");
		frenzyenabled = getConfig().getBoolean("frenzy.enabled");
		onlinetime = getConfig().getBoolean("levelingmethods.onlinetime.enabled");
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
		frenzyduration = getConfig().getInt("frenzy.baseduration");
		update = getConfig().getBoolean("updatechecker");
		salvage = getConfig().getString("salvage");
		
		salvageRef.put("Iron", new HashMap<Integer, Integer>());
		salvageRef.put("Gold", new HashMap<Integer, Integer>());
		salvageRef.put("Diamond", new HashMap<Integer, Integer>());
		String[] salvageArray = salvage.split("; ");
		for (String s: salvageArray) {
			String[] subset = s.split(", ");
			salvageRef.get(subset[1]).put(Integer.getInteger(subset[0]), Integer.getInteger(subset[2]));
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
		Plugin p = pm.getPlugin("Vault");
		if (p != null) 
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
}
