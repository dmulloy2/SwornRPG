package net.dmulloy2.swornrpg.listeners;

import java.util.List;

import net.dmulloy2.swornrpg.SwornRPG;
import net.dmulloy2.swornrpg.data.PlayerData;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 * @contributor Minesworn
 */
	
public class BlockListener implements Listener 
{
	
	public SwornRPG plugin;
	
	public BlockListener(SwornRPG plugin)
	{
		this.plugin = plugin;
	}

	public Item drop(Block block, int id)
	{
		return drop(block, id, (byte)0);
	}

	public Item drop(Block block, int id, byte type)
	{
		Item i;
		if (type > 0)
		{
			MaterialData data = new MaterialData(id);
			data.setData(type);
			ItemStack itm = data.toItemStack(1);
			i = block.getWorld().dropItem(block.getLocation(), itm);
		} 
		else
		{
			i = block.getWorld().dropItem(block.getLocation(), new ItemStack(id, 1));
		}
		return i;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) 
	{
		try 
		{
			Block block = event.getBlock();
			Material blockType = block.getType();
			Player player = event.getPlayer();
			GameMode gm = player.getGameMode();
			
			/**Iron door protection**/
			if (blockType.equals(Material.IRON_DOOR_BLOCK))
			{
				/**Config and GameMode check**/
				if ((plugin.irondoorprotect == true) && (gm == (GameMode.SURVIVAL)))
				{
					/**Protect the iron door!**/
					event.setCancelled(true);
					player.sendMessage(plugin.prefix + ChatColor.RED + "This iron door was protected from being broken");
				}
			}
			
			/**Cancels block breaking if the player is riding or sitting**/
			Entity vehicle = player.getVehicle();
			PlayerData data = plugin.getPlayerDataCache().getData(player.getName());
			if (data.isSitting() && vehicle != null)
			{
				event.setCancelled(true);
				player.sendMessage(plugin.prefix + ChatColor.YELLOW + "If you wish to break this block, use " + ChatColor.RED + "/standup");
			}
			if (data.isRiding() && vehicle != null)
			{
				event.setCancelled(true);
			}
			if (data.isSpick())
			{
				String broken = block.toString().toLowerCase().replaceAll("_", " ");
				if (broken.contains("obsidian"))
				{
					event.setCancelled(true);
				}
			}
			
			/**Random Block Drops**/
			if (
					(blockType.equals(Material.CLAY)) || 
					(blockType.equals(Material.SAND)) || 
					(blockType.equals(Material.STONE)) || 
					(blockType.equals(Material.WOOD)) || 
					(blockType.equals(Material.COBBLESTONE)) || 
					(blockType.equals(Material.ENDER_STONE)) ||           
					(blockType.equals(Material.NETHERRACK)) || 
					(blockType.equals(Material.DIRT)) || 
					(blockType.equals(Material.GRASS)) || 
					(blockType.equals(Material.GRAVEL))
				) 
			{
				/**Cancellation check**/
				if (event.isCancelled())
					return;
				
				/**Config and GameMode check**/
				if ((plugin.randomdrops == true) && (gm == (GameMode.SURVIVAL)))
				{
					int r2258 = Util.random(10000);
					int r2267 = Util.random(10000);
					int r2262 = Util.random(10000);
					int r2263 = Util.random(10000);
					int r2264 = Util.random(10000);
					int r2265 = Util.random(10000);          
					int r264 = Util.random(1000);
					int r384 = Util.random(5000);
					int r399 = Util.random(50000);  
					int r266 = Util.random(1000);
					int r265 = Util.random(1000);
					List<Integer> generallist = plugin.getConfig().getIntegerList("block-drops.general");
					int id1 = generallist.get(0);
					int id2 = generallist.get(1);
					int id3 = generallist.get(2);
					int id4 = generallist.get(3);
					int id5 = generallist.get(4);
					int id6 = generallist.get(5);
					int id7 = generallist.get(6);
					int id8 = generallist.get(7);
					int id9 = generallist.get(8);
					int id10 = generallist.get(9);
					int id11 = generallist.get(10);
					if (r2258 == 0) drop(block, id1);
					if (r2267 == 0) drop(block, id2);
					if (r2262 == 0) drop(block, id3);
					if (r2263 == 0) drop(block, id4);
					if (r2264 == 0) drop(block, id5);
					if (r2265 == 0) drop(block, id6);
					if (r264 == 0) drop(block, id7);        
					if (r384 == 0) drop(block, id8);
					if (r399 == 0) drop(block, id9); 
					if (r266 == 0) drop(block, id10);
					if (r265 == 0) drop(block, id11);
					
					if (blockType.equals(Material.CLAY)) 
					{
						List<Integer> claylist = plugin.getConfig().getIntegerList("block-drops.clay");
						int cl1 = (int) claylist.get(0);
						int cl2 = (int) claylist.get(1);
						int cl3 = (int) claylist.get(2);
						int cl4 = (int) claylist.get(3);
						int r341 = Util.random(20);
						int r287 = Util.random(20);
						int r318 = Util.random(20);
						int r30 = Util.random(100);

						if (r341 == 0) drop(block, cl1);
						if (r287 == 0) drop(block, cl2);
						if (r318 == 0) drop(block, cl3);
						if (r30 == 0) drop(block, cl4);
					}
					if (blockType.equals(Material.GRASS)) 
					{
						List<Integer> grasslist = plugin.getConfig().getIntegerList("block-drops.grass");
						int ga1 = grasslist.get(0);
						int ga2 = grasslist.get(1);
						int ga3 = grasslist.get(2);
						int r361 = Util.random(30);
						int r392 = Util.random(30);
						int r391 = Util.random(30);
					
						if (r361 == 0) drop(block, ga1);
						if (r392 == 0) drop(block, ga2);
						if (r391 == 0) drop(block, ga3);
					}
					if (blockType.equals(Material.DIRT)) 
					{
						List<Integer> dirtlist = plugin.getConfig().getIntegerList("block-drops.dirt");
						int d1 = dirtlist.get(0);
						int d2 = dirtlist.get(1);
						int d3 = dirtlist.get(2);
						int d4 = dirtlist.get(3);
						int r392 = Util.random(30);
						int r357 = Util.random(30);
						int r395 = Util.random(100);
						int r89 = Util.random(100);
						
						if (r392 == 0) drop(block, d1);
						if (r357 == 0) drop(block, d2);
						if (r395 == 0) drop(block, d3);
						if (r89 == 0) drop(block, d4);
					}
					if (blockType.equals(Material.GRAVEL)) 
					{
						List<Integer> gravellist = plugin.getConfig().getIntegerList("block-drops.gravel");
						int gv1 = gravellist.get(1);
						int gv2 = gravellist.get(2);
						int gv3 = gravellist.get(3);
						int r289 = Util.random(15);
						int r352 = Util.random(10);
						int r87 = Util.random(50);

						if (r289 == 0) drop(block, gv1);
						if (r352 == 0) drop(block, gv2);
						if (r87 == 0) drop(block, gv3);
					}
					if (blockType.equals(Material.SAND)) 
					{
						List<Integer> sandlist = plugin.getConfig().getIntegerList("block-drops.sand");
						int sa1 = sandlist.get(0);
						int sa2 = sandlist.get(1);
						int sa3 = sandlist.get(2);
						int r88 = Util.random(50);
						int r362 = Util.random(100);
						int r371 = Util.random(100);
						
						if (r88 == 0) drop(block, sa1);
						if (r362 == 0) drop(block, sa2);
						if (r371 == 0) drop(block, sa3);
					}
					if (blockType.equals(Material.STONE)) 
					{
						List<Integer> stonelist = plugin.getConfig().getIntegerList("block-drops.stone");
						int st1 = stonelist.get(0);
						int st2 = stonelist.get(1);
						int st3 = stonelist.get(2);
						int r15 = Util.random(75);
						int r16 = Util.random(25);
						int r14 = Util.random(200);
					
						if (r15 == 0) drop(block, st1);
						if (r16 == 0) drop(block, st2);
						if (r14 == 0) drop(block, st3);
					}
					if (blockType.equals(Material.NETHERRACK))
					{
						List<Integer> netherracklist = plugin.getConfig().getIntegerList("block-drops.netherrack");
						int n1 = netherracklist.get(0);
						int n2 = netherracklist.get(1);
						int n3 = netherracklist.get(2);
						int n4 = netherracklist.get(3);
						int r385 = Util.random(15);
						int r372 = Util.random(25);
						int r112 = Util.random(10);
						int r406 = Util.random(10);
	
						if (r385 == 0) drop(block, n1);
						if (r372 == 0) drop(block, n2);
						if (r112 == 0) drop(block, n3);
						if (r406 == 0) drop(block, n4);
					}
					if (blockType.equals(Material.ENDER_STONE)) 
					{
						List<Integer> endstonelist = plugin.getConfig().getIntegerList("block-drops.ender_stone");
						int e1 = endstonelist.get(0);
						int e2 = endstonelist.get(1);
						int e3 = endstonelist.get(2);
						int r116 = Util.random(150);
						int r388 = Util.random(100);
						int r368 = Util.random(25);
						
						if (r116 == 0) drop(block, e1);
						if (r388 == 0) drop(block, e2);
						if (r368 == 0) drop(block, e3);
					}
					if (blockType.equals(Material.COBBLESTONE))
					{
						List<Integer> cobblelist = plugin.getConfig().getIntegerList("block-drops.cobblestone");
						int cb1 = cobblelist.get(0);
						int cb2 = cobblelist.get(1);
						int cb3 = cobblelist.get(2);
						int r389 = Util.random(20);
						int r145 = Util.random(200);
						int r386 = Util.random(100);
	
						if (r389 == 0) drop(block, cb1);
						if (r145 == 0) drop(block, cb2);
						if (r386 == 0) drop(block, cb3);
					}
					if (blockType.equals(Material.WOOD)) 
					{
						List<Integer> woodlist = plugin.getConfig().getIntegerList("block-drops.wood");
						int w1 = woodlist.get(0);
						int w2 = woodlist.get(1);
						int w3 = woodlist.get(2);
						int r338 = Util.random(20);
						int r32 = Util.random(200);
						int r127 = Util.random(100);
						
						if (r338 == 0) drop(block, w1);
						if (r32 == 0) drop(block, w2);
						if (r127 == 0) drop(block, w3);
					}
				}
			}
		}
		catch (Exception localException)
		{
		}
	}
}