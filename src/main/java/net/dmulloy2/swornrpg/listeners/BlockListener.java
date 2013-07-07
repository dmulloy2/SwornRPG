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
		if (! plugin.randomdrops || event.isCancelled())
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
		
		if (plugin.blockDropsMap.containsKey(typeId))
		{
			for (BlockDrop blockDrop : plugin.blockDropsMap.get(typeId))
			{
				if (Util.random(blockDrop.getChance()) == 0)
				{
					block.getWorld().dropItemNaturally(block.getLocation(), blockDrop.getItem());
				}
			}
				
			if (plugin.blockDropsMap.containsKey(0))
			{
				for (BlockDrop blockDrop : plugin.blockDropsMap.get(0)) 
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
		if (! plugin.irondoorprotect)
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
				blockData.setData(blockState.getData().getData());
				itemStack.setData(blockData);
			}
				
			player.getInventory().addItem(itemStack);
			
			String itemName = FormatUtil.getFriendlyName(itemStack.getType());
			player.sendMessage(plugin.prefix + FormatUtil.format(plugin.getMessage("building_redeem"), itemName));
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