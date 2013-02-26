package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.util.InventoryHelper;
import net.dmulloy2.swornrpg.util.TooBigException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 * @contributor Minesworn
 */

public class PlayerListener implements Listener 
{

	private SwornRPG plugin;
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		try 
		{
			String msg = event.getMessage();
			Player player = event.getPlayer();
			if (this.plugin.isAdminChatting(player.getName()))
			{
				this.plugin.sendAdminMessage(player.getName(), msg);
				event.setCancelled(true);
			}
			if (this.plugin.isCouncilChatting(player.getName()))
			{
				this.plugin.sendCouncilMessage(player.getName(), msg);
				event.setCancelled(true);
			}
		}
		catch (Exception localException)
		{
		}	
	}
	
    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) 
    {
        final String oldName = event.getPlayer().getName();
        final String newName = this.plugin.getDefinedName(oldName);
        if (!newName.equals(oldName))
        {
            try 
            {
                this.plugin.addTagChange(oldName, newName);
            } 
            catch (final TooBigException e) 
            {
                this.plugin.getLogger().severe("Error while changing name from memory:");
                this.plugin.getLogger().severe(e.getMessage());
            }
        }
    }
}
