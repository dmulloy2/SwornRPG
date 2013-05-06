package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.BlockDrop;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
	
public class BlockListener implements Listener 
{
	
	public SwornRPG plugin;
	
	public BlockListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	public Item drop(Block block, int id)
	{
		return drop(block, id, (byte)0);
	}

	public Item drop(Block block, int id, byte type)
	{
		Item i;
		if (type > 0)
		{
			MaterialData data = new MaterialData(id);
			data.setData(type);
			ItemStack itm = data.toItemStack(1);
			i = block.getWorld().dropItem(block.getLocation(), itm);
		} 
		else
		{
			i = block.getWorld().dropItem(block.getLocation(), new ItemStack(id, 1));
		}
		return i;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) 
	{
		if (event.isCancelled())
			return;
		
		try 
		{
			Block block = event.getBlock();
			Material blockType = block.getType();
			Player player = event.getPlayer();
			GameMode gm = player.getGameMode();
			
			/**Iron door protection**/
			if (blockType.equals(Material.IRON_DOOR_BLOCK))
			{
				/**Config and GameMode check**/
				if ((plugin.irondoorprotect == true) && (gm != (GameMode.CREATIVE)))
				{
					/**Protect the iron door!**/
					event.setCancelled(true);
					player.sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("iron_door_protect")));
					if (plugin.debug) plugin.outConsole(plugin.getMessage("log_irondoor_protect"), player.getName());
				}
			}
			
			/**Cancels block breaking if the player is riding or sitting**/
			Entity vehicle = player.getVehicle();
			PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
			if (data.isSitting() && vehicle != null)
			{
				event.setCancelled(true);
				player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("chairblockdeny"), "&c/standup"));
			}
			if (data.isRiding() && vehicle != null)
			{
				event.setCancelled(true);
			}
			
			/**Rare Drops**/
			if (plugin.blockDropsMap.containsKey(blockType.getId()))
			{
				if (plugin.randomdrops && gm == GameMode.SURVIVAL) 
				{
					for (BlockDrop blockDrop : plugin.blockDropsMap.get(blockType.getId()))
					{
						int r = Util.random(blockDrop.getChance());
						
						if (r == 0) drop(block, blockDrop.getItem().getTypeId(), blockDrop.getItem().getData().getData());
					}
					
					if (plugin.blockDropsMap.containsKey(0))
					{
						for (BlockDrop blockDrop : plugin.blockDropsMap.get(0)) 
						{
							int r = Util.random(blockDrop.getChance());
							
							if (r == 0) drop(block, blockDrop.getItem().getTypeId(), blockDrop.getItem().getData().getData());
						}
					}
				}
				
			}
		}
		catch (Exception localException)
		{
			if (plugin.debug) plugin.outConsole(plugin.getMessage("log_error_block"), localException.getMessage());
		}
	}
}