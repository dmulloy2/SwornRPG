package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdAChat extends SwornRPGCommand
{
	public CmdAChat (SwornRPG plugin)
	{
		super(plugin);
		this.name = "a";
		this.description = "Talk in admin-only chat";
		this.aliases.add("achat");
		this.requiredArgs.add("message");
		this.permission = PermissionType.CMD_ACHAT.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		StringBuilder message = new StringBuilder();
		for (int i=0; i<args.length; i++)
		{
			message.append(args[i] + " ");
		}
		
		String sname;
		if (sender instanceof Player)
			sname = sender.getName();
		else
			sname = "Console";
		
		String node = PermissionType.CMD_ACHAT.permission.getNode();
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("achat"), sname, message.toString()), node);
	}
}