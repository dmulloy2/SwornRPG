/**
 * SwornRPG - a Bukkit plugin
 * Copyright (C) 2013 - 2015 dmulloy2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.dmulloy2.swornrpg.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.types.Permission;
import net.dmulloy2.swornrpg.types.PlayerData;
import net.dmulloy2.swornapi.types.Sorter;
import net.dmulloy2.swornapi.util.FormatUtil;
import net.dmulloy2.swornapi.util.Util;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdLeaderboard extends SwornRPGCommand
{
	protected boolean updating;
	protected long lastUpdateTime;
	protected List<String> leaderboard;

	public CmdLeaderboard(SwornRPG plugin)
	{
		super(plugin);
		this.name = "lb";
		this.aliases.add("top");
		this.description = "Display experience leaderboard";
		this.permission = Permission.LEADERBOARD;
		this.mustBePlayer = true;
		this.usesPrefix = true;
	}

	@Override
	public void perform()
	{
		if (updating)
		{
			err("Leaderboard is already updating!");
			return;
		}

		if (leaderboard == null)
		{
			this.leaderboard = new ArrayList<>();
		}

		if (System.currentTimeMillis() - lastUpdateTime > 600000L)
		{
			sendMessage(plugin.getMessage("leaderboard_wait"));
			leaderboard.clear();
			this.updating = true;
			new BuildLeaderboardThread();
		}

		new DisplayLeaderboardThread(sender.getName(), args);
	}

	public void displayLeaderboard(String senderName, String[] args)
	{
		CommandSender sender = getSender(senderName);
		if (sender == null)
			return;

		if (leaderboard.isEmpty())
		{
			err(sender, "No players with experience found!");
			return;
		}

		int index = 1;

		if (args.length > 0)
		{
			int indexFromArg = argAsInt(0, false);
			if (indexFromArg > 1)
				index = indexFromArg;
		}

		int pageCount = getPageCount();

		if (index > pageCount)
		{
			err(sender, getMessage("error_no_page_with_index"), args[0]);
			return;
		}

		for (String s : getPage(index))
			sendMessage(sender, s);
	}

	private final int linesPerPage = 10;

	public int getPageCount()
	{
		return (getListSize() + linesPerPage - 1) / linesPerPage;
	}

	public int getListSize()
	{
		return leaderboard.size();
	}

	public List<String> getPage(int index)
	{
		List<String> lines = new ArrayList<>();

		StringBuilder line = new StringBuilder();
		line.append(getHeader(index));
		lines.add(line.toString());

		lines.addAll(getLines((index - 1) * linesPerPage, index * linesPerPage));

		if (index != getPageCount())
		{
			line = new StringBuilder();
			line.append(FormatUtil.format(getMessage("leaderboard_nextpage"), index + 1));
			lines.add(line.toString());
		}

		return lines;
	}

	public String getHeader(int index)
	{
		return FormatUtil.format(getMessage("leaderboard_header"), index, getPageCount());
	}

	public List<String> getLines(int startIndex, int endIndex)
	{
		List<String> lines = new ArrayList<>();
		for (int i = startIndex; i < endIndex && i < getListSize(); i++)
		{
			lines.add(leaderboard.get(i));
		}

		return lines;
	}

	public class BuildLeaderboardThread extends Thread
	{
		public BuildLeaderboardThread()
		{
			super("SwornRPG-BuildLeaderboard");
			this.setPriority(MIN_PRIORITY);
			this.start();
		}

		@Override
		public void run()
		{
			plugin.log("Updating leaderboard...");

			long start = System.currentTimeMillis();

			Map<UUID, PlayerData> allData = plugin.getPlayerDataCache().getAllData();
			List<PlayerData> sorted = xpSorter().sort(allData.values());

			String format = getMessage("leaderboard_format");

			for (int i = 0; i < sorted.size(); i++)
			{
				try
				{
					PlayerData data = sorted.get(i);
					int xp = data.getTotalxp();
					if (xp == 0) continue;

					String name = data.getLastKnownBy();
					int spaces = 20 - name.length();

					leaderboard.add(FormatUtil.format(format, i + 1, name + " ".repeat(Math.max(0, spaces)), data.getLevel(), data.getTotalxp()));
				} catch (Throwable ignored) { }
			}

			sorted.clear();

			lastUpdateTime = System.currentTimeMillis();
			updating = false;

			plugin.log("Leaderboard updated! Took {0} ms!", System.currentTimeMillis() - start);

			allData.clear();
		}
	}

	public class DisplayLeaderboardThread extends Thread
	{
		private final String[] args;
		private final String senderName;
		public DisplayLeaderboardThread(String senderName, String[] args)
		{
			super("SwornRPG-DisplayLeaderboard");
			this.setPriority(MIN_PRIORITY);
			this.senderName = senderName;
			this.args = args;
			this.start();
		}

		@Override
		public void run()
		{
			try
			{
				while (updating)
				{
					sleep(500L);
				}

				displayLeaderboard(senderName, args);
			}
			catch (Throwable ex)
			{
				CommandSender sender = getSender(senderName);
				if (sender != null)
					err(sender, "Failed to update leaderboard: &c{0}", ex);

				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "updating leaderboard"));
			}
		}
	}

	private CommandSender getSender(String name)
	{
		if (name.equalsIgnoreCase("CONSOLE"))
			return plugin.getServer().getConsoleSender();
		else
			return Util.matchPlayer(name);
	}

	private Sorter<PlayerData, Integer> xpSorter()
	{
		return new Sorter<>(key -> {
			// Filter out 0 values
			int value = key.getTotalxp();
			return value > 0 ? value : null;
		});
	}
}