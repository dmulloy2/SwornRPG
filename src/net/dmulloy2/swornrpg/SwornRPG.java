package net.dmulloy2.swornrpg;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.commands.CmdAdm;
import net.dmulloy2.swornrpg.commands.CmdDmu;
import net.dmulloy2.swornrpg.commands.CmdRide;
import net.dmulloy2.swornrpg.commands.CmdUnride;
import net.dmulloy2.swornrpg.commands.SRPG;
import net.dmulloy2.swornrpg.commands.CmdA;
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
  private String pluginName = "SwornRPG";

  public String adminChatPerm = "srpg.adminchat";
  public String adminAbilitiesPerm = "srpg.abilities";
  public String adminRidePerm = "srpg.ride";
  public String adminSayPerm = "srpg.asay";
  public String adminClearPerm = "srpg.aclear";

  public void onDisable()
  {
    System.out.println("[SwornRPG] " + this.pluginName + " v " + getDescription().getVersion() + " has been disabled");
    this.adminchaters.clear();
  }

  public void onEnable()
  {
    System.out.println("[SwornRPG] " + this.pluginName + " v " + getDescription().getVersion() + " has been enabled");
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(this.playerListener, this);
    pm.registerEvents(this.entityListener, this);
    pm.registerEvents(this.blockListener, this);
    pm.registerEvents(this.tagListener, this);
	this.getCommand("srpg").setExecutor(new SRPG(this));
	this.getCommand("ride").setExecutor(new CmdRide(this));
	this.getCommand("unride").setExecutor(new CmdUnride(this));
	this.getCommand("dmu").setExecutor(new CmdDmu(this));
	this.getCommand("adm").setExecutor(new CmdAdm(this));
	this.getCommand("a").setExecutor(new CmdA(this));
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

  public void sendMessageAll(String str)
  {
    List<Player> arr = Util.Who();
    for (int i = 0; i < arr.size(); i++) {
      Player p = (Player)arr.get(i);
      p.sendMessage(str);
    }
  }
}
