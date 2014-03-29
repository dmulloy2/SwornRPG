package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author dmulloy2
 */

public class CmdItemName extends SwornRPGCommand
{
	public CmdItemName(SwornRPG plugin)
	{
		super(plugin);
		this.name = "itemname";
		this.aliases.add("iname");
		this.requiredArgs.add("name");
		this.description = "Set the name of your in-hand item";
		this.permission = Permission.ITEMNAME;

		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
	    ItemStack hand = player.getItemInHand();
	    if (hand == null || hand.getType() == Material.AIR)
	    {
	    	sendpMessage(plugin.getMessage("hand_empty"));
	    	return;
	    }

		String name = FormatUtil.join(" ", args);

	    ItemMeta meta = hand.getItemMeta();
	    meta.setDisplayName(FormatUtil.format(name.toString()));
	    hand.setItemMeta(meta);
	    	
	    String inhand = FormatUtil.getFriendlyName(hand.getType());
	    sendpMessage(plugin.getMessage("item_name"), inhand, name);
	}
}