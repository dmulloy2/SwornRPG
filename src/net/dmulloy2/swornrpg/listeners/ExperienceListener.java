package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.events.PlayerLevelupEvent;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

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
		if (plugin.playerkills == false)
			return;
		PluginManager pm = Bukkit.getServer().getPluginManager();
		Player killed = event.getEntity().getPlayer();
		Player killer = event.getEntity().getKiller();
		//Checks to see if Factions is enabled
		if ((pm.getPlugin("Factions") != null)||(pm.getPlugin("SwornNations") != null))
		{
			//Checks to see if it was PvP
			if (killer instanceof Player)
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				Faction otherFaction2 = Board.getFactionAt(new FLocation(killed.getLocation()));
				//Disables xp gain in war zone, helpful for pvpboxes
				if ((otherFaction.isWarZone())||(otherFaction2.isWarZone()))
					return;
				String killerp = killer.getName();
				String killedp = killed.getName();
				//Killer xp gain
				int killxp = 25;
				PlayerData data = plugin.getPlayerDataCache().getData(killerp);
				data.setPlayerxp(data.getPlayerxp() + killxp);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerXpGainEvent (killer));
				killer.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + ChatColor.GREEN + killxp + ChatColor.YELLOW + " xp for killing " + ChatColor.RED + killedp);
				//Killed xp loss
				int killedxp = 10;
				PlayerData data1 = plugin.getPlayerDataCache().getData(killedp);
				data1.setPlayerxp(data1.getPlayerxp() - killedxp);
				killed.sendMessage(plugin.prefix + ChatColor.YELLOW + "You lost " + ChatColor.RED + killedxp + ChatColor.YELLOW + " xp after getting killed  by " + ChatColor.RED + killerp);
			}
		}
	}
	
	//Rewards XP in PvE situations
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (plugin.mobkills == false)
			return;
		Entity kill = event.getEntity().getKiller();
		if (kill instanceof Player)
		{
			Player killer = event.getEntity().getKiller();
			String mobname = event.getEntity().getType().toString().toLowerCase();
			Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
			if (otherFaction.isWarZone())
				return;
			int killxp = 5;
			PlayerData data = plugin.getPlayerDataCache().getData(killer.getName());
			data.setPlayerxp(data.getPlayerxp() + killxp);
			Bukkit.getServer().getPluginManager().callEvent(new PlayerXpGainEvent (killer));
			if (mobname.startsWith("e")||mobname.startsWith("o"))
				
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
		if (plugin.xplevel == false)
			return;
		Player player = event.getPlayer();
		String playerp = player.getName();
		int oldlevel = event.getOldLevel();
		int newlevel = event.getNewLevel();
		if (newlevel - oldlevel != 1)
			return;
		PlayerData data = plugin.getPlayerDataCache().getData(playerp);
		int xpgained = 15;
		data.setPlayerxp(data.getPlayerxp() + xpgained);
		Bukkit.getServer().getPluginManager().callEvent(new PlayerXpGainEvent (player));
		player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You gained " + ChatColor.GREEN + xpgained + ChatColor.YELLOW + " xp for gaining Minecraft xp");
	}
	
	//Rewards items and money on player levelup
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelup(PlayerLevelupEvent event)
	{
		Player player = event.getPlayer();
		String playerp = player.getName();
		PlayerData data = plugin.getPlayerDataCache().getData(playerp);
		//Set frenzy used false
		data.setFrenzyused(false);
		//Set current level
		data.setOldlevel(data.getPlayerxp()/125);
		int oldlevel = data.getOldlevel();
		player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have leveled up to level " + ChatColor.GREEN + oldlevel + ChatColor.YELLOW + "!");
		//Awards money if money rewards are enabled
		if (plugin.money == true)
		{
			//Checks for vault
			if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null)
			{
				Economy economy = plugin.getEconomy();
				double money = (int) oldlevel*plugin.basemoney;
				economy.depositPlayer(player.getName(), money);
				player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " +  ChatColor.GREEN + "$" + money + ChatColor.YELLOW + " for leveling up");
			}
		}
		//Awards items if money rewards are enabled
		if (plugin.items == true)
		{
			int level = data.getPlayerxp()/125;
			int rewardamt = level*plugin.itemperlevel;
			ItemStack item = new ItemStack(plugin.itemreward, rewardamt);
			String friendlyitem = item.getType().toString().toLowerCase().replaceAll("_", " ");
			InventoryWorkaround.addItems(player.getInventory(), item);
			player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You were rewarded " + rewardamt + " " + friendlyitem + ChatColor.YELLOW + "(s)");
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerXpGain(PlayerXpGainEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		int oldlevel = data.getOldlevel();
		int newlevel = data.getPlayerxp()/125;
		//If the player leveled up, call the appropriate event
		if (newlevel > oldlevel)
		{
			Bukkit.getServer().getPluginManager().callEvent(new PlayerLevelupEvent (player, newlevel, oldlevel));
		}
		else
		{
			data.setOldlevel(newlevel);
		}
	}
}
