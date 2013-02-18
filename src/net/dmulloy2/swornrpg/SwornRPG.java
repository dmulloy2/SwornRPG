package net.dmulloy2.swornrpg;

//Java Imports
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Plugin imports
import net.dmulloy2.swornrpg.commands.CmdAChat;
import net.dmulloy2.swornrpg.commands.CmdHat;
import net.dmulloy2.swornrpg.commands.CmdFrenzy;
import net.dmulloy2.swornrpg.commands.CmdRide;
import net.dmulloy2.swornrpg.commands.CmdASay;
import net.dmulloy2.swornrpg.commands.CmdSRPG;
import net.dmulloy2.swornrpg.commands.CmdHighCouncil;
import net.dmulloy2.swornrpg.commands.CmdUnride;
import net.dmulloy2.swornrpg.listeners.BlockListener;
import net.dmulloy2.swornrpg.listeners.EntityListener;
import net.dmulloy2.swornrpg.listeners.PlayerListener;
import net.dmulloy2.swornrpg.util.Util;

//Bukkit imports
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

	public List<String> adminchaters = new ArrayList<String>();
	public List<String> councilchaters = new ArrayList<String>();
	public List<Material> allowedBlocks = new ArrayList<Material>();

	public boolean irondoorprotect;
	public boolean randomdrops;
	public boolean axekb;
	public boolean arrowfire;

	public String adminChatPerm = "srpg.adminchat";
	public String adminRidePerm = "srpg.ride";
	public String adminSayPerm = "srpg.asay";
	public String adminClearPerm = "srpg.aclear";
	public String councilChatPerm = "srpg.council";
	public String adminReloadPerm = "srpg.reload";

  
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
		
		//Permissions Messages
		getCommand("ride").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		getCommand("unride").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		getCommand("asay").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		getCommand("a").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		getCommand("hat").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		getCommand("hc").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		getCommand("unride").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
	
		//Initializes the Util class
		Util.Initialize(this);

		//Saves the default config if one does not exist
		saveDefaultConfig();
		    
		//Copys defaults if they do not exist
		getConfig().options().copyDefaults(true);
		
		//Loads the config file
		loadConfig();
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
  
	//Help menu
	public void displayHelp(Player p)
	{
		p.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + getDescription().getFullName() + ChatColor.DARK_RED + " ======");
		p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " <args> ");
		if (PermissionInterface.checkPermission(p, adminReloadPerm))
		{
			p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " reload " + ChatColor.YELLOW + "Reloads the config");
		}
		p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
		//p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " level " + ChatColor.YELLOW + "Displays your current level");
		//if (PermissionInterface.checkPermission(p, adminResetPerm))
		//{
			//p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " levelr <name> " + ChatColor.YELLOW + "Resets a player's level.");
		//}
		//p.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters Frenzy mode.");
		if (PermissionInterface.checkPermission(p, adminRidePerm))
		{
			p.sendMessage(ChatColor.RED + "/ride" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Ride another player");
			p.sendMessage(ChatColor.RED + "/unride" + ChatColor.YELLOW + " Stop riding another player");
		}
		p.sendMessage(ChatColor.RED + "/hat" + ChatColor.GOLD + " [remove] " + ChatColor.YELLOW + "Get a new hat!");
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
	}
  
	//Console logging
	public static void outConsole(String s)
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
	}
}