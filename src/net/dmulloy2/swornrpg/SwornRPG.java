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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Plugin imports
import net.dmulloy2.swornrpg.commands.*;
import net.dmulloy2.swornrpg.listeners.*;
import net.dmulloy2.swornrpg.util.TooBigException;
import net.dmulloy2.swornrpg.util.Util;
import net.dmulloy2.swornrpg.util.VersionChecker;

//Bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.tag.TagAPI;

/**
 * @author dmulloy2
 */

public class SwornRPG extends JavaPlugin
{
	//Define some stuff
	private static Logger log;
	private EntityListener entityListener = new EntityListener(this);
	private PlayerListener playerListener = new PlayerListener(this);
	private BlockListener blockListener = new BlockListener(this);
	private TagListener tagListener = new TagListener(this);

	public List<String> adminchaters = new ArrayList<String>();
	public List<String> councilchaters = new ArrayList<String>();
    private HashMap<String, String> tagChanges;
	
	VersionChecker vc = new VersionChecker(this);
	PluginDescriptionFile pdfFile;
	
    private FileConfiguration tagsConfig = null;
    private File tagsConfigFile = null;

	public boolean irondoorprotect, randomdrops, axekb, arrowfire;
//	public boolean frenzyenabled, onlinetime, mining, items, mcxp;
//	public boolean playerkills, mobkills, money;
//	public int frenzyduration;

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
	
	//General command strings
	public String prefix = ChatColor.GOLD + "[SwornRPG] ";
	public String invalidargs = prefix + ChatColor.RED + "Invalid arguments count ";
	public String mustbeplayer = prefix + ChatColor.RED + " You must be a player to use this command";
	public String noperm = ChatColor.RED + "You do not have permission to perform this command";

	//What the plugin does when it is disabled
	public void onDisable()
	{
		outConsole(getDescription().getFullName() + " has been disabled");
		adminchaters.clear();
		councilchaters.clear();
	}

	//What the plugin does when it is enabled
	public void onEnable()
	{
		//Console logging
		log = Logger.getLogger("Minecraft");
		outConsole(getDescription().getFullName() + " has been enabled");
    
		//Registers Listener events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.blockListener, this);
		//Check for TagAPI
		if (pm.getPlugin("TagAPI") != null)
		{
			//If found, enable Tags
			pm.registerEvents(this.tagListener, this);
			outConsole("TagAPI found. Enabling all Tag related features");
		}
		else
		{
			//If not, give link for TagAPI
			outConsole("TagAPI not found. Disabling all Tag related features");
			outConsole("http://dev.bukkit.org/server-mods/tag/");
		}

		//Initializes all SwornRPG commands
		getCommand("srpg").setExecutor(new CmdSRPG (this));
		getCommand("ride").setExecutor(new CmdRide (this));
		getCommand("unride").setExecutor(new CmdRide (this));
		getCommand("asay").setExecutor(new CmdASay (this));
		getCommand("a").setExecutor(new CmdAChat (this));
		getCommand("frenzy").setExecutor(new CmdFrenzy (this));
		getCommand("hat").setExecutor(new CmdHat (this));
		getCommand("hc").setExecutor(new CmdHighCouncil (this));
		getCommand("unride").setExecutor(new CmdUnride (this));
		getCommand("eject").setExecutor(new CmdEject (this));
		getCommand("match").setExecutor(new CmdMatch (this));
		getCommand("tag").setExecutor(new CmdTag (this));
		getCommand("removetag").setExecutor(new CmdResetTag (this));
		getCommand("level").setExecutor(new CmdLevel (this));
		getCommand("levelr").setExecutor(new CmdLevelr (this));

		
		//Permissions Messages
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
		getCommand("removetag").setPermissionMessage(noperm);
		getCommand("levelr").setPermissionMessage(noperm);
		
		//Initializes the Util class
		Util.Initialize(this);
		
		//Loads configurations
		loadConfigs();
		
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
	}
	
	//What the plugin does upon loading
	public void onLoad()
	{
		this.tagChanges = new HashMap<String, String>();
	}
	
	//Load configurations
	public void loadConfigs()
	{
		//Regular config
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		loadConfig();
		
		//Version checker
		pdfFile = getDescription();
		vc.versionChecker(pdfFile);
		
		//Tags file
		savetagsConfig();
	}

	//Players who are admin chatting
	public boolean isAdminChatting(String str)
	{
		for (int i = 0; i < this.adminchaters.size(); i++) 
		{
			if (((String)this.adminchaters.get(i)).equals(str)) 
			{
				return true;
			}
		}	
		return false;
	}
  
	//Players who are council chatting
	public boolean isCouncilChatting(String str)
	{
		for (int i = 0; i < this.councilchaters.size(); i++)
		{	
			if (((String)this.councilchaters.get(i)).equals(str)) 
			{
				return true;
			}
		}
		return false;
	}
  
	public void playEffect(Effect e, Location l, int num) 
	{
		for (int i = 0; i < getServer().getOnlinePlayers().length; i++)
			getServer().getOnlinePlayers()[i].playEffect(l, e, num);
	}

	//Sends a message to all players with the admin chat perm
	public void sendAdminMessage(String str, String str2)
	{
		List<Player> arr = Util.Who();
		for (int i = 0; i < arr.size(); i++)
		{
			Player p = (Player)arr.get(i);
			if (PermissionInterface.checkPermission(p, adminChatPerm))
			{
				p.sendMessage(ChatColor.GRAY + str + ": " + ChatColor.AQUA + str2);
			}
		}	
	}

	//Sends a message to all players with the council chat perm
	public void sendCouncilMessage(String str, String str2)
	{
		List<Player> arr = Util.Who();
		for (int i = 0; i < arr.size(); i++) 
		{
			Player p = (Player)arr.get(i);
			if (PermissionInterface.checkPermission(p, councilChatPerm))
			{
				p.sendMessage(ChatColor.GOLD + str + ": " + ChatColor.RED + str2);
			}
		}	
	}

	//Sends a message to all players on the server
	public void sendMessageAll(String str)
	{
		List<Player> arr = Util.Who();
		for (int i = 0; i < arr.size(); i++) 
		{
			Player p = (Player)arr.get(i);
			p.sendMessage(str);
		}
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
//		frenzyenabled = getConfig().getBoolean("frenzy.enabled");
//		onlinetime = getConfig().getBoolean("leveling-methods.onlinetime");
//		mining = getConfig().getBoolean("leveling-methods.mining");
//		playerkills = getConfig().getBoolean("leveling-methods.playerkills");
//		mobkills = getConfig().getBoolean("leveling-methods.mobkills");
//		money = getConfig().getBoolean("levelingrewards.money");
//		items = getConfig().getBoolean("levelingrewards.items");
//		mcxp = getConfig().getBoolean("levelingrewards.minecraft-xp");
//		frenzyduration = getConfig().getInt("frenzy.baseduration");
	}
	
	//Add Tag Change
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
    
    //Remove Tag Change
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
    
	//Help menu
	public void displayHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + getDescription().getFullName() + ChatColor.DARK_RED + " ======");
		p.sendMessage(ChatColor.RED + "/<command>" + ChatColor.DARK_RED + " <required> " + ChatColor.GOLD + "[optional]");
		if (PermissionInterface.checkPermission(p, adminReloadPerm))
		{
			p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " reload " + ChatColor.YELLOW + "Reloads the config");
		}
		p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
		p.sendMessage(ChatColor.RED + "/level " + ChatColor.GOLD + "[name] " + ChatColor.YELLOW + "Displays your current level");
		if (PermissionInterface.checkPermission(p, adminResetPerm))
		{
			p.sendMessage(ChatColor.RED + "/levelr " + ChatColor.GOLD + "[name] " + ChatColor.YELLOW + "Resets a player's level.");
		}
		p.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters Frenzy mode.");
		if (PermissionInterface.checkPermission(p, adminRidePerm))
		{
			p.sendMessage(ChatColor.RED + "/ride" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Ride another player");
			p.sendMessage(ChatColor.RED + "/unride" + ChatColor.YELLOW + " Stop riding another player");
			p.sendMessage(ChatColor.RED + "/eject" + ChatColor.YELLOW + " Kick someone off your head");
		}
		if (PermissionInterface.checkPermission(p, hatPerm))
		{
			p.sendMessage(ChatColor.RED + "/hat" + ChatColor.GOLD + " [remove] " + ChatColor.YELLOW + "Get a new hat!");
		}
		if (PermissionInterface.checkPermission(p, adminChatPerm))
		{
			p.sendMessage(ChatColor.RED + "/a" + ChatColor.DARK_RED + " <message> "+ ChatColor.YELLOW + "Talk in admin chat");
		}
		if (PermissionInterface.checkPermission(p, councilChatPerm))
		{
			p.sendMessage(ChatColor.RED + "/hc" + ChatColor.DARK_RED + " <message> " + ChatColor.YELLOW + "Talk in council chat");
		}
		if (PermissionInterface.checkPermission(p, adminSayPerm))
		{
			p.sendMessage(ChatColor.RED + "/asay" + ChatColor.DARK_RED + " <message> " + ChatColor.YELLOW + "Alternate admin say command");
		}
		if (PermissionInterface.checkPermission(p, matchPerm))
		{
			p.sendMessage(ChatColor.RED + "/match" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Match online and offline players");
		}
		if (PermissionInterface.checkPermission(p, tagPerm))
		{
			p.sendMessage(ChatColor.RED + "/tag" + ChatColor.GOLD + " [player] " + ChatColor.DARK_RED + "<tag> " + ChatColor.YELLOW + "Change the name above your head");
		}
		if (PermissionInterface.checkPermission(p, tagresetPerm))
		{
			p.sendMessage(ChatColor.RED + "/tagr" + ChatColor.GOLD + " [player] " + ChatColor.YELLOW + "Resets a player's tag");
		}
	}
}