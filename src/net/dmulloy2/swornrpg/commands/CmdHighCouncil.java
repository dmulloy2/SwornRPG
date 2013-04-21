package net.dmulloy2.swornrpg.commands;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.PermissionType;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class CmdHighCouncil extends SwornRPGCommand
{
	public CmdHighCouncil (SwornRPG plugin)
	{
		super(plugin);
		this.name = "hc";
		this.description = "Talk in council chat";
		this.aliases.add("council");
		this.requiredArgs.add("message");
		this.permission = PermissionType.CMD_COUNCIL.permission;
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		int amt = args.length;
		String str = "";
		for (int i = 0; i < amt; i++)
		{
			str = str + args[i] + " ";
		}
		String sname;
		if (sender instanceof Player)
			sname = sender.getName();
		else
			sname = "Console";
		Bukkit.getServer().broadcast(FormatUtil.format(plugin.getMessage("council"), sname, str), "srpg.council");
	}
}