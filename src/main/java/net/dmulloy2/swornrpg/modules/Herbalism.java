/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;
import org.bukkit.material.Tree;

/**
 * @author dmulloy2
 */

public class Herbalism extends Module
{
	private int xpGain;

	public Herbalism(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("levelingMethods.herbalism.enabled", true));
		this.xpGain = plugin.getConfig().getInt("levelingMethods.herbalism.xpgain");
	}

	// Herbalism gain
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (plugin.isDisabledWorld(player))
			return;

		if (isApplicable(event.getBlock()))
		{
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			int herbalism = data.getHerbalism();
			if (herbalism >= 10)
			{
				int xp = xpGain * 10;
				String message = FormatUtil.format(plugin.getPrefix() + plugin.getMessage("herbalism_gain"), xp);
				plugin.getExperienceHandler().handleXpGain(player, xp, message);
				data.setHerbalism(0);
			}
			else
			{
				data.setHerbalism(herbalism + 1);
			}
		}
	}

	// Instant growth
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (plugin.isDisabledWorld(player))
			return;

		Block block = event.getBlock();
		if (! isGrowable(block))
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		int level = data.getLevel(150);
		if (Util.random(200 - level) == 0)
		{
			boolean message = false;
			BlockState blockState = block.getState();
			Material mat = blockState.getType();
			MaterialData dat = blockState.getData();
			if (dat instanceof NetherWarts)
			{
				((NetherWarts) dat).setState(NetherWartsState.RIPE);
				blockState.update();
				message = true;
			}
			else if (dat instanceof Crops)
			{
				((Crops) dat).setState(CropState.RIPE);
				blockState.update();
				message = true;
			}
			else if (dat instanceof CocoaPlant)
			{
				((CocoaPlant) dat).setSize(CocoaPlantSize.LARGE);
				blockState.update();
				message = true;
			}
			else if (mat == Material.SAPLING)
			{
				Tree tree = (Tree) block.getState().getData();
				TreeSpecies species = tree.getSpecies();
				TreeType type = TreeType.TREE;
				switch (species)
				{
					case ACACIA:
						type = TreeType.ACACIA;
						break;
					case BIRCH:
						type = TreeType.BIRCH;
						break;
					case DARK_OAK:
						type = TreeType.DARK_OAK;
						break;
					case GENERIC:
						type = TreeType.TREE;
						break;
					case JUNGLE:
						type = TreeType.JUNGLE;
						break;
					case REDWOOD:
						type = TreeType.REDWOOD;
						break;
				}

				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), type);
				message = true;
			}
			else if (mat == Material.RED_MUSHROOM)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.RED_MUSHROOM);
				message = true;
			}
			else if (mat == Material.BROWN_MUSHROOM)
			{
				block.setType(Material.AIR);
				block.getWorld().generateTree(block.getLocation(), TreeType.BROWN_MUSHROOM);
				message = true;
			}

			if (message)
				player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("insta_growth")));
		}
	}

	private boolean isApplicable(Block block)
	{
		BlockState state = block.getState();
		switch (state.getType())
		{
			case CACTUS:
			case MELON_BLOCK:
			case PUMPKIN:
				return true;
			case CROPS:
				return ((Crops) state.getData()).getState() == CropState.RIPE;
			case NETHER_WARTS:
				return ((NetherWarts) state.getData()).getState() == NetherWartsState.RIPE;
			case COCOA:
				return ((CocoaPlant) state.getData()).getSize() == CocoaPlantSize.LARGE;
			default:
				return false;
		}
	}

	private boolean isGrowable(Block block)
	{
		BlockState state = block.getState();
		Material material = block.getType();
		MaterialData data = state.getData();

		return data instanceof NetherWarts || data instanceof Crops || data instanceof CocoaPlant
				|| material == Material.SAPLING || material == Material.RED_MUSHROOM || material == Material.BROWN_MUSHROOM;
	}
}