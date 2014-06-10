/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdLore extends SwornRPGCommand
{
	public CmdLore(SwornRPG plugin)
	{
		super(plugin);
		this.name = "lore";
		this.requiredArgs.add("lore");
		this.description = "Set the lore of your in-hand item";
		this.permission = Permission.LORE;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		ItemStack inHand = player.getItemInHand();
		if (inHand == null || inHand.getType() == Material.AIR)
		{
			err("You must have an item in your hand to do this!");
			return;
		}

		// Join and fix spacing
		String str = FormatUtil.join(" ", args);
		str = str.replaceAll("_", " ");
		str = str.replaceAll("\"", "");

		String[] split = str.split("\\|");
		List<String> lore = new ArrayList<>();
		for (String line : split)
			lore.add(FormatUtil.format(line));

		// Apply
		ItemMeta meta = inHand.getItemMeta();
		meta.setLore(lore);
		inHand.setItemMeta(meta);

		sendpMessage("&eYou have set your &b{0}&e''s lore to \"&r{1}&e\"", FormatUtil.getFriendlyName(inHand.getType()), lore);
	}
}