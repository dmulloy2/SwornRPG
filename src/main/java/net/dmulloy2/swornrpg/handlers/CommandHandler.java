package net.dmulloy2.swornrpg.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.commands.CmdHelp;
import net.dmulloy2.swornrpg.commands.SwornRPGCommand;
import net.dmulloy2.swornrpg.util.FormatUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

/**
 * @author dmulloy2
 */

public class CommandHandler implements CommandExecutor
{
	private String commandPrefix;
	private List<SwornRPGCommand> registeredPrefixedCommands;
	private List<SwornRPGCommand> registeredCommands;

	private final SwornRPG plugin;
	public CommandHandler(SwornRPG plugin)
	{
		this.plugin = plugin;
		this.registeredCommands = new ArrayList<SwornRPGCommand>();
	}

	public void registerCommand(SwornRPGCommand command)
	{
		PluginCommand pluginCommand = plugin.getCommand(command.getName());
		if (pluginCommand != null)
		{
			pluginCommand.setExecutor(command);
			registeredCommands.add(command);
		}
		else
		{
			plugin.outConsole(Level.WARNING, plugin.getMessage("log_command_missing"), command.getName());
		}
	}

	public void registerPrefixedCommand(SwornRPGCommand command)
	{
		if (commandPrefix != null)
			registeredPrefixedCommands.add(command);
	}

	public List<SwornRPGCommand> getRegisteredCommands()
	{
		return registeredCommands;
	}

	public List<SwornRPGCommand> getRegisteredPrefixedCommands()
	{
		return registeredPrefixedCommands;
	}

	public String getCommandPrefix()
	{
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix)
	{
		this.commandPrefix = commandPrefix;
		this.registeredPrefixedCommands = new ArrayList<SwornRPGCommand>();

		plugin.getCommand(commandPrefix).setExecutor(this);
	}

	public boolean usesCommandPrefix()
	{
		return commandPrefix != null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		List<String> argsList = new ArrayList<String>();

		if (args.length > 0)
		{
			String commandName = args[0];
			for (int i = 1; i < args.length; i++)
				argsList.add(args[i]);

			for (SwornRPGCommand command : registeredPrefixedCommands)
			{
				if (commandName.equalsIgnoreCase(command.getName()) || command.getAliases().contains(commandName.toLowerCase()))
				{
					command.execute(sender, argsList.toArray(new String[0]));
					return true;
				}
			}

			sender.sendMessage(FormatUtil.format(plugin.getMessage("error") + plugin.getMessage("unknown_command"), args[0]));
		}
		else
		{
			new CmdHelp(plugin).execute(sender, args);
		}

		return true;
	}
}