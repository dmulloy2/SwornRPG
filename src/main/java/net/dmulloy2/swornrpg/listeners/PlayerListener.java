package net.dmulloy2.swornrpg.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.BlockDrop;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.TimeUtil;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.InventoryWorkaround;
import net.dmulloy2.swornrpg.util.TooBigException;
import net.dmulloy2.swornrpg.util.Util;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.events.PlayerXpGainEvent;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;

/**
 * @author dmulloy2
 * @contributor Dimpl
 */

public class PlayerListener implements Listener
{
	private SwornRPG plugin;
	private List<String> pages = new ArrayList<String>();
	
	public PlayerListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	/**Salvaging**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (plugin.salvaging == false)
			return;
		
		if (!event.hasBlock())
			return;
	
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
	
		Player pl = event.getPlayer();
		if (!(pl.getGameMode() == (GameMode.SURVIVAL)))
			return;
	
		String blockType = null;
		if (block.getType().equals(Material.IRON_BLOCK))
			blockType = "Iron";
		if (block.getType().equals(Material.GOLD_BLOCK))
			blockType = "Gold";
		if (block.getType().equals(Material.DIAMOND_BLOCK))
			blockType = "Diamond";
	
		if (blockType != null)
		{
			if ((block.getRelative(-1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(1, 0, 0).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, -1).getType() == (Material.FURNACE)) || (block.getRelative(0, 0, 1).getType() == (Material.FURNACE)))
			{
				ItemStack item = pl.getItemInHand();
				Integer itemId = item.getTypeId();
				double mult = 1.0D - ((double) item.getDurability() / item.getType().getMaxDurability());
				double amt = 0.0D;
				
				if (plugin.salvageRef.get(blockType).containsKey(itemId))
					amt = Math.round(plugin.salvageRef.get(blockType).get(itemId) * mult);
	
				if (amt > 0.0D)
				{
					String article = "a";
					if (blockType == "Iron")
						article = "an";
					String materialExtension = " ingot";
					if (blockType == "Diamond")
						materialExtension = "";
					String plural = "";
					if (amt > 1.0D)
						plural = "s";
					String salvagem = item.getType().toString().toLowerCase().replaceAll("_", " ");
					pl.sendMessage(FormatUtil.format(plugin.getMessage("salvage_success"), article, salvagem, amt, (blockType.toLowerCase() + materialExtension + plural)));
					plugin.outConsole(plugin.getMessage("log_salvage"), pl.getName(), item.getType().toString().toLowerCase().replaceAll("_", " "), amt, blockType.toLowerCase(), materialExtension, plural);
					Inventory inv = pl.getInventory();
					inv.removeItem(new ItemStack[] { item });
					Material give= null;
					if (blockType == "Iron")
						give = Material.IRON_INGOT;
					if (blockType == "Gold")
						give = Material.GOLD_INGOT;
					if (blockType == "Diamond")
						give = Material.DIAMOND;
					ItemStack salvaged = new ItemStack(give.getId(), (int)amt);
					InventoryWorkaround.addItems(inv, salvaged);
					event.setCancelled(true);
				}
				else
				{
					String itemname = item.getType().toString().toLowerCase().replaceAll("_", " ");
					pl.sendMessage(FormatUtil.format(plugin.getMessage("not_salvagable"), itemname, blockType.toLowerCase()));
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
		if (player == null)
			return;
		
		/**Warzone check**/
		if (plugin.checkFactions(player, true))
			return;
		
		/**Coordinates**/
		double x = (int) Math.floor(player.getLocation().getX());
		double y = (int) Math.floor(player.getLocation().getY());
		double z = (int) Math.floor(player.getLocation().getZ());
		
		/**Player death book toggle check**/
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
		if (!(data.isDeathbookdisabled()))
		{
			/**Check for essentials**/
			PluginManager pm = plugin.getServer().getPluginManager();
			
			/**If essentials is found, send the message via mail**/
			if (pm.isPluginEnabled("Essentials"))
			{		
				Entity killer = event.getEntity().getKiller();
				
				/**Initialize Essentials Hook**/
				IEssentials ess = null;
				Plugin essPlugin = pm.getPlugin("Essentials"); //No need for null check, we already established that it was enabled
				ess = (IEssentials) essPlugin;
				User user = ess.getUser(player); //If essentials is functioning, the player should have a user instance
				
				if (killer instanceof Player)
				{
					Player killerp = event.getEntity().getKiller();
					String killern = killerp.getName();
					String world = player.getWorld().getName();

					String mail = FormatUtil.format(plugin.getMessage("mail_pvp_format"), killern, x, y, z, world, TimeUtil.getLongDateCurr());
					user.addMail(mail);
					
					player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("death_coords_mail")));
					if (plugin.debug) plugin.outConsole(plugin.getMessage("log_death_coords"), player.getName(), "sent", "mail message");
				}
				else
				{
					String world = player.getWorld().getName();
					
					String mail = FormatUtil.format(plugin.getMessage("mail_pve_format"), x, y, z, world, TimeUtil.getLongDateCurr());
					user.addMail(mail);
					
					player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("death_coords_mail")));
					if (plugin.debug) plugin.outConsole(plugin.getMessage("log_death_coords"), player.getName(), "sent", "mail message");
				}
			}
			else
			{
				/**If not found, create a book with their death coords**/
				final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
				BookMeta meta = (BookMeta)book.getItemMeta();
				pages.add(FormatUtil.format(plugin.getMessage("book_format"), player.getName(), x, y, z));
				meta.setTitle(ChatColor.RED + "DeathCoords");
				meta.setAuthor(ChatColor.GOLD + "SwornRPG");
				meta.setPages(pages);
				book.setItemMeta(meta);
				class BookGiveTask extends BukkitRunnable
				{
					@Override
					public void run()
					{
						player.getInventory().addItem(book);
						pages.clear();
					}	
				}
				new BookGiveTask().runTaskLater(plugin, 20);
				if (plugin.debug) plugin.outConsole(plugin.getMessage("log_death_coords"), player.getName(), "given", "book");
			}
		}
    }

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) 
	{	
		Player player = event.getPlayer();
		String playerp = player.getName();
		
		/**Player Health (Join)**/
		try 
		{
			plugin.getPlayerHealthBar().updateHealth(player);
		}
		catch (NoSuchMethodException | IllegalStateException e)
		{
			plugin.outConsole(Level.SEVERE, plugin.getMessage("log_health_error"), e.getMessage());
		}
		
		/**Try to get the player's data from the cache otherwise create a new data entry**/
		PlayerData data = plugin.getPlayerDataCache().getData(playerp);
		if (data == null)
		{
			if (plugin.debug) plugin.outConsole(plugin.getMessage("log_new_data"), player.getName());
			plugin.getPlayerDataCache().newData(playerp);
			
			/**Basic data that a player needs**/
			data = plugin.getPlayerDataCache().getData(playerp);
			data.setXpneeded(100 + (data.getPlayerxp()/4));
			data.setLevel(0);
		}
		
		/**Converter to the new leveling system**/
		if (data.getXpneeded() <= 99)
		{
			data.setXpneeded(100 + (data.getPlayerxp()/4));
		}
	
		/**Makes sure Tag changes are permanent**/
		PluginManager pm = plugin.getServer().getPluginManager();
		if (pm.isPluginEnabled("TagAPI"))
		{
			String name = player.getName();
			String newName = plugin.getDefinedName(name);
			if (newName != name) 
			{
				try 
				{
					plugin.addTagChange(name, newName);
				} 
				catch (TooBigException e) 
				{
					plugin.outConsole(Level.SEVERE, plugin.getMessage("log_tag_error"), e.getMessage());
				}
			}
		}
		
		/**Update Notification**/
		if (plugin.update == true)
		{
			if (player.hasPermission("srpg.update") && (plugin.updateNeeded()))
			{
				player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("update_message")));
				player.sendMessage(FormatUtil.format("&6[SwornRPG]&e " + plugin.getMessage("update_url")));
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
		if (data.isUnlimtdammo())
		{
			data.setUnlimtdammo(false);
		}
	}
	
	/**Checks to make sure that if a player is riding another player, teleportation is not disabled**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
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
		Player player = event.getPlayer();
		plugin.getAbilitiesManager().activateSpick(player, false, action);
	}
	
	/**Frenzy!**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onFrenzyActicate(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		Player player = event.getPlayer();
		String inhand = player.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " ");
		String[] array = inhand.split(" ");
			
		if (array.length < 2)
		{
			return;
		}
			
		/**If it is not a diamond or iron tool, return**/
		if (!array[0].equals("diamond") && !array[0].equals("iron"))
		{
			return;
		}
			
		/**If it is not a shovel or pickaxe, return**/
		if (!array[1].equals("sword"))
		{
			return;
		}
		
		plugin.getAbilitiesManager().activateFrenzy(player, false, action);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		/**Player Health (Respawn)**/
		Player player = event.getPlayer();
		try 
		{
			plugin.getPlayerHealthBar().updateHealth(player);
		}
		catch (NoSuchMethodException | IllegalStateException e)
		{
			plugin.outConsole(Level.SEVERE, plugin.getMessage("log_health_error"), e.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFish(PlayerFishEvent event)
	{
		if (event.isCancelled())
			return;
		
		if (event.getCaught() == null)
			return;
		
		if (plugin.fishing == true)
		{
			/**XP Gain**/
			if (event.getCaught().getType().toString().equalsIgnoreCase("dropped_item"))
			{
				String mobname = "fish";
				String message = FormatUtil.format(plugin.prefix + plugin.getMessage("fishing_gain"), plugin.fishinggain, mobname);
				PlayerXpGainEvent xpgainevent = new PlayerXpGainEvent(event.getPlayer(), plugin.fishinggain, message);
				plugin.getServer().getPluginManager().callEvent(xpgainevent);
			}
		}
			
		/**Fish Drops**/
		Player player = event.getPlayer();
		GameMode gm = player.getGameMode();
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		Location loc = player.getLocation();
		int level = data.getLevel();
		if (level <= 10)
			level = 10;
		for (int i=0; i<data.getLevel(); i++)
		{
			if (plugin.fishDropsMap.containsKey(i))
			{
				if (gm == GameMode.SURVIVAL) 
				{ 
					for (BlockDrop fishDrop : plugin.fishDropsMap.get(i))
					{
						int r = Util.random(fishDrop.getChance());
						
						if (r == 0) 
						{
							drop(loc, fishDrop.getItem().getTypeId(), fishDrop.getItem().getData().getData());
							String name = fishDrop.getItem().toString().toLowerCase().replaceAll("_", " ");
							player.sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("fishing_drop"), name));
						}
					}
				}	
			}
		}
	}
	
	public Item drop(Location loc, int id)
	{
		return drop(loc, id, (byte)0);
	}

	public Item drop(Location loc, int id, byte type)
	{
		Item i;
		if (type > 0)
		{
			MaterialData data = new MaterialData(id);
			data.setData(type);
			ItemStack itm = data.toItemStack(1);
			i = loc.getWorld().dropItem(loc, itm);
		} 
		else
		{
			i = loc.getWorld().dropItem(loc, new ItemStack(id, 1));
		}
		return i;
	}
	
	/**Dexterity : Burst**/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event)
	{
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (plugin.checkFactions(player, false))
			return;
		
		if (player.isSneaking())
			return;
		
		GameMode gm = player.getGameMode();
		if (gm == GameMode.CREATIVE)
			return;
		
		if (player.isSprinting())
		{
			int rand = Util.random(20);
			if (rand == 0)
			{			
				player.addPotionEffect(PotionEffectType.SPEED.createEffect((int) 20, 1));
				player.sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("speed_boost")));
			}
		}
	}
}