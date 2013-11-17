package net.dmulloy2.swornrpg.types;

import org.bukkit.Material;

/**
 * @author dmulloy2
 */

public enum Ability
{
	FRENZY(Material.IRON_SWORD, Material.DIAMOND_SWORD),
	SUPER_PICKAXE(Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE),
	UNLIMITED_AMMO;

	private Material[] materials;
	
	Ability(Material... materials)
	{
		this.materials = materials;
	}

	public boolean isValidMaterial(Material mat)
	{
		if (materials == null || materials.length == 0)
			return false;

		for (Material material : materials)
		{
			if (material == mat)
				return true;
		}

		return false;
	}
}
