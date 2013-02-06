package net.dmulloy2.swornrpg;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.commands.CmdAChat;
import net.dmulloy2.swornrpg.commands.CmdHat;
import net.dmulloy2.swornrpg.commands.CmdFrenzy;
import net.dmulloy2.swornrpg.commands.CmdRide;
import net.dmulloy2.swornrpg.commands.CmdASay;
import net.dmulloy2.swornrpg.commands.CmdHelp;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author orange451
 * @editor dmulloy2
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

	this.getCommand("srpg").setExecutor(new CmdHelp (this));
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

  public void sendAdminMessage(String str, String str2)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      if (PermissionInterface.checkPermission(p, this.adminChatPerm))
        p.sendMessage(ChatColor.GRAY + str + ": " + ChatColor.AQUA + str2);
    }
  }
  
  public void sendCouncilMessage(String str, String str2)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      if (PermissionInterface.checkPermission(p, this.councilChatPerm))
        p.sendMessage(ChatColor.GOLD + str + ": " + ChatColor.RED + str2);
    }
  }

  public void sendMessageAll(String str)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      p.sendMessage(str);
    }
  }
}
