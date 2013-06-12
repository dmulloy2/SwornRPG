package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.BlockDrop;
import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

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
		
		Block block = event.getBlock();
		if (block == null)
			return;
		
		if (plugin.isDisabledWorld(block))
			return;
		
		try 
		{
			Material blockType = block.getType();
			Player player = event.getPlayer();
			GameMode gm = player.getGameMode();
			
			/**Iron door protection**/
			if (blockType.equals(Material.IRON_DOOR_BLOCK))
			{
				/**Config and GameMode check**/
				if (plugin.irondoorprotect && gm != GameMode.CREATIVE)
				{
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled())
			return;
			
		if (plugin.blockredemption == false)
			return;
			
		Player player = event.getPlayer();
		if (player == null)
			return;
			
		Block block = event.getBlock();
		if (block == null)
			return;
		
		if (plugin.isDisabledWorld(block))
			return;

		GameMode gm = player.getGameMode();
		if (gm == GameMode.CREATIVE)
			return;
			
		BlockState blockState =  block.getState();
		MaterialData blockData = blockState.getData();
		
		int itemId = blockState.getTypeId();
		ItemStack itemStack = new ItemStack(itemId, 1);

		if (isBlackListed(itemId))
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int level = data.getLevel();
		if (level == 0) level = 1;
		if (level > 100) level = 100;
			
		int rand = Util.random(300/level);
		if (rand == 0)
		{	
			if (blockData != null)
			{
				itemStack.setData(blockData);
			}
				
			player.getInventory().addItem(itemStack);
			
			String item = itemStack.getType().toString().toLowerCase().replaceAll("_", " ");
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("building_redeem"), item));
		}
	}
	
	/**Blacklist Check**/
	public boolean isBlackListed(Material mat)
	{
		for (String string : plugin.redeemBlacklist)
		{
			Material material = null;
			try { material = Material.getMaterial(string.toUpperCase()); }
			catch (Exception e) { material = Material.getMaterial(Integer.parseInt(string)); }

			if (material != null)
			{
				return (material == mat);
			}
		}
		
		String[] defaultBlackList = new String[]{"FIRE", "CROPS", "POTATO", "CARROT", "NETHER_WARTS", "PUMPKIN_STEM", "MELON_STEM"};
		for (String s : defaultBlackList)
		{
			Material material = null;
			try { material = Material.getMaterial(s.toUpperCase()); }
			catch (Exception e) { material = Material.getMaterial(Integer.parseInt(s)); }

			if (material != null)
			{
				return (material == mat);
			}
		}
		
		return false;
	}
	
	public boolean isBlackListed(int itemId)
	{
		Material mat = null;
		try { mat = Material.getMaterial(itemId); }
		catch (Exception e) { return false; }
		
		return (isBlackListed(mat));
	}
}