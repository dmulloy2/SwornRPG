package net.dmulloy2.swornrpg.handlers;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.permissions.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class PermissionHandler 
{
	public PermissionHandler(SwornRPG plugin)
	{
	}
	
	public boolean hasPermission(CommandSender sender, Permission permission) 
	{
		return (permission == null) ? true : hasPermission(sender, getPermissionString(permission));
	}

	public boolean hasPermission(CommandSender sender, String permission) 
	{
		if (sender instanceof Player) 
		{
			Player p = (Player) sender;
			return (p.hasPermission(permission) || p.isOp());
		}
		
		return true;
	}
	
	private String getPermissionString(Permission permission) 
	{
		return "srpg." + permission.getNode().toLowerCase();
	}

}