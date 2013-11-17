package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdAdminChat extends SwornRPGCommand
{
	public CmdAdminChat(SwornRPG plugin)
	{
		super(plugin);
		this.name = "a";
		this.aliases.add("adminchat");
		this.requiredArgs.add("message");
		this.description = "Talk in admin-only chat";
		this.permission = Permission.ADMINCHAT;
	}

	@Override
	public void perform()
	{
		StringBuilder message = new StringBuilder();
		for (String arg : args)
		{
			message.append(arg + " ");
		}

		if (message.lastIndexOf(" ") >= 0)
		{
			message.deleteCharAt(message.lastIndexOf(" "));
		}

		String name = sender instanceof Player ? player.getName() : "Console";
		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("achat"), name, message.toString()), node);
	}
}