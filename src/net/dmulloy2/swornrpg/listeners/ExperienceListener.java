package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.events.PlayerLevelupEvent;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.milkbowl.vault.economy.Economy;

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
		
	/**Rewards XP in PvP situations**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		/**Checks for player kills to be enabled in the config**/
		if (plugin.playerkills == false)
			return;
		
		Player killed = event.getEntity().getPlayer();
		Player killer = event.getEntity().getKiller();
		
		/**Checks to see if it was PvP**/
		if (killer instanceof Player)
		{
			/**Warzone Check**/
			PluginManager pm = plugin.getServer().getPluginManager();
			if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				Faction otherFaction2 = Board.getFactionAt(new FLocation(killed.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction2.isWarZone()))
					return;
			}
			
			String killerp = killer.getName();
			String killedp = killed.getName();
			
			/**Suicide Check**/
			if (killedp == killerp)
				return;
			
			String message = "";
			
			/**Killer Xp Gain**/
			int killxp = plugin.killergain;
			message = (plugin.prefix + FormatUtil.format(plugin.getMessage("pvp_kill_msg"), killxp, killedp));
			pm.callEvent(new PlayerXpGainEvent (killer, killxp, message));
			
			/**Killed Xp Loss**/
			int killedxp = -(plugin.killedloss);
			int msgxp = Math.abs(killedxp);
			message = (plugin.prefix + FormatUtil.format(plugin.getMessage("pvp_death_msg"), msgxp, killerp));
			pm.callEvent(new PlayerXpGainEvent (killed, killedxp, message));
			
			/**Debug Message**/
			if (plugin.debug) 
			{
				plugin.outConsole(killedp + " lost " + msgxp + " xp after getting killed by  " + killerp);
				plugin.outConsole(killerp + " gained " + killxp + " xp for killing " + killedp);
			} 
			
		}
	}
	
	/**Rewards XP in PvE situations**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event)
	{
		/**Checks for mob kills to be enabled in the config**/
		if (plugin.mobkills == false)
			return;
		
		Entity kill = event.getEntity().getKiller();
		Entity killed = event.getEntity();
		
		/**Checks to make sure it wasn't pvp**/
		if (killed instanceof Player)
			return;
		
		/**Checks to make sure the killer is a player**/
		if (kill instanceof Player)
		{
			Player killer = event.getEntity().getKiller();
			String mobname = event.getEntity().getType().toString().toLowerCase().replaceAll("_", " ");
			PluginManager pm = plugin.getServer().getPluginManager();
			
			/**Warzone and Safezone check**/
			if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
			{
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction.isSafeZone()))
					return;
			}
			
			/**Camping Check**/
			World world = kill.getWorld();
			Location loc = kill.getLocation();
			int RADIUS = 10;
			for (int dx = -RADIUS; dx <= RADIUS; dx++) 
			{
				for (int dy = -RADIUS; dy <= RADIUS; dy++) 
				{
					for (int dz = -RADIUS; dz <= RADIUS; dz++) 
					{
						int id = world.getBlockTypeIdAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz);
						if (id == 52)
						{
							killer.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("spawner_camper")));
							return;
						}
					}
				}
			}
			
			/**XP gain calculation**/
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
						||mobname.equals("slime")
					)
				killxp = plugin.mobkillsxp*2;
			else
				killxp = plugin.mobkillsxp;
			
			/**Message**/
			String article = "";
			if (mobname.startsWith("a")||mobname.startsWith("e")||mobname.startsWith("i")||mobname.startsWith("o")||mobname.startsWith("u"))
				article = "an";
			else
				article = "a";
			String message = (plugin.prefix + FormatUtil.format(plugin.getMessage("mob_kill"), killxp, (article + " &c" + mobname)));
			
			/**Call Event**/
			pm.callEvent(new PlayerXpGainEvent (killer, killxp, message));
			if (plugin.debug) plugin.outConsole(killer.getName() + "gained " + killxp + " xp for killing " + mobname);
		}
	}
	
	/**Rewards XP on Minecraft xp levelup**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelChange(PlayerLevelChangeEvent event)
	{
		/**Checks to make sure xp level xp gain is enabled in the config**/
		if (plugin.xplevel == false)
			return;
		
		Player player = event.getPlayer();

		/**Warzone Check**/
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
		{
			Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
			if (otherFaction.isWarZone())
				return;
		}
		
		/**Camping Check**/
		World world = player.getWorld();
		Location loc = player.getLocation();
		int RADIUS = 10;
		for (int dx = -RADIUS; dx <= RADIUS; dx++) 
		{
			for (int dy = -RADIUS; dy <= RADIUS; dy++) 
			{
				for (int dz = -RADIUS; dz <= RADIUS; dz++) 
				{
					int id = world.getBlockTypeIdAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz);
					if (id == 52)
					{
						player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("spawner_camper")));
						return;
					}
				}
			}
		}
		
		/**Define Stuff**/
		int oldlevel = event.getOldLevel();
		int newlevel = event.getNewLevel();
		if (newlevel - oldlevel != 1)
			return;
		int xpgained = plugin.xplevelgain;
		
		/**Call Event**/
		String message = (plugin.prefix + FormatUtil.format(plugin.getMessage("mc_xp_gain"), xpgained));
		pm.callEvent(new PlayerXpGainEvent (player, xpgained, message));
	}
	
	/**Rewards items and money on player levelup**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLevelup(PlayerLevelupEvent event)
	{
		/**Cancellation check**/
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		
		/**Prepare data for the next level**/
		data.setLevel(data.getLevel() + 1);
		data.setXpneeded(data.getXpneeded() + (data.getXpneeded()/4));
		data.setPlayerxp(0);
		
		/**Send messages**/
		int level = data.getLevel();
		player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup"), level));
		if (plugin.debug) plugin.outConsole(player.getName() + " leveled up to level " + level);
		
		/**Award money if enabled**/
		if (plugin.money == true)
		{
			/**Vault Check**/
			PluginManager pm = plugin.getServer().getPluginManager();
			if (pm.isPluginEnabled("Vault"))
			{
				Economy economy = plugin.getEconomy();
				double money = (int) level*plugin.basemoney;
				economy.depositPlayer(player.getName(), money);
				player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_money"), money));
			}
		}
		
		/**Award items if enabled**/
		if (plugin.items == true)
		{
			int rewardamt = level*plugin.itemperlevel;
			ItemStack item = new ItemStack(plugin.itemreward, rewardamt);
			String friendlyitem = item.getType().toString().toLowerCase().replaceAll("_", " ");
			InventoryWorkaround.addItems(player.getInventory(), item);
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("levelup_items"), rewardamt, friendlyitem));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void OnPlayerXpGain(PlayerXpGainEvent event)
	{
		/**Cancellation check**/
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		String message = event.getMessage();
		
		/**Add the xp gained to their overall xp**/
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		int xpgained = event.getXpGained();
		data.setPlayerxp(data.getPlayerxp() + xpgained);
		data.setTotalxp(data.getTotalxp() + xpgained);
		
		/**Send the message**/
		player.sendMessage(message);
		
		/**Levelup check**/
		int xp = data.getPlayerxp();
		int xpneeded = data.getXpneeded();
		int newlevel = (xp/xpneeded);
		int oldlevel = data.getLevel();
		
		if ((xp - xpneeded) >= 0)
		{
			/**If so, call levelup event**/
			PluginManager pm = plugin.getServer().getPluginManager();
			pm.callEvent(new PlayerLevelupEvent (player, newlevel, oldlevel));
		}
	}
}