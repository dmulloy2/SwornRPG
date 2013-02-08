package net.dmulloy2.swornrpg;

import java.util.ArrayList;
import java.util.List;
import net.dmulloy2.swornrpg.commands.CmdAChat;
import net.dmulloy2.swornrpg.commands.CmdHat;
import net.dmulloy2.swornrpg.commands.CmdFrenzy;
import net.dmulloy2.swornrpg.commands.CmdRide;
import net.dmulloy2.swornrpg.commands.CmdASay;
import net.dmulloy2.swornrpg.commands.CmdSRPG;
import net.dmulloy2.swornrpg.commands.CmdHighCouncil;
import net.dmulloy2.swornrpg.listeners.BlockListener;
import net.dmulloy2.swornrpg.listeners.EntityListener;
import net.dmulloy2.swornrpg.listeners.PlayerListener;
import net.dmulloy2.swornrpg.listeners.TagListener;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author dmulloy2
 */

public class SwornRPG extends JavaPlugin
{
  private EntityListener entityListener = new EntityListener(this);
  private PlayerListener playerListener = new PlayerListener(this);
  private BlockListener blockListener = new BlockListener(this);
  private TagListener tagListener = new TagListener(this);

  public List<String> adminchaters = new ArrayList<String>();
  public List<String> councilchaters = new ArrayList<String>();
  
  private String pluginName = "SwornRPG";

  public String adminChatPerm = "srpg.adminchat";
  public String adminAbilitiesPerm = "srpg.abilities";
  public String adminRidePerm = "srpg.ride";
  public String adminSayPerm = "srpg.asay";
  public String adminClearPerm = "srpg.aclear";
  public String councilChatPerm = "srpg.council";
  
  public void onDisable()
  {
    System.out.println("[SwornRPG] " + this.pluginName + " v " + getDescription().getVersion() + " has been disabled");
    this.adminchaters.clear();
    this.councilchaters.clear();
  }

  public void onEnable()
  {
    System.out.println("[SwornRPG] " + this.pluginName + " v " + getDescription().getVersion() + " has been enabled");
    
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this.playerListener, this);
    pm.registerEvents(this.entityListener, this);
    pm.registerEvents(this.blockListener, this);
    pm.registerEvents(this.tagListener, this);

	this.getCommand("srpg").setExecutor(new CmdSRPG (this));
	this.getCommand("ride").setExecutor(new CmdRide (this));
	this.getCommand("unride").setExecutor(new CmdRide (this));
	this.getCommand("asay").setExecutor(new CmdASay (this));
	this.getCommand("a").setExecutor(new CmdAChat (this));
	this.getCommand("frenzy").setExecutor(new CmdFrenzy (this));
	this.getCommand("hat").setExecutor(new CmdHat (this));
	this.getCommand("hc").setExecutor(new CmdHighCouncil (this));
	
	Util.Initialize(this);
    Plugin p = Bukkit.getPluginManager().getPlugin("TagAPI");
    if (p != null) {
    	System.out.println("[SwornRPG] Hooked into TagAPI. Enabling all TagAPI related features.");
    	} else {
    		System.out.println("[SwornRPG] Could not hook into TagAPI. Disabling TagAPI related features.");
    	}
    //Saves the default config if one does not exist
    this.saveDefaultConfig();
  }

  public boolean isAdminChatting(String str)
  {
    for (int i = 0; i < this.adminchaters.size(); i++) {
      if (((String)this.adminchaters.get(i)).equals(str)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isCouncilChatting(String str)
  {
    for (int i = 0; i < this.councilchaters.size(); i++) {
      if (((String)this.councilchaters.get(i)).equals(str)) {
        return true;
      }
    }
    return false;
  }
  
  public void playEffect(Effect e, Location l, int num) {
    for (int i = 0; i < getServer().getOnlinePlayers().length; i++)
      getServer().getOnlinePlayers()[i].playEffect(l, e, num);
  }

  //Sends a message to all players with the admin chat perm
  public void sendAdminMessage(String str, String str2)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      if (PermissionInterface.checkPermission(p, this.adminChatPerm))
        p.sendMessage(ChatColor.GRAY + str + ": " + ChatColor.AQUA + str2);
    }
  }
  
  //Sends a message to all players with the council chat perm
  public void sendCouncilMessage(String str, String str2)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      if (PermissionInterface.checkPermission(p, this.councilChatPerm))
        p.sendMessage(ChatColor.GOLD + str + ": " + ChatColor.RED + str2);
    }
  }

  //Sends a message to all players on the server
  public void sendMessageAll(String str)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      p.sendMessage(str);
    }
  }
  public void displayHelp(CommandSender player){
	  player.sendMessage(ChatColor.DARK_RED + "======" + ChatColor.GOLD + " SwornRPG " + ChatColor.DARK_RED + "======");
	  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " <args> ");
	  if (player.hasPermission("srpg.admin")){
		  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " resetconfig " + ChatColor.YELLOW + "Resets your config file");
		  player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " reload " + ChatColor.YELLOW + "Reloads the config");}
	  	player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " help " + ChatColor.YELLOW + "Displays this help menu");
	  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " level " + ChatColor.YELLOW + "Displays your current level");
	  //if (PermissionInterface.checkPermission(player, this.plugin.adminClearPerm)){
		  //player.sendMessage(ChatColor.RED + "/srpg" + ChatColor.DARK_RED + " levelr <name> " + ChatColor.YELLOW + "Resets a player's level.");}
	  //player.sendMessage(ChatColor.RED + "/frenzy" + ChatColor.YELLOW + " Enters beast mode");
	  if (player.hasPermission("srpg.ride")){
		  player.sendMessage(ChatColor.RED + "/ride" + ChatColor.GOLD + " (unride) " + ChatColor.YELLOW + "Ride another player");}
	  player.sendMessage(ChatColor.RED + "/hat " + ChatColor.YELLOW + "Get a new hat!");
	  if (player.hasPermission("srpg.adminchat")){
		  player.sendMessage(ChatColor.RED + "/a " + ChatColor.YELLOW + "Talk in admin chat");}
	  if (player.hasPermission("srpg.council")){
		  player.sendMessage(ChatColor.RED + "/hc " + ChatColor.YELLOW + "Talk in council chat");}
	  if (player.hasPermission("srpg.asay")){
		  player.sendMessage(ChatColor.RED + "/asay " + ChatColor.YELLOW + "Alternate admin say command");}
  }
}
