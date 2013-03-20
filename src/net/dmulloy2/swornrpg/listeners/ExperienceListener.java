package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.events.PlayerLevelupEvent;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.World;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 * @author dmulloy2
 */

public class ExperienceListener implements Listener 
{

	private SwornRPG plugin;
	public ExperienceListener(SwornRPG plugin) 
	{
		this.plugin = plugin;
	}
	
	//Rewards XP in PvP situations
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event)
	{
		//Checks for player kills to be enabled in the config
		if (plugin.playerkills == false)
			return;
		Player killed = event.getEntity().getPlayer();
		Player killer = event.getEntity().getKiller();
		//Checks to see if it was PvP
		if (killer instanceof Player)
		{
			//Factions Warzone check, helpful for pvpboxes
			PluginManager pm = Bukkit.getServer().getPluginManager();
			if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				Faction otherFaction2 = Board.getFactionAt(new FLocation(killed.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction2.isWarZone()))
					return;
			}
			String killerp = killer.getName();
			String killedp = killed.getName();
			//Checks for suicide
			if (killedp == killerp)
				return;
			//Killer xp gain
			int killxp = plugin.killergain;
			pm.callEvent(new PlayerXpGainEvent (killer, killxp));
			killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing " + ChatColor.RED + killedp);
			//Killed xp loss
			int killedxp = -(plugin.killedloss);
			int msgxp = Math.abs(killedxp);
			pm.callEvent(new PlayerXpGainEvent (killed, killedxp));
			killed.sendMessage(plugin.prefix + ChatColor.YELLOW + "You lost " + ChatColor.RED + msgxp + ChatColor.YELLOW + " xp after getting killed by " + ChatColor.RED + killerp);
		}
	}
	
	//Rewards XP in PvE situations
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		//Checks for mob kills to be enabled in the config
		if (plugin.mobkills == false)
			return;
		Entity kill = event.getEntity().getKiller();
		Entity killed = event.getEntity();
		//Checks to make sure it isnt pvp
		if (killed instanceof Player)
			return;
		//Checks to make sure the killer is a player
		if (kill instanceof Player)
		{
			Player killer = event.getEntity().getKiller();
			String mobname = event.getEntity().getType().toString().toLowerCase().replaceAll("_", " ");
			PluginManager pm = Bukkit.getServer().getPluginManager();
			//Factions exploit check
			if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction.isSafeZone()))
					return;
			}
			//Camping check
			World world = kill.getWorld();
			Location loc = kill.getLocation();
			int RADIUS = 5;
			for (int dx = -RADIUS; dx <= RADIUS; dx++) 
			{
				for (int dy = -RADIUS; dy <= RADIUS; dy++) 
				{
					for (int dz = -RADIUS; dz <= RADIUS; dz++) 
					{
						int id = world.getBlockTypeIdAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz);
						if (id == 52)
						{
							killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You find no rewards camping mob spawners");
							return;
						}
					}
				}
			}
			//Kill xp calculation
			int killxp;
			if (
					mobname.equals("wither")
					||mobname.equals("ender dragon")
				)
				killxp = plugin.mobkillsxp*3;
			else if (
						mobname.equals("creeper")
						||mobname.equals("enderman")
						||mobname.equals("iron golem")
						||mobname.equals("skeleton")
						||mobname.equals("blaze")
						||mobname.contains("zombie")
						||mobname.contains("spider")
						||mobname.equals("ghast")
						||mobname.equals("magma cube")
						||mobname.equals("witch")
					)
				killxp = plugin.mobkillsxp*2;
			else
				killxp = plugin.mobkillsxp;
			//Call event
			pm.callEvent(new PlayerXpGainEvent (killer, killxp));
			//Send message
			if (mobname.startsWith("e")||mobname.startsWith("o")||mobname.startsWith("i"))				
			{
				killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing an " + ChatColor.RED + mobname);
			}
			else
			{
				killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing a " + ChatColor.RED + mobname);
			}
		}
	}
	
	//Rewards XP on Minecraft xp levelup
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		//Checks to make sure xp level xp gain is enabled in the config
		if (plugin.xplevel == false)
			return;
		Player player = event.getPlayer();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		//Factions exploit check
		if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
		{
			Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
			if (otherFaction.isWarZone())
				return;
		}
		//Camping check
		World world = player.getWorld();
		Location loc = player.getLocation();
		int RADIUS = 5;
		for (int dx = -RADIUS; dx <= RADIUS; dx++) 
		{
			for (int dy = -RADIUS; dy <= RADIUS; dy++) 
			{
				for (int dz = -RADIUS; dz <= RADIUS; dz++) 
				{
					int id = world.getBlockTypeIdAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz);
					if (id == 52)
					{
						player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You find no rewards camping mob spawners");
						return;
					}
				}
			}
		}
		int oldlevel = event.getOldLevel();
		int newlevel = event.getNewLevel();
		if (newlevel - oldlevel != 1)
			return;
		int xpgained = plugin.xplevelgain;
		Bukkit.getServer().getPluginManager().callEvent(new PlayerXpGainEvent (player, xpgained));
		player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You gained " + ChatColor.GREEN + xpgained + ChatColor.YELLOW + " xp for gaining Minecraft xp");
	}
	
	//Rewards items and money on player levelup
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelup(PlayerLevelupEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		//Prepares data for the next level
		data.setFrenzyused(false);
		data.setLevel(data.getLevel() + 1);
		data.setXpneeded(data.getXpneeded() + (data.getXpneeded()/4));
		data.setPlayerxp(0);
		int level = data.getLevel();
		player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have leveled up to level " + ChatColor.GREEN + level + ChatColor.YELLOW + "!");
		//Awards money if money rewards are enabled
		if (plugin.money == true)
		{
			//Checks for vault
			if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
			{
				Economy economy = plugin.getEconomy();
				double money = (int) level*plugin.basemoney;
				economy.depositPlayer(player.getName(), money);
				player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " +  ChatColor.GREEN + "$" + money + ChatColor.YELLOW + " for leveling up");
			}
		}
		//Awards items if money rewards are enabled
		if (plugin.items == true)
		{
			int rewardamt = level*plugin.itemperlevel;
			ItemStack item = new ItemStack(plugin.itemreward, rewardamt);
			String friendlyitem = item.getType().toString().toLowerCase().replaceAll("_", " ");
			InventoryWorkaround.addItems(player.getInventory(), item);
			player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + rewardamt + " " + friendlyitem + ChatColor.YELLOW + "(s)");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerXpGain(PlayerXpGainEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		int xpgained = event.getXpGained();
		//Add the xp gained to their overall xp
		data.setPlayerxp(data.getPlayerxp() + xpgained);
		data.setTotalxp(data.getTotalxp() + xpgained);
		int xp = data.getPlayerxp();
		int xpneeded = data.getXpneeded();
		int newlevel = (xp/xpneeded);
		int oldlevel = data.getLevel();
		//Did the player level up?
		if ((xp - xpneeded) >= 1)
		{
			//If the player leveled up, call level up event
			Bukkit.getServer().getPluginManager().callEvent(new PlayerLevelupEvent (player, newlevel, oldlevel));
		}
	}
}