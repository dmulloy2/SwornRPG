/**
 * (c) 2014 dmulloy2
 */
package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;

import org.apache.commons.lang.NotImplementedException;

/**
 * @author dmulloy2
 */

public class CmdMeta extends SwornRPGCommand
{
	public CmdMeta(SwornRPG plugin)
	{
		super(plugin);
		this.name = "meta";
		this.aliases.add("itemmeta");
		this.optionalArgs.add("type");
		this.optionalArgs.add("args");
		this.description = "Modify an item's item meta";
		this.permission = Permission.META;
		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		throw new NotImplementedException("Not implemented!"); // TODO

//		// Hand check
//		ItemStack hand = player.getItemInHand();
//		if (hand == null || hand.getType() == Material.AIR)
//		{
//			sendpMessage(plugin.getMessage("hand_empty"));
//			return;
//		}
//
//		if (args.length == 0)
//		{
//			printMetaInfo(hand);
//			return;
//		}
//
//		// TODO: Actual item meta-y stuff :P
//		// String type = args[0];
//
//		String name = FormatUtil.join(" ", args);
//
//		ItemMeta meta = hand.getItemMeta();
//		meta.setDisplayName(FormatUtil.format(name.toString()));
//		hand.setItemMeta(meta);
//
//		String inhand = FormatUtil.getFriendlyName(hand.getType());
//		sendpMessage(plugin.getMessage("item_name"), inhand, name);
	}
//
//	private final void printMetaInfo(ItemStack item)
//	{
//		ItemMeta meta = item.getItemMeta();
//		if (meta == null)
//		{
//			err("This item does not have any meta!");
//			return;
//		}
//
//		if (unsupportedTypes.contains(meta.getClass()))
//		{
//			err("This item is currently not supported!");
//			return;
//		}
//
//		List<String> lines = new ArrayList<String>();
//
//		StringBuilder line = new StringBuilder();
//		line.append(FormatUtil.format("&3====[ &e{0} &3]====", FormatUtil.getFriendlyName(item.getType())));
//		lines.add(line.toString());
//
//		if (item.getType().getMaxDurability() != 0)
//		{
//			line = new StringBuilder();
//			line.append(FormatUtil.format("&eDurability: &b{0}", item.getDurability()));
//			lines.add(line.toString());
//		}
//
//		Map<Enchantment, Integer> enchantments = item.getEnchantments();
//		if (! enchantments.isEmpty())
//		{
//			line = new StringBuilder();
//			line.append(FormatUtil.format("&eEnchantments: &b{0}", EnchantmentType.toString(enchantments)));
//			lines.add(line.toString());
//		}
//
//		// TODO: Do something with material data?
//		// TODO: Meta specific operations
//	}
//
//	private static List<Class<? extends ItemMeta>> unsupportedTypes;
//
//	static
//	{
//		unsupportedTypes.add(FireworkEffectMeta.class);
//		unsupportedTypes.add(FireworkMeta.class);
//	}
}