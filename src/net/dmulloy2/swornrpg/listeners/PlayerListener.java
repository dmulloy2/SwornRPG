package net.dmulloy2.swornrpg.listeners;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.*;
import net.dmulloy2.swornrpg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 * @author dmulloy2
 * @contributor t7seven7t
 */

public class PlayerListener implements Listener 
{

	private SwornRPG plugin;
	private List<String> pages = new ArrayList<String>();
	int ChestMax = 8;
	int LegsMax = 7;
	int HelmMax = 5;
	int bootsMax = 4;

	public PlayerListener(SwornRPG plugin) 
	{
		this.plugin = plugin;
	}

	/**Salvaging**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		if (!event.hasBlock()) 
			return;

		if (event.getClickedBlock() == null) 
			return;

		Player pl = event.getPlayer();

		Block block = event.getClickedBlock();
      
		if (!(event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
			return;
		
		if (!(pl.getGameMode() == (GameMode.SURVIVAL)))
			return;
		
		if(plugin.salvaging == false)
			return;

		if (block.getType().equals(Material.IRON_BLOCK))
		{
			if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE))) 
			{
				ItemStack item = pl.getItemInHand();
				Material mitem = item.getType();
				double mult = 1.0D - ((double) item.getDurability() / item.getType().getMaxDurability());
				double amtIron = 0;
				if (mitem.equals(Material.IRON_BOOTS))
					amtIron = Math.floor(this.bootsMax * mult);
				if (mitem.equals(Material.IRON_HELMET)) 
					amtIron = Math.floor(this.HelmMax * mult);
				if (mitem.equals(Material.IRON_LEGGINGS)) 
					amtIron = Math.floor(this.LegsMax * mult);
				if (mitem.equals(Material.IRON_CHESTPLATE))
					amtIron = Math.floor(this.ChestMax * mult);
				if (amtIron > 0.0D)
				{
					pl.sendMessage(ChatColor.GRAY + "You have salvaged an " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " iron ingot(s)");
					plugin.outConsole(pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " iron ingot(s)");
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					Material give = Material.IRON_INGOT;
					ItemStack salvaged = new ItemStack(give.getId(), (int)amtIron);
					InventoryWorkaround.addItems(inv, salvaged);
				}
				else
				{
					pl.sendMessage(ChatColor.GRAY + "Error, '" + mitem.toString().toLowerCase().replaceAll("_", " ") + "' is not a type of iron armor");
				}
			}
		} 
		else if (block.getType().equals(Material.DIAMOND_BLOCK)) 
		{
			if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE)))
			{
				ItemStack item = pl.getItemInHand();
				Material mitem = item.getType();
				double mult = 1.0D - ((double) item.getDurability() / item.getType().getMaxDurability());
				double amtIron = 0.0D;
				if (mitem.equals(Material.DIAMOND_BOOTS))   
					amtIron = Math.floor(this.bootsMax * mult);
				if (mitem.equals(Material.DIAMOND_HELMET)) 
					amtIron = Math.floor(this.HelmMax * mult);
				if (mitem.equals(Material.DIAMOND_LEGGINGS))
					amtIron = Math.floor(this.LegsMax * mult);
				if (mitem.equals(Material.DIAMOND_CHESTPLATE)) 
					amtIron = Math.floor(this.ChestMax * mult);
				amtIron -= 1.0D;
				if (amtIron > 0.0D)
				{
					pl.sendMessage(ChatColor.GRAY + "You have salvaged a " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " diamond(s)");
					plugin.outConsole(pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " diamond(s)");
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					Material give = Material.DIAMOND;
					ItemStack salvaged = new ItemStack(give.getId(), (int)amtIron);
					InventoryWorkaround.addItems(inv, salvaged);
				}
				else
				{
					pl.sendMessage(ChatColor.GRAY + "Error, '" + mitem.toString().toLowerCase().replaceAll("_", " ") + "' is not a type of diamond armor");
				}
			}
		} 
		else if (block.getType().equals(Material.GOLD_BLOCK))
		{
			if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE)))
			{
				ItemStack item = pl.getItemInHand();
				Material mitem = item.getType();
				double mult = 1.0D - ((double) item.getDurability() / item.getType().getMaxDurability());
				double amtIron = 0.0D;
				if (mitem.equals(Material.GOLD_BOOTS))   
					amtIron = Math.floor(this.bootsMax * mult);
				if (mitem.equals(Material.GOLD_HELMET)) 
					amtIron = Math.floor(this.HelmMax * mult);
				if (mitem.equals(Material.GOLD_LEGGINGS))
					amtIron = Math.floor(this.LegsMax * mult);
				if (mitem.equals(Material.GOLD_CHESTPLATE)) 
					amtIron = Math.floor(this.ChestMax * mult);
				amtIron -= 1.0D;
				if (amtIron > 0.0D)
				{
					pl.sendMessage(ChatColor.GRAY + "You have salvaged a " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " gold ingot(s)");
					plugin.outConsole(pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " gold ingot(s)");
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					Material give = Material.IRON_INGOT;
					ItemStack salvaged = new ItemStack(give.getId(), (int)amtIron);
					InventoryWorkaround.addItems(inv, salvaged);
					}
				else
				{
					pl.sendMessage(ChatColor.GRAY + "Error, '" + mitem.toString().toLowerCase().replaceAll("_", " ") + "' is not a type of gold armor");
				}
			}
		}
	}
	    
    /**
     * Books or Mail messages with death coordinates
     * Creds to Milkywayz for helping with the books :)
     */
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
		/**Checks to make sure death books are enabled in the config**/
		if (plugin.deathbook == false)
			return;
		final Player player = event.getEntity();
		/**Warzone check**/
		PluginManager pm = Bukkit.getPluginManager();
		if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
		{
			Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
			if (otherFaction.isWarZone())
				return;
		}
		/**Player death book toggle check**/
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (!(data.isDeathbookdisabled()))
		{
			/**Check for essentials**/
			if (!(pm.isPluginEnabled("Essentials")))
			{
				/**If not found, create a book with their death coords**/
				double x = (int) Math.floor(player.getLocation().getX());
				double y = (int) Math.floor(player.getLocation().getY());
				double z = (int) Math.floor(player.getLocation().getZ());
				final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
				BookMeta meta = (BookMeta)book.getItemMeta();
				pages.add(plugin.prefix + ChatColor.RED + player.getName() + ChatColor.GOLD + " died at " + ChatColor.RED + x + ", " + y + ", " + z);
				meta.setTitle(ChatColor.RED + "DeathCoords");
				meta.setAuthor(ChatColor.GOLD + "SwornRPG");
				meta.setPages(pages);
				book.setItemMeta(meta);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						InventoryWorkaround.addItems(player.getInventory(), book);
					}				
				},20);
				this.pages.clear();
				if (plugin.debug) plugin.outConsole(player.getName() + "was given a book with their death coords");
			}
			else
			{
				/**If essentials is found, send the message via mail**/
				double x = (int) Math.floor(player.getLocation().getX());
				double y = (int) Math.floor(player.getLocation().getY());
				double z = (int) Math.floor(player.getLocation().getZ());
				ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
				Entity killer = event.getEntity().getKiller();
				if (killer instanceof Player)
				{
					Player killerp = event.getEntity().getKiller();
					String killern = killerp.getName();
					Bukkit.getServer().dispatchCommand(ccs, "mail send " + player.getName() + " You were killed by" + killern  + "at " + x + ", " + y + ", " + z + " on " + TimeUtil.getLongDateCurr());
					player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have been sent a mail message with your death coords!");
					if (plugin.debug) plugin.outConsole(player.getName() + "was sent a mail message with their death coords");
				}
				else
				{
					Bukkit.getServer().dispatchCommand(ccs, "mail send " + player.getName() + " You died at " + x + ", " + y + ", " + z + " on " + TimeUtil.getLongDateCurr());
					player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have been sent a mail message with your death coords!");
					if (plugin.debug) plugin.outConsole(player.getName() + "was sent a mail message with their death coords");
				}
			}
		}
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) 
	{	
		Player player = event.getPlayer();
		String playerp = player.getName();
		/**Try to get the player's data from the cache otherwise create a new data entry**/
		PlayerData data = plugin.getPlayerDataCache().getData(playerp);
		if (data == null)
		{
			if (plugin.debug) plugin.outConsole("Creating a new player data file for " + playerp);
			plugin.getPlayerDataCache().newData(playerp);
			/**Basic data that a player needs**/
			data = plugin.getPlayerDataCache().getData(playerp);
			data.setXpneeded(100 + (data.getPlayerxp()/4));
			data.setLevel(0);
		}
		
		/**Converter to the new leveling system**/
		if (data.getXpneeded() == 0)
		{
			data.setXpneeded(100 + (data.getPlayerxp()/4));
		}
	
		/**Makes sure Tag changes are permanent**/
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if (pm.isPluginEnabled("TagAPI"))
		{
			String name = player.getName();
			String newName = this.plugin.getDefinedName(name);
			if (newName != name) 
			{
				try 
				{
					this.plugin.addTagChange(name, newName);
				} 
				catch (final TooBigException e) 
				{
					this.plugin.getLogger().severe("Error while changing name from memory:");
					this.plugin.getLogger().severe(e.getMessage());
				}
			}
		}
		
		/**Update Notification**/
		if (plugin.update)
		{
			if (player.hasPermission("srpg.update") && (plugin.updateNeeded()))
			{
				player.sendMessage(plugin.prefix + ChatColor.YELLOW + "A new version is available! Get it at");
				player.sendMessage(plugin.prefix + ChatColor.YELLOW + "http://dev.bukkit.org/server-mods/swornrpg/");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) 
	{
		/**Treat as player disconnect**/
		onPlayerDisconnect(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) 
	{
		if (!event.isCancelled()) 
		{
			/**Treat as player disconnect**/
			onPlayerDisconnect(event.getPlayer());
		}
	}

	/**Basic data needing to be false when a player leaves the game**/
	public void onPlayerDisconnect(Player player) 
	{
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (data.isRiding())
		{
			data.setRiding(false);
		}
		if (data.isVehicle())
		{
			data.setVehicle(false);
		}
		if (data.isSitting())
		{
			data.setSitting(false);
			Entity vehicle = player.getVehicle();
			if (vehicle != null)
				vehicle.remove();
		}
		if (data.isSpick())
		{
			data.setSpick(false);
		}
	}
	
	/**Checks to make sure that if a player is riding another player, teleportation is not disabled**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		String cmd = event.getMessage().toString().toLowerCase().replaceAll("/", "").replaceAll(" ", "");
		if (cmd.contains("spawn")||cmd.contains("home")||cmd.contains("warp")||cmd.contains("tp"))
		{
			if (player.getVehicle() != null && data.isRiding())
			{
				/**If a player is riding another player, leave the vehicle**/
				player.leaveVehicle();
				data.setRiding(false);
			}
			if (player.getPassenger() != null && data.isVehicle())
			{
				/**If a player is being ridden, eject the passenger**/
				player.eject();
				data.setVehicle(false);
			}
		}
	}
		
	/**Cancel pickup event if a player is on another player's head**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if ((data.isRiding()) && (player.getVehicle() != null))
		{	
			event.setCancelled(true);
		}
	}
		
	/**Super Pickaxes**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSuperPickActivate(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if (!(action == (Action.RIGHT_CLICK_AIR)))
			return;
		final Player player = event.getPlayer();
		String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
		if (!(inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold")))
			return;
		if (plugin.spenabled != true)
			return;
		if (player.getItemInHand() == null)
			return;
		final PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (data.isScooldown())
		{
			player.sendMessage(FormatUtil.format(plugin.prefix + "&cYou are recovering from super pickaxe"));
			return;
		}
		if (data.isSpick())
		{
			player.sendMessage(FormatUtil.format(plugin.prefix + "&cError, you are already using this ability"));
		}
		player.sendMessage(FormatUtil.format(plugin.prefix + "&aReady to mine?"));
		player.sendMessage(FormatUtil.format(plugin.prefix + "&aYour pickaxe has become a super pickaxe!"));
		int level = data.getLevel();
		final int duration = (20*(plugin.spbaseduration + (level*plugin.superpickm)));
		int strength = 1;
		data.setSpick(true);
		player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect((int) duration, strength));
		if (plugin.debug) plugin.outConsole(player.getName() + "has activated super pickaxe. Duration: " + duration);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				player.sendMessage(FormatUtil.format(plugin.prefix + "&eSuper pickaxe ability has worn off"));
				data.setSpick(false);
				data.setScooldown(true);
				int cooldown = (20*(duration*plugin.superpickcd));
				data.setSuperpickcd(cooldown);
				if (plugin.debug) plugin.outConsole(player.getName() + "has a cooldown of " + cooldown + " for super pickaxe");
			}				
		},(duration));
	}
}
