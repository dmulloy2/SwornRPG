package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdHighCouncil extends SwornRPGCommand
{
	public CmdHighCouncil(SwornRPG plugin)
	{
		super(plugin);
		this.name = "hc";
		this.description = "Talk in council chat";
		this.aliases.add("council");
		this.requiredArgs.add("message");
		this.permission = Permission.CMD_COUNCIL;
		
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
		
		String node = plugin.getPermissionHandler().getPermissionString(permission);
		plugin.getServer().broadcast(FormatUtil.format(plugin.getMessage("council"), sname, message.toString()), node);
	}
}