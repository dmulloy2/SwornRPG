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
	private final SwornRPG plugin;
	public BlockListener(final SwornRPG plugin)
	{
		this.plugin = plugin;
	}
	
	/** Block Drops **/
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakMonitor(BlockBreakEvent event)
	{
		if (! plugin.isRandomdrops() || event.isCancelled())
			return;
		
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (plugin.checkFactions(player, true))
			return;
		
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		
		Block block = event.getBlock();
		int typeId = block.getTypeId();
		
		if (plugin.getBlockDropsMap().containsKey(typeId))
		{
			for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(typeId))
			{
				if (Util.random(blockDrop.getChance()) == 0)
				{
					block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getItem());
				}
			}
				
			if (plugin.getBlockDropsMap().containsKey(0))
			{
				for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(0)) 
				{
					if (Util.random(blockDrop.getChance()) == 0)
					{
						block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getItem());
					}
				}
			}
		}		
	}
	
	/** Iron Door Protection **/
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakHighest(BlockBreakEvent event)
	{
		if (! plugin.isIrondoorprotect())
			return;
		
		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		
		if (event.getBlock().getType() == Material.IRON_DOOR_BLOCK)
		{
			event.setCancelled(true);
			
			player.sendMessage(FormatUtil.format(plugin.prefix + plugin.getMessage("iron_door_protect")));
			plugin.debug(plugin.getMessage("log_irondoor_protect"), player.getName());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled() || ! plugin.isBlockredemption())
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		Player player = event.getPlayer();
		GameMode gm = player.getGameMode();
		if (gm == GameMode.CREATIVE)
			return;
			
		BlockState blockState =  block.getState();
		MaterialData blockData = blockState.getData();
		
		Material material = blockState.getType();
		if (isBlacklistedMaterial(material))
			return;
		
		ItemStack itemStack = new ItemStack(material);

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int level = data.getLevel();
		if (level == 0) level = 1;
		if (level > 100) level = 100;
			
		int rand = Util.random(300/level);
		if (rand == 0)
		{	
			if (blockData != null)
			{
				blockData.setData(blockState.getData().getData());
				itemStack.setData(blockData);
			}
				
			player.getInventory().addItem(itemStack);
			
			String itemName = FormatUtil.getFriendlyName(itemStack.getType());
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("building_redeem"), itemName));
		}
	}
	
	public boolean isBlacklistedMaterial(Material mat)
	{
		for (String string : plugin.getRedeemBlacklist())
		{
			Material material = null;
			
			try 
			{
				int id = Integer.parseInt(string);
				material = Material.getMaterial(id);
			}
			catch (NumberFormatException ex)
			{
				material = Material.valueOf(string);
			}
			
			if (material != null)
			{
				if (material == mat)
					return true;
			}
		}
			
		String[] defaultBlackList = new String[]{"FIRE", "CROPS", "POTATO", "CARROT", "NETHER_WARTS", "PUMPKIN_STEM", "MELON_STEM"};
		for (String string : defaultBlackList)
		{
			Material material = Material.valueOf(string);
			if (material != null)
			{
				if (material.equals(mat))
					return true;
			}
		}
		
		return false;
	}
}