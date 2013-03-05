package net.dmulloy2.swornrpg.listeners;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.InventoryHelper;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.dmulloy2.swornrpg.data.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.PluginManager;

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
							System.out.println("[SwornRPG] " + pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " iron ingot(s)");
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
							System.out.println("[SwornRPG] " + pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " diamond(s)");
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
							System.out.println("[SwornRPG] " + pl.getName() + " salvaged " + mitem.toString().toLowerCase().replaceAll("_", " ") + " for " + amtIron + " gold ingot(s)");
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
     * Books on Player Deaths
     * Creds to Milkywayz (BukkitDev Staff) for helping me on this :3
     */
	@EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event)
    {
		if (plugin.deathbook == true)
		{
			Entity ent = event.getEntity();
			if(ent instanceof Player)
			{
				PluginManager pm = Bukkit.getPluginManager();
				if (pm.getPlugin("Essentials") == null)
				{
					final Player player = (Player)event.getEntity();
					final PlayerData data = plugin.getPlayerDataCache().getData(player);
					if (!(data.isDeathbookdisabled()))
					{
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
				}
				else
				{
					final Player player = (Player)event.getEntity();
					final PlayerData data = plugin.getPlayerDataCache().getData(player);
					if (!(data.isDeathbookdisabled()))
					{
						double x = (int) Math.floor(player.getLocation().getX());
						double y = (int) Math.floor(player.getLocation().getY());
						double z = (int) Math.floor(player.getLocation().getZ());
						ConsoleCommandSender ccs = Bukkit.getServer().getConsoleSender();
						Bukkit.getServer().dispatchCommand(ccs, "mail send " + player.getName() + " You died at " + x + ", " + y + ", " + z);
						player.sendMessage(plugin.prefix + ChatColor.YELLOW + "You have been sent a mail message with your death coords!");
					}
				}
			}
		}
    }
	
	//Creates a player data file if one does not exist
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) 
	{
		// Store current time - will use it later
		final long now = System.currentTimeMillis();
		
		// Try to get the player's data from the cache otherwise create a new data entry
		PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
		if (data == null)
			data = plugin.getPlayerDataCache().newData(event.getPlayer());
		
		if (data.getFrenzyusedlevel() != (data.getPlayerxp()/125))
			data.setFrenzyused(false);

		// Set most recent login time (now)
		data.setLastOnline(now);
		
		data.setTimeOfLastUpdate(now);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		// Treat as player disconnect
		onPlayerDisconnect(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(final PlayerKickEvent event) {
		if (!event.isCancelled()) {
			// Treat as player disconnect
			onPlayerDisconnect(event.getPlayer());
		}
	}

	public void onPlayerDisconnect(final Player player) 
	{
		final long now = System.currentTimeMillis();
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		// Update spent time before setting their disconnect time
		data.updateSpentTime();
		if (plugin.onlinetime == true)
		{
			data.setPlayerxp((int) (data.getPlayerxp() + ((now - data.getTimeOfLastUpdate())/40000)));
			data.setLastOnline(System.currentTimeMillis());
		}
	}
}
