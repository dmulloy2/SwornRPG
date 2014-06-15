package net.dmulloy2.swornrpg.commands;

import java.util.logging.Level;
import java.util.regex.Pattern;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;

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
		this.requiredArgs.add("color");
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

		if (args.length == 1)
		{
			String color = args[0];
			if (! isValidTag(color))
			{
				invalidArgs();
				return;
			}

			if (! color.startsWith("&"))
				color = "&" + color;

			plugin.getTagHandler().setTag(player, color + sender.getName());

			sendpMessage(plugin.getMessage("tag_changed_self"), getFormattedColor(color));
		}
		else if (args.length == 2)
		{
			if (! hasPermission(Permission.TAG_OTHERS) && ! player.getName().equalsIgnoreCase(args[0]))
			{
				err(plugin.getMessage("insufficient_permissions"), getPermissionString(Permission.TAG_OTHERS));
				plugin.getLogHandler().log(Level.WARNING, getMessage("log_denied_access"), sender.getName());
				return;
			}

			String color = args[1];
			if (! isValidTag(color))
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

			if (! color.startsWith("&"))
				color = "&" + color;

			String newTag = args[1] + target.getName();
			if (newTag.length() > 16)
			{
				err(getMessage("username_too_large"));
				return;
			}

			plugin.getTagHandler().setTag(target, newTag);

			sendpMessage(plugin.getMessage("tag_changed_changer"), target.getName(), getFormattedColor(color));
			sendpMessage(target, plugin.getMessage("tag_changed_changed"), getFormattedColor(color));
		}
	}

	private final boolean isValidTag(String tag)
	{
		if (tag.length() == 2 && tag.contains("&"))
		{
			if (tag.contains("&"))
			{
				return Pattern.matches("[a-fA-F0-9]", tag.replaceAll("&", ""));
			}
		}
		else if (tag.length() == 1)
		{
			return Pattern.matches("[a-fA-F0-9]", tag);
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