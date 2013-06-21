package net.dmulloy2.swornrpg;

import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

public class BlockDrop
{
	final ItemStack item;
	final int chance;
	
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
	
	public String toString()
	{
		return "[" + FormatUtil.getFriendlyName(item.getType()) + ", " + chance + "]";
	}
}