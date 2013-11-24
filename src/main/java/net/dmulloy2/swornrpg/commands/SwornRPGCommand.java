package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornrpg.util.FormatUtil;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public abstract class SwornRPGCommand implements CommandExecutor
{
	protected final SwornRPG plugin;

	protected CommandSender sender;
	protected Player player;
	protected String args[];

	protected String name;
	protected String description;

	protected Permission permission;

	protected boolean mustBePlayer;
	protected List<String> requiredArgs;
	protected List<String> optionalArgs;
	protected List<String> aliases;

	protected boolean usesPrefix;

	public SwornRPGCommand(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.requiredArgs = new ArrayList<String>(2);
		this.optionalArgs = new ArrayList<String>(2);
		this.aliases = new ArrayList<String>(2);
	}

	public abstract void perform();

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		execute(sender, args);
		return true;
	}

	public final void execute(CommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
		if (sender instanceof Player)
			player = (Player) sender;

		if (mustBePlayer && ! isPlayer())
		{
			err(plugin.getMessage("mustbeplayer"));
			return;
		}

		if (requiredArgs.size() > args.length)
		{
			invalidArgs();
			return;
		}

		if (! hasPermission())
		{
			err(plugin.getMessage("noperm"));
			return;
		}

		try
		{
			perform();
		}
		catch (Throwable e)
		{
			err("Error executing command: {0}", e.getMessage());
			plugin.getLogHandler().debug(Util.getUsefulStack(e, "executing command " + name));
		}
	}

	protected final boolean isPlayer()
	{
		return player != null;
	}

	private final boolean hasPermission()
	{
		return plugin.getPermissionHandler().hasPermission(sender, permission);
	}

	protected final void err(String msg, Object... args)
	{
		sendMessage(getMessage("error") + FormatUtil.format(msg, args));
	}

	protected PlayerData getPlayerData(OfflinePlayer target)
	{
		return plugin.getPlayerDataCache().getData(target.getName());
	}

	protected PlayerData getPlayerData(String key)
	{
		return plugin.getPlayerDataCache().getData(key);
	}

	protected final void sendMessage(String msg, Object... args)
	{
		sender.sendMessage(FormatUtil.format(msg, args));
	}

	protected final void sendpMessage(String msg, Object... args)
	{
		sender.sendMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}

	protected final void sendMessageAll(String msg, Object... args)
	{
		plugin.getServer().broadcastMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}

	protected final void sendMessageTarget(String msg, Player target, Object... args)
	{
		target.sendMessage(plugin.getPrefix() + FormatUtil.format(msg, args));
	}

	protected final String getMessage(String msg)
	{
		return plugin.getMessage(msg);
	}

	public final String getName()
	{
		return name;
	}

	public final List<String> getAliases()
	{
		return aliases;
	}

	public final String getUsageTemplate(final boolean displayHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("&b/");

		if (plugin.getCommandHandler().usesCommandPrefix() && usesPrefix)
			ret.append(plugin.getCommandHandler().getCommandPrefix() + " ");

		ret.append(name);

		for (String s : optionalArgs)
			ret.append(String.format(" &3[%s]", s));

		for (String s : requiredArgs)
			ret.append(String.format(" &3<%s>", s));

		if (displayHelp)
			ret.append(" &e" + description);

		return FormatUtil.format(ret.toString());
	}

	protected final boolean argMatchesAlias(String arg, String... aliases)
	{
		for (String s : aliases)
		{
			if (arg.equalsIgnoreCase(s))
				return true;
		}

		return false;
	}

	protected int argAsInt(int arg, boolean msg)
	{
		try
		{
			return Integer.valueOf(args[arg]);
		}
		catch (NumberFormatException ex)
		{
			if (msg)
				invalidArgs();
			return -1;
		}
	}

	protected double argAsDouble(int arg, boolean msg)
	{
		try
		{
			return Double.valueOf(args[arg]);
		}
		catch (NumberFormatException ex)
		{
			if (msg) 
				invalidArgs();
			return -1;
		}
	}

	protected final void invalidArgs()
	{
		err(plugin.getMessage("invalidargs") + " " + getUsageTemplate(false));
	}

	protected final OfflinePlayer getTarget(int arg, boolean others)
	{
		OfflinePlayer target = null;
		if (args.length == 1 && others)
		{
			target = Util.matchPlayer(args[arg]);
			if (target == null)
			{
				target = Util.matchOfflinePlayer(args[arg]);
				if (target == null)
				{
					err(plugin.getMessage("noplayer"));
					return null;
				}
			}
		}
		else
		{
			if (sender instanceof Player)
			{
				target = player;
			}
			else
			{
				err(plugin.getMessage("console_level"));
				return null;
			}
		}

		return target;
	}

	protected final boolean hasPermission(Permission permission)
	{
		return plugin.getPermissionHandler().hasPermission(sender, permission);
	}
}