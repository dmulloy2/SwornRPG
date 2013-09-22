package net.dmulloy2.swornrpg.types;

import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class BlockDrop
{
	private final ItemStack item;
	private final int chance;
	
	public BlockDrop(final ItemStack itemStack, final int chance) 
	{
		this.item = itemStack;
		this.chance = chance;
	}
	
	public ItemStack getItem()
	{
		return item;
	}
	
	public int getChance()
	{
		return chance;
	}
	
	@Override
	public String toString()
	{
		return "[" + FormatUtil.getFriendlyName(item.getType()) + ", " + chance + "]";
	}
}