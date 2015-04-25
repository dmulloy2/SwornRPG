/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.swornrpg.modules;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.InventoryUtil;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author dmulloy2
 */

public class Salvaging extends Module
{
	public Salvaging(SwornRPG plugin)
	{
		super(plugin);
	}

	@Override
	public void loadSettings()
	{
		setEnabled(plugin.getConfig().getBoolean("salvaging", true));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Block block = event.getClickedBlock();
		if (block == null || event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() != GameMode.SURVIVAL)
			return;

		if (plugin.isDisabledWorld(block))
			return;

		String blockType = "";
		if (block.getType() == Material.IRON_BLOCK)
			blockType = "Iron";
		if (block.getType() == Material.GOLD_BLOCK)
			blockType = "Gold";
		if (block.getType() == Material.DIAMOND_BLOCK)
			blockType = "Diamond";

		if (! blockType.isEmpty())
		{
			if (block.getRelative(-1, 0, 0).getType() == Material.FURNACE
					|| block.getRelative(1, 0, 0).getType() == Material.FURNACE
					|| block.getRelative(0, 0, -1).getType() == Material.FURNACE
					|| block.getRelative(0, 0, 1).getType() == Material.FURNACE)
			{
				ItemStack item = player.getItemInHand();
				Material type = item.getType();

				double mult = 1.0D - ((double) item.getDurability() / item.getType().getMaxDurability());
				double amt = 0.0D;

				if (plugin.getSalvageRef().get(blockType.toLowerCase()).containsKey(type))
					amt = Math.round(plugin.getSalvageRef().get(blockType.toLowerCase()).get(type) * mult);

				if (amt > 0.0D)
				{
					String article = FormatUtil.getArticle(blockType);
					String materialExtension = blockType.equals("Diamond") ? "" : " ingot";
					String plural = amt > 1.0D ? "s" : "";
					String itemName = FormatUtil.getFriendlyName(item.getType());

					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("salvage_success"),
							article, itemName, amt, blockType.toLowerCase(), materialExtension, plural));

					plugin.log(plugin.getMessage("log_salvage"), player.getName(), itemName, amt, blockType.toLowerCase(),
							materialExtension, plural);

					PlayerInventory inv = player.getInventory();
					inv.removeItem(item);

					Material give = null;
					if (blockType == "Iron")
						give = Material.IRON_INGOT;
					if (blockType == "Gold")
						give = Material.GOLD_INGOT;
					if (blockType == "Diamond")
						give = Material.DIAMOND;

					ItemStack salvaged = new ItemStack(give, (int) amt);
					InventoryUtil.giveItem(player, salvaged);
					event.setCancelled(true);
				}
				else
				{
					String itemName = FormatUtil.getFriendlyName(item.getType());
					player.sendMessage(plugin.getPrefix() + FormatUtil.format(plugin.getMessage("not_salvagable"), itemName,
							blockType.toLowerCase()));
				}
			}
		}
	}
}