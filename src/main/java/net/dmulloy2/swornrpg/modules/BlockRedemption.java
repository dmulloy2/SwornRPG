/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;
import net.dmulloy2.util.MaterialUtil;
import net.dmulloy2.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author dmulloy2
 */

public class BlockRedemption extends Module
{
	private List<Material> redemptionBlacklist;

	public BlockRedemption(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("redemptionEnabled", true));
		this.redemptionBlacklist = MaterialUtil.fromStrings(plugin.getConfig().getStringList("redemptionBlacklist"));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		Block block = event.getBlock();
		if (plugin.isDisabledWorld(block))
			return;

		Material material = block.getType();
		if (! material.isBlock() || redemptionBlacklist.contains(material))
			return;

		PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Block redemption
		int level = data.getLevel(100);
		if (Util.random(300 / level) == 0)
		{
			ItemStack itemStack = new ItemStack(material);
			MaterialData materialData = block.getState().getData();
			if (materialData != null)
				itemStack.setData(materialData);

			InventoryUtil.giveItem(player, itemStack);

			String itemName = FormatUtil.getFriendlyName(itemStack.getType());
			player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("building_redeem"), itemName));
		}
	}
}