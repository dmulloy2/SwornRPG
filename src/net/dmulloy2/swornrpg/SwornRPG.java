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
import org.bukkit.command.CommandSender;
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
  
	public String adminChatPerm = "srpg.adminchat";
	public String adminRidePerm = "srpg.ride";
	public String adminSayPerm = "srpg.asay";
	public String adminClearPerm = "srpg.aclear";
	public String councilChatPerm = "srpg.council";
  
	//What the plugin does when it is disabled
	public void onDisable()
	{
		outConsole(getDescription().getFullName() + " has been disabled");
		this.adminchaters.clear();
		this.councilchaters.clear();
	}

	//What the plugin does when it is enabled
	public void onEnable()
	{
		log = Logger.getLogger("Minecraft");
		outConsole(getDescription().getFullName() + " has been enabled");
    
		//Registers Listener events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		pm.registerEvents(this.entityListener, this);
		pm.registerEvents(this.blockListener, this);

		//Initializes all SwornRPG commands
		this.getCommand("srpg").setExecutor(new CmdSRPG (this));
		this.getCommand("ride").setExecutor(new CmdRide (this));
		this.getCommand("unride").setExecutor(new CmdRide (this));
		this.getCommand("asay").setExecutor(new CmdASay (this));
		this.getCommand("a").setExecutor(new CmdAChat (this));
		this.getCommand("frenzy").setExecutor(new CmdFrenzy (this));
		this.getCommand("hat").setExecutor(new CmdHat (this));
		this.getCommand("hc").setExecutor(new CmdHighCouncil (this));
		this.getCommand("unride").setExecutor(new CmdUnride (this));
		
		//Permissions Messages
		this.getCommand("ride").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		this.getCommand("unride").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		this.getCommand("asay").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		this.getCommand("a").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		this.getCommand("hat").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		this.getCommand("hc").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
		this.getCommand("unride").setPermissionMessage(ChatColor.RED + "You do not have permission to perform this command");
	
		//Initializes the Util class
		Util.Initialize(this);

		//Saves the default config if one does not exist
		this.saveDefaultConfig();
    
		//Copys defaults if they do not exist
		this.getConfig().options().copyDefaults(true);
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
			if (PermissionInterface.checkPermission(p, this.adminChatPerm))
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
			if (PermissionInterface.checkPermission(p, this.councilChatPerm))
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
	public void displayHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.DARK_RED + "====== " + ChatColor.GOLD + getDescription().getFullName() + ChatColor.DARK_RED + " ======");
		p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " <args> ");
		if (p.hasPermission("srpg.admin"))
		{
			p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " reload " + ChatColor.YELLOW + "Reloads the config");
		}
		p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
		//p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " level " + ChatColor.YELLOW + "Displays your current level");
		//if (p.hasPermission("srpg.clear"))
		//{
			//p.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " levelr <name> " + ChatColor.YELLOW + "Resets a player's level.");
		//}
		//p.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters Frenzy mode.");
		if (p.hasPermission("srpg.ride"))
		{
			p.sendMessage(ChatColor.RED + "/ride" + ChatColor.DARK_RED + " <player> " + ChatColor.YELLOW + "Ride another player");
			p.sendMessage(ChatColor.RED + "/unride" + ChatColor.YELLOW + " Stop riding another player");
		}
		p.sendMessage(ChatColor.RED + "/hat" + ChatColor.GOLD + " [remove] " + ChatColor.YELLOW + "Get a new hat!");
		if (p.hasPermission("srpg.adminchat"))
		{
			p.sendMessage(ChatColor.RED + "/a" + ChatColor.DARK_RED + " <message> "+ ChatColor.YELLOW + "Talk in admin chat");
		}
		if (p.hasPermission("srpg.council"))
		{
			p.sendMessage(ChatColor.RED + "/hc" + ChatColor.DARK_RED + " <message> " + ChatColor.YELLOW + "Talk in council chat");
		}
		if (p.hasPermission("srpg.asay"))
		{
			p.sendMessage(ChatColor.RED + "/asay" + ChatColor.DARK_RED + " <message> " + ChatColor.YELLOW + "Alternate admin say command");
		}
	}
  
	//Console logging
	public static void outConsole(String s)
	{
		log.log(Level.INFO, "[SwornRPG] " + s);
	}
}
