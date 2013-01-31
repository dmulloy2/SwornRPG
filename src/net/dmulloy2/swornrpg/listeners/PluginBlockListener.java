package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.main;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * @author orange451
 * @editor dmulloy2
 */

public class PluginBlockListener
  implements Listener {

  public PluginBlockListener(main plugin)
  {
  }

  public Item drop(Block block, int id)
  {
    return drop(block, id, (byte)0);
  }

  public Item drop(Block block, int id, byte type)
  {
    Item i;
    if (type > 0) {
      MaterialData data = new MaterialData(id);
      data.setData(type);
      ItemStack itm = data.toItemStack(1);
      i = block.getWorld().dropItem(block.getLocation(), itm);
    } else {
      i = block.getWorld().dropItem(block.getLocation(), new ItemStack(id, 1));
    }
    return i;
  }

  @EventHandler(priority=EventPriority.NORMAL)
  public void onBlockBreak(BlockBreakEvent event) {
    try {
      Block block = event.getBlock();
      Material blockType = block.getType();
      if (blockType.equals(Material.IRON_DOOR_BLOCK)) {
        event.setCancelled(true);
        return;
      }

      if (!event.isCancelled())
      {
        if ((blockType.equals(Material.CLAY)) || 
          (blockType.equals(Material.SAND)) || 
          (blockType.equals(Material.STONE)) || 
          (blockType.equals(Material.WOOD)) || 
          (blockType.equals(Material.COBBLESTONE)) || 
          (blockType.equals(Material.ENDER_STONE)) ||           
          (blockType.equals(Material.NETHERRACK)) || 
          (blockType.equals(Material.DIRT)) || 
          (blockType.equals(Material.GRASS)) || 
          (blockType.equals(Material.GRAVEL))) {
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
          if (r2258 == 0) drop(block, 2258);
          if (r2267 == 0) drop(block, 2267);
          if (r2262 == 0) drop(block, 2262);
          if (r2263 == 0) drop(block, 2263);
          if (r2264 == 0) drop(block, 2264);
          if (r2265 == 0) drop(block, 2265);
          if (r264 == 0) drop(block, 264);        
          if (r384 == 0) drop(block, 384);
          if (r399 == 0) drop(block, 399); 
          if (r266 == 0) drop(block, 266);
          if (r265 == 0) drop(block, 265);
          if (blockType.equals(Material.CLAY)) {
            int r341 = Util.random(20);
            int r287 = Util.random(20);
            int r318 = Util.random(20);
            int r30 = Util.random(100);

            if (r341 == 0) drop(block, 341);
            if (r287 == 0) drop(block, 287);
            if (r318 == 0) drop(block, 318);
            if (r30 == 0) drop(block, 30);
          }
          if (blockType.equals(Material.GRASS)) {
            int r361 = Util.random(30);
            int r392 = Util.random(30);
            int r391 = Util.random(30);

            if (r361 == 0) drop(block, 361);
            if (r392 == 0) drop(block, 392);
            if (r391 == 0) drop(block, 391);
          }
          if (blockType.equals(Material.DIRT)) {
            int r392 = Util.random(30);
            int r357 = Util.random(30);
            int r395 = Util.random(100);
            int r89 = Util.random(100);

            if (r392 == 0) drop(block, 392);
            if (r357 == 0) drop(block, 357);
            if (r395 == 0) drop(block, 395);
            if (r89 == 0) drop(block, 330);
          }
          if (blockType.equals(Material.GRAVEL)) {
            int r289 = Util.random(15);
            int r352 = Util.random(10);
            int r87 = Util.random(50);

            if (r289 == 0) drop(block, 289);
            if (r352 == 0) drop(block, 352);
            if (r87 == 0) drop(block, r87);
          }
          if (blockType.equals(Material.SAND)) {
            int r88 = Util.random(50);
            int r362 = Util.random(100);
            int r371 = Util.random(100);
            if (r88 == 0) drop(block, 88);
            if (r362 == 0) drop(block, 362);
            if (r371 == 0) drop(block, 371);
          }
          if (blockType.equals(Material.STONE)) {
            int r15 = Util.random(75);
            int r16 = Util.random(25);
            int r14 = Util.random(200);

            if (r15 == 0) drop(block, 15);
            if (r16 == 0) drop(block, 16);
            if (r14 == 0) drop(block, 14);
          }
          if (blockType.equals(Material.NETHERRACK)) {
            int r385 = Util.random(15);
            int r372 = Util.random(25);
            int r112 = Util.random(10);

            if (r385 == 0) drop(block, 385);
            if (r372 == 0) drop(block, 372);
            if (r112 == 0) drop(block, 112);
          }
          if (blockType.equals(Material.ENDER_STONE)) {
            int r388 = Util.random(100);
            int r116 = Util.random(150);
            int r368 = Util.random(20);

            if (r388 == 0) drop(block, 388);
            if (r116 == 0) drop(block, 116);
            if (r368 == 0) drop(block, 368);
          }
          if (blockType.equals(Material.COBBLESTONE)) {
            int r389 = Util.random(20);
            int r145 = Util.random(200);
            int r386 = Util.random(100);

            if (r389 == 0) drop(block, 389);
            if (r145 == 0) drop(block, 145);
            if (r386 == 0) drop(block, 386);
          }
          if (blockType.equals(Material.WOOD)) {
            int r338 = Util.random(20);
            int r32 = Util.random(200);
            int r127 = Util.random(100);

            if (r338 == 0) drop(block, 338);
            if (r32 == 0) drop(block, 32);
            if (r127 == 0) drop(block, 127);
          }
        }
      }
    }
    catch (Exception localException)
    {
    }
  }
}
