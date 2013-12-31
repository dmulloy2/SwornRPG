package net.dmulloy2.swornrpg.types;

import lombok.Getter;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
public class BlockDrop
{
	private final ItemStack item;
	private final int chance;
	
	public BlockDrop(ItemStack itemStack, int chance) 
	{
		this.item = itemStack;
		this.chance = chance;
	}

	@Override
	public String toString()
	{
		return "[" + FormatUtil.getFriendlyName(item.getType()) + ", " + chance + "]";
	}
}