package net.dmulloy2.swornrpg.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Getter
@AllArgsConstructor
public class BlockDrop
{
	private final ItemStack item;
	private final int chance;

	@Override
	public String toString()
	{
		return "[" + FormatUtil.getFriendlyName(item.getType()) + ", " + chance + "]";
	}
}