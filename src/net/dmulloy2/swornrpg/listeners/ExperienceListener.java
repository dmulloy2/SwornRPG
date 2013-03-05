package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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
				//Disables xp gain in war zone, helpful for pvpboxes
				Faction otherFaction = Board.getFactionAt(new FLocation(killer.getLocation()));
				//Double check to make sure it can't be exploited
				Faction otherFaction2 = Board.getFactionAt(new FLocation(killed.getLocation()));
				if ((otherFaction.isWarZone())||(otherFaction2.isWarZone()))
					return;
				String killerp = killer.getName();
				String killedp = killed.getName();
				//Killer xp gain
				int killxp = 25;
				PlayerData data = plugin.getPlayerDataCache().getData(killerp);
				data.setPlayerxp(data.getPlayerxp() + killxp);
				killer.sendMessage(plugin.prefix + ChatColor.GREEN + "You were rewarded " + killxp + " xp for killing " + killedp);
				//Killed xp loss
				int killedxp = 10;
				PlayerData data1 = plugin.getPlayerDataCache().getData(killedp);
				data1.setPlayerxp(data1.getPlayerxp() - killedxp);
				killed.sendMessage(plugin.prefix + ChatColor.RED + "You lost " + killedxp + " xp after getting killed  by " + killerp);
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
			killer.sendMessage(plugin.prefix + ChatColor.GREEN + "You were rewarded " + killxp + " xp for killing a(n) " + mobname);
		}
	}
}
