package net.dmulloy2.swornrpg.types;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.bukkit.inventory.ItemStack;

/**
 * @author dmulloy2
 */

@Data
@AllArgsConstructor
public class BlockDrop
{
	private final ItemStack item;
	private final int chance;
}