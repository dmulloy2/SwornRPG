package net.dmulloy2.swornrpg.types;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dmulloy2.types.MyMaterial;

/**
 * @author dmulloy2
 */

@Data
@AllArgsConstructor
public class BlockDrop
{
	private final MyMaterial material;
	private final int chance;
}