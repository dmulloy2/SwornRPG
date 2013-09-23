package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.types.BlockDrop;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.MaterialUtil;
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
		if (plugin.checkFactions(player, true))
			return;

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();

		Material type = block.getType();

		if (plugin.getBlockDropsMap().containsKey(type))
		{
			for (BlockDrop blockDrop : plugin.getBlockDropsMap().get(type))
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
		if (! plugin.isIrondoorprotect() || event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (event.getBlock().getType() == Material.IRON_DOOR_BLOCK)
		{
			event.setCancelled(true);

			player.sendMessage(FormatUtil.format(plugin.getPrefix() + plugin.getMessage("iron_door_protect")));
			plugin.debug(plugin.getMessage("log_irondoor_protect"), player.getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (event.isCancelled() || !plugin.isBlockredemption())
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		Player player = event.getPlayer();
		GameMode gm = player.getGameMode();
		if (gm == GameMode.CREATIVE)
			return;

		BlockState blockState = block.getState();
		MaterialData blockData = blockState.getData();

		Material material = blockState.getType();
		if (isBlacklistedMaterial(material))
			return;

		ItemStack itemStack = new ItemStack(material);

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		int level = data.getLevel();
		if (level == 0)
			level = 1;
		if (level > 100)
			level = 100;

		int rand = Util.random(300 / level);
		if (rand == 0)
		{
			if (blockData != null)
			{
				itemStack.setData(blockData);
			}

			player.getInventory().addItem(itemStack);

			String itemName = FormatUtil.getFriendlyName(itemStack.getType());
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("building_redeem"), itemName));
		}
	}

	public boolean isBlacklistedMaterial(Material mat)
	{
		for (String string : plugin.getRedeemBlacklist())
		{
			Material material = MaterialUtil.getMaterial(string);

			if (material != null)
			{
				if (material == mat)
					return true;
			}
		}

		String[] defaultBlackList = new String[] { "FIRE", "CROPS", "POTATO", "CARROT", "NETHER_WARTS", "PUMPKIN_STEM", "MELON_STEM" };
		for (String string : defaultBlackList)
		{
			if (MaterialUtil.isValidMaterial(string))
			{
				Material material = Material.matchMaterial(string);
				if (material.equals(mat))
					return true;
			}
		}

		return false;
	}
}