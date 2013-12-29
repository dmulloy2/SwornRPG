package net.dmulloy2.swornrpg.commands;

import java.util.regex.Pattern;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdTag extends SwornRPGCommand
{
	public CmdTag(SwornRPG plugin)
	{
		super(plugin);
		this.name = "tag";
		this.requiredArgs.add("tag");
		this.optionalArgs.add("player");
		this.description = "Change the color above your head";
		this.permission = Permission.TAG;

		this.mustBePlayer = true;
	}

	@Override
	public void perform()
	{
		if (! plugin.getPluginManager().isPluginEnabled("TagAPI"))
		{
			err(plugin.getMessage("plugin_not_found"), "TagAPI");
			return;
		}

		if (args.length == 2)
		{
			if (! hasPermission(Permission.TAG_OTHERS))
			{
				sendMessage(plugin.getMessage("noperm"));
				return;
			}

			if (! isValidTag(args[1]))
			{
				invalidArgs();
				return;
			}

			Player target = Util.matchPlayer(args[0]);
			if (target == null)
			{
				err(getMessage("player_not_found"), args[0]);
				return;
			}

			String newTag = args[1] + target.getName();
			String color = getFormattedColor(args[1]);

			plugin.getTagHandler().addTagChange(target, newTag);

			sendpMessage(plugin.getMessage("tag_changed_changer"), color);
			sendMessageTarget(plugin.getMessage("tag_changed_changed"), target, color);
		}
		else if (args.length == 1)
		{
			if (! isValidTag(args[0]))
			{
				invalidArgs();
				return;
			}

			plugin.getTagHandler().addTagChange(player, args[0] + sender.getName());

			sendpMessage(plugin.getMessage("tag_changed_self"), getFormattedColor(args[0]));
		}
	}

	private final boolean isValidTag(String tag)
	{
		if (tag.length() == 2)
		{
			if (tag.contains("&"))
			{
				return Pattern.matches("[a-zA-Z0-9]", tag.replaceAll("&", ""));
			}
		}

		return false;
	}

	private final String getFormattedColor(String colorCode)
	{
		colorCode = colorCode.replaceAll("&", "");
		ChatColor color = ChatColor.getByChar(colorCode);
		return color + FormatUtil.getFriendlyName(color.name());
	}
}