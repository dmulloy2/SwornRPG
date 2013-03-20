package net.dmulloy2.swornrpg.listeners;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.*;
import net.dmulloy2.swornrpg.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
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
import org.bukkit.util.Vector;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;

/**
 * @author dmulloy2
 * @contributor t7seven7t
 * @contributor Milkywayz
 */

public class PlayerListener implements Listener 
{

	private SwornRPG plugin;
	private List<String> pages = new ArrayList<String>();
	int ChestMax = 8;
	int LegsMax = 7;
	int HelmMax = 5;
	int bootsMax = 4;

	int ironMax = 155;
	int diamondMax = 155;

	public PlayerListener(SwornRPG plugin) 
	{
		this.plugin = plugin;
	}

	//Salvaging
	@SuppressWarnings("deprecation") //player.updateInventory()
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		try
		{
			if (!event.hasBlock()) 
			{
				return;
			}

			if (event.getClickedBlock() == null) 
			{
				return;
			}

			Player pl = event.getPlayer();

			Block block = event.getClickedBlock();
      
			event.getAction().equals(Action.LEFT_CLICK_BLOCK);

			if (block.getType().equals(Material.IRON_BLOCK))
			{
				if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE))) 
				{
					ItemStack item = pl.getItemInHand();
					Material mitem = item.getType();
					double mult = 1.0D - item.getDurability() / this.ironMax;
					double amtIron = 0.0D;
					if (mitem.equals(Material.IRON_BOOTS))
					{
						amtIron = Math.ceil(this.bootsMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.IRON_HELMET)) 
					{
						amtIron = Math.ceil(this.HelmMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.IRON_LEGGINGS)) 
					{
						amtIron = Math.ceil(this.LegsMax * mult);
						if (amtIron < 0.0D)
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.IRON_CHESTPLATE))
					{
						amtIron = Math.ceil(this.ChestMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					if (amtIron > 0.0D)
						try
					{
							pl.sendMessage(ChatColor.GRAY + "You have salvaged an " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " iron ingot(s)");
							plugin.outConsole(pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " iron ingot(s)");
							Inventory inv = pl.getInventory();
							inv.removeItem(new ItemStack[] { item });
							Material give = Material.IRON_INGOT;
							int slot = getSlot(give.getId(), inv);
							if (slot == -1) 
							{
								slot = InventoryHelper.getFirstFreeSlot(inv);
							}	
							if (slot <= -1) return;
							int amt = 0;
							if (inv.getItem(slot) != null) 
							{
								amt = inv.getItem(slot).getAmount();
							}

							ItemStack itm = new ItemStack(give.getId(), amt + (int)amtIron);
							inv.setItem(slot, itm);
							pl.updateInventory();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					else
						pl.sendMessage(ChatColor.GRAY + "Error, '" + mitem.toString().toLowerCase().replaceAll("_", " ") + "' is not a type of iron armor");
				}
			} 
			else if (block.getType().equals(Material.DIAMOND_BLOCK)) 
			{
				if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE)))
				{
					ItemStack item = pl.getItemInHand();
					Material mitem = item.getType();

					double mult = 1.0D - item.getDurability() / this.diamondMax;
					double amtIron = 0.0D;
					if (mitem.equals(Material.DIAMOND_BOOTS))
					{
						amtIron = Math.ceil(this.bootsMax * mult);
					}
					if (amtIron < 0.0D)
					{
						amtIron = 1.0D;
					}
					if (mitem.equals(Material.DIAMOND_HELMET)) 
					{
						amtIron = Math.ceil(this.HelmMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.DIAMOND_LEGGINGS))
					{
						amtIron = Math.ceil(this.LegsMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.DIAMOND_CHESTPLATE))
					{
						amtIron = Math.ceil(this.ChestMax * mult);
						if (amtIron < 0.0D)
						{
							amtIron = 1.0D;
						}
					}
					amtIron -= 1.0D;
					if (amtIron > 0.0D)
						try 
					{
							pl.sendMessage(ChatColor.GRAY + "You have salvaged a " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " diamond(s)");
							plugin.outConsole(pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " diamond(s)");
							Inventory inv = pl.getInventory();
							inv.removeItem(new ItemStack[] { item });
							Material give = Material.DIAMOND;
							int slot = getSlot(give.getId(), inv);
							if (slot == -1)
							{
								slot = InventoryHelper.getFirstFreeSlot(inv);
							}
							if (slot <= -1) return;
							int amt = 0;
							if (inv.getItem(slot) != null)
							{
								amt = inv.getItem(slot).getAmount();
							}
							ItemStack itm = new ItemStack(give.getId(), amt + (int)amtIron);
							inv.setItem(slot, itm);
							pl.updateInventory();
					}
					catch (Exception localException1)
					{
					}
					else
						pl.sendMessage(ChatColor.GRAY + "Error, '" + mitem.toString().toLowerCase().replaceAll("_", " ") + "' is not a type of diamond armor");
				}
			} 
			else if (block.getType().equals(Material.GOLD_BLOCK))
				if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE)))
				{
					ItemStack item = pl.getItemInHand();
					Material mitem = item.getType();

					double mult = 1.0D - item.getDurability() / this.diamondMax;
					double amtIron = 0.0D;
					if (mitem.equals(Material.GOLD_BOOTS))   
					{
						amtIron = Math.ceil(this.bootsMax * mult);
					}
					if (amtIron < 0.0D)
					{
						amtIron = 1.0D;
					}
					if (mitem.equals(Material.GOLD_HELMET)) 
					{
						amtIron = Math.ceil(this.HelmMax * mult);
						if (amtIron < 0.0D)
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.GOLD_LEGGINGS))
					{
						amtIron = Math.ceil(this.LegsMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					if (mitem.equals(Material.GOLD_CHESTPLATE)) 
					{
						amtIron = Math.ceil(this.ChestMax * mult);
						if (amtIron < 0.0D) 
						{
							amtIron = 1.0D;
						}
					}
					amtIron -= 1.0D;
					if (amtIron > 0.0D)
						try 
					{
							pl.sendMessage(ChatColor.GRAY + "You have salvaged a " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " gold ingot(s)");
							plugin.outConsole(pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " gold ingot(s)");
							Inventory inv = pl.getInventory();
							inv.removeItem(new ItemStack[] { item });
							Material give = Material.GOLD_INGOT;
							int slot = getSlot(give.getId(), inv);
							if (slot == -1) 
							{
								slot = InventoryHelper.getFirstFreeSlot(inv);
							}
							if (slot <= -1) return;
							int amt = 0;
							if (inv.getItem(slot) != null)
							{
								amt = inv.getItem(slot).getAmount();
							}
							ItemStack itm = new ItemStack(give.getId(), amt + (int)amtIron);
							inv.setItem(slot, itm);
							pl.updateInventory();
					}
					catch (Exception localException2)
					{
					}
					else
						pl.sendMessage(ChatColor.GRAY + "Error, '" + mitem.toString().toLowerCase().replaceAll("_", " ") + "' is not a type of gold armor");
				}
		}
		catch (Exception localException3)
		{
		}
	}

	public int getSlot(int id, Inventory inv) 
	{
		int ret = -1;
		for (int i = 0; i <= 35; i++) 
		{
			ItemStack item = inv.getItem(i);
			if (item != null) 
			{
				int type = item.getTypeId();
				if (type == id) 
				{
					ret = i;
				}
			}
		}

		return ret;
	}
	    
    /**
     * Books or Mail messages with death coordinates
     * Creds to Milkywayz (BukkitDev Staff) for helping me on this :3
     */
	@EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event)
    {
		//Checks to make sure death books are enabled in the config
		if (plugin.deathbook == true)
		{
			//Checks that it was a player that died
			Entity ent = event.getEntity();
			if(ent instanceof Player)
			{
				//Warzone check
				PluginManager pm = Bukkit.getPluginManager();
				if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
				{
					Faction otherFaction = Board.getFactionAt(new FLocation(ent.getLocation()));
					if (otherFaction.isWarZone())
						return;
				}
				//Player death book toggle check
				final Player player = (Player)event.getEntity();
				PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
				if (!(data.isDeathbookdisabled()))
				{
					//Check for essentials
					if (!(pm.isPluginEnabled("Essentials")))
					{
						//If not found, create a book with their death coords
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
					}
					else
					{
						//If essentials is found, send the message via mail
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
						}
						else
						{
							Bukkit.getServer().dispatchCommand(ccs, "mail send " + player.getName() + " You died at " + x + ", " + y + ", " + z + " on " + TimeUtil.getLongDateCurr());
							player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have been sent a mail message with your death coords!");
						}
					}
				}
			}
		}
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) 
	{	
		Player player = event.getPlayer();
		String playerp = player.getName();
		// Try to get the player's data from the cache otherwise create a new data entry
		PlayerData data = plugin.getPlayerDataCache().getData(playerp);
		if (data == null)
		{
			plugin.outConsole("Creating a new player data file for " + playerp);
			//Basic data that a player needs
			data = plugin.getPlayerDataCache().getData(playerp);
			data.setPlayerxp(0);
			data.setXpneeded(100 + (data.getPlayerxp()/4));
			data.setLevel(0);
			data.setFrenzyused(false);
		}
		
		//Converter to the new leveling system
		if (data.getXpneeded() == 0)
		{
			data.setXpneeded(100 + (data.getPlayerxp()/4));
		}

		if (data.getLevel() <= 0)
		{
			data.setLevel(0);
		}
	
		//Makes sure Tag changes are permanent
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
		
		//Update Notification
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
		// Treat as player disconnect
		onPlayerDisconnect(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event) 
	{
		if (!event.isCancelled()) 
		{
			// Treat as player disconnect
			onPlayerDisconnect(event.getPlayer());
		}
	}

	public void onPlayerDisconnect(Player player) 
	{
		final PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		//Basic data needing to be false when a player leaves the game
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
		}
	}
	
	//Chairs
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChairInteract(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			//Makes sure they are shift clicking
			if (player.isSneaking())
			{
				//Warzone check (for PvPBoxes)
				PluginManager pm = Bukkit.getPluginManager();
				if (pm.isPluginEnabled("Factions")||pm.isPluginEnabled("SwornNations"))
				{
					Faction otherFaction = Board.getFactionAt(new FLocation(player.getLocation()));
					if (otherFaction.isWarZone())
						return;
				}
				String clicked = event.getClickedBlock().getType().toString().toLowerCase().replaceAll("_", " ");
				//Check to make sure they are clicking on a stair
				if (clicked.contains("step")||clicked.contains("stair"))
				{
					//Set them as a passenger to the stair/arrow
					data.setSitting(true);	
					Arrow it = player.getWorld().spawnArrow(event.getClickedBlock().getLocation().add(0.5, 0, 0.5), new Vector(0, 0, 0), 0f, 0f);
					it.setPassenger(player);
					player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You are now sitting on a " + ChatColor.GREEN + clicked + ChatColor.YELLOW + " stand up with" + ChatColor.RED + " /standup");
				}
			}
		}
		if (event.getAction() == Action.LEFT_CLICK_AIR)
		{
			if (data.isSitting())
			{
				Entity vehicle = event.getPlayer().getVehicle();
				if (vehicle instanceof Arrow)
				{
					//Get out of the chair
					 player.leaveVehicle();
					 vehicle.remove();
					 player.teleport(vehicle.getLocation().add(0, 1, 0));
					 data.setSitting(false);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		/**
		 * Checks to make sure that if a player is riding another player,
		 * teleportation is not disabled
		 */
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		String cmd = event.getMessage().toString().toLowerCase().replaceAll("/", "").replaceAll(" ", "");
		if (cmd.contains("spawn")||cmd.contains("home")||cmd.contains("warp")||cmd.contains("tp"))
		{
			if (player.getVehicle() != null && data.isRiding())
			{
				//If a player is riding another player, leave the vehicle
				player.leaveVehicle();
				data.setRiding(false);
			}
			if (player.getPassenger() != null && data.isVehicle())
			{
				//If a player is being ridden, eject the passenger
				player.eject();
				data.setVehicle(false);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		Player player = event.getPlayer();;
		Entity vehicle = player.getVehicle();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (data.isSitting() && vehicle != null)
		{
			//Cancels block breaking if you are sitting, prevents glitches
			event.setCancelled(true);
			player.sendMessage(plugin.prefix + ChatColor.YELLOW + "If you wish to break this block, use " + ChatColor.RED + "/standup");
		}
		if (data.isRiding() && vehicle != null)
		{
			//Cancels block breaking if you are riding a player
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if ((data.isRiding()) && (player.getVehicle() != null))
		{
			//Cancel pickup event if a player is on another player's head
			event.setCancelled(true);
		}
	}
		
	/**
	 * Super Pickaxes, coming to a SwornRPG near you!
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerActivate(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if (player.getItemInHand() != null)
		{
			String inhand = player.getItemInHand().toString().toLowerCase().replaceAll("_", " ");
			if (inhand.contains("pickaxe")&&!inhand.contains("wood")&&!inhand.contains("gold"));
			{
				player.sendMessage(plugin.prefix + ChatColor.GREEN + "Ready to mine?");
				player.sendMessage(plugin.prefix + ChatColor.GREEN + "Your " + inhand + " has become a super pickaxe!");
			}	
		}
	}
	*/
}