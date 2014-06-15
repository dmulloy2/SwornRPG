/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdName extends SwornRPGCommand
{
	public CmdName(SwornRPG plugin)
	{
		super(plugin);
		this.name = "name";
		this.aliases.add("iname");
		this.requiredArgs.add("name");
		this.description = "Set the name of your in-hand item";
		this.permission = Permission.NAME;
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
		String name = FormatUtil.join(" ", args);
		name = name.replaceAll("_", " ");
		name = name.replaceAll("\"", "");

		// Format
		name = FormatUtil.format(name);

		// Apply
		ItemMeta meta = inHand.getItemMeta();
		meta.setDisplayName(name);
		inHand.setItemMeta(meta);

		sendpMessage("&eYou have set your &b{0}&e''s name to \"&r{1}&e\"", FormatUtil.getFriendlyName(inHand.getType()), name);
	}
}