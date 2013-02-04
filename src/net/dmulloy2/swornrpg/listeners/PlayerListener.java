package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.SwornRPG;

import net.dmulloy2.swornrpg.util.InventoryHelper;
import net.dmulloy2.swornrpg.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author orange451
 * @editor dmulloy2
 */

public class PlayerListener
  implements Listener {

  private SwornRPG plugin;
  int ChestMax = 8;
  int LegsMax = 7;
  int HelmMax = 5;
  int bootsMax = 4;

  int ironMax = 155;
  int diamondMax = 155;

  public PlayerListener(SwornRPG plugin) {
    this.plugin = plugin;
  }

  public boolean BlockNear(Material mat, Block block, int x, int y, int z) {
    Location loc = new Location(Util.world, block.getX() + x, block.getY() + y, block.getZ() + z);
    Block block2 = Util.world.getBlockAt(loc);
    if (block2.getType() == mat) {
      return true;
    }
    return false;
  }

  @SuppressWarnings("deprecation")
@EventHandler(priority=EventPriority.NORMAL)
  public void onPlayerInteract(PlayerInteractEvent event) {
    try {
      if (!event.hasBlock()) {
        return;
      }

      if (event.getClickedBlock() == null) {
        return;
      }

      Player pl = event.getPlayer();

      Block block = event.getClickedBlock();

      event.getAction().equals(Action.LEFT_CLICK_BLOCK);

      if (block.getType().equals(Material.IRON_BLOCK)) {
        if ((BlockNear(Material.FURNACE, block, -1, 0, 0)) || (BlockNear(Material.FURNACE, block, 1, 0, 0)) || (BlockNear(Material.FURNACE, block, 0, 0, -1)) || (BlockNear(Material.FURNACE, block, 0, 0, 1))) {
          ItemStack item = pl.getItemInHand();
          Material mitem = item.getType();
          double mult = 1.0D - item.getDurability() / this.ironMax;
          double amtIron = 0.0D;
          if (mitem.equals(Material.IRON_BOOTS)) {
            amtIron = Math.ceil(this.bootsMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.IRON_HELMET)) {
            amtIron = Math.ceil(this.HelmMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.IRON_LEGGINGS)) {
            amtIron = Math.ceil(this.LegsMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.IRON_CHESTPLATE)) {
            amtIron = Math.ceil(this.ChestMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          System.out.println(amtIron);
          if (amtIron > 0.0D)
            try {
              pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "You have salvaged '" + mitem + "' for " + amtIron + " iron ingot(s)");
              Inventory inv = pl.getInventory();
              inv.removeItem(new ItemStack[] { item });
              Material give = Material.IRON_INGOT;
              int slot = getSlot(give.getId(), inv);
              if (slot == -1) {
                slot = InventoryHelper.getFirstFreeSlot(inv);
              }
              if (slot <= -1) return;
              int amt = 0;
              if (inv.getItem(slot) != null) {
                amt = inv.getItem(slot).getAmount();
              }

              ItemStack itm = new ItemStack(give.getId(), amt + (int)amtIron);
              inv.setItem(slot, itm);
              pl.updateInventory();
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          else
            pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "'" + mitem + "' is not a type of iron gear");
        }
      } else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
        if ((BlockNear(Material.FURNACE, block, -1, 0, 0)) || (BlockNear(Material.FURNACE, block, 1, 0, 0)) || (BlockNear(Material.FURNACE, block, 0, 0, -1)) || (BlockNear(Material.FURNACE, block, 0, 0, 1))) {
          ItemStack item = pl.getItemInHand();
          Material mitem = item.getType();

          double mult = 1.0D - item.getDurability() / this.diamondMax;
          double amtIron = 0.0D;
          if (mitem.equals(Material.DIAMOND_BOOTS)) {
            amtIron = Math.ceil(this.bootsMax * mult);
          }
          if (amtIron < 0.0D) {
            amtIron = 1.0D;
          }
          if (mitem.equals(Material.DIAMOND_HELMET)) {
            amtIron = Math.ceil(this.HelmMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.DIAMOND_LEGGINGS)) {
            amtIron = Math.ceil(this.LegsMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.DIAMOND_CHESTPLATE)) {
            amtIron = Math.ceil(this.ChestMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          amtIron -= 1.0D;
          if (amtIron > 0.0D)
            try {
              pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "You have salvaged '" + mitem + "' for " + amtIron + " diamond(s)");
              Inventory inv = pl.getInventory();
              inv.removeItem(new ItemStack[] { item });
              Material give = Material.DIAMOND;
              int slot = getSlot(give.getId(), inv);
              if (slot == -1) {
                slot = InventoryHelper.getFirstFreeSlot(inv);
              }
              if (slot <= -1) return;
              int amt = 0;
              if (inv.getItem(slot) != null) {
                amt = inv.getItem(slot).getAmount();
              }
              ItemStack itm = new ItemStack(give.getId(), amt + (int)amtIron);
              inv.setItem(slot, itm);
              pl.updateInventory();
            }
            catch (Exception localException1)
            {
            }
          else
            pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "'" + mitem + "' is not a type of diamond gear");
        }
      } else if (block.getType().equals(Material.GOLD_BLOCK))
        if ((BlockNear(Material.FURNACE, block, -1, 0, 0)) || (BlockNear(Material.FURNACE, block, 1, 0, 0)) || (BlockNear(Material.FURNACE, block, 0, 0, -1)) || (BlockNear(Material.FURNACE, block, 0, 0, 1))) {
          ItemStack item = pl.getItemInHand();
          Material mitem = item.getType();

          double mult = 1.0D - item.getDurability() / this.diamondMax;
          double amtIron = 0.0D;
          if (mitem.equals(Material.GOLD_BOOTS)) {
            amtIron = Math.ceil(this.bootsMax * mult);
          }
          if (amtIron < 0.0D) {
            amtIron = 1.0D;
          }
          if (mitem.equals(Material.GOLD_HELMET)) {
            amtIron = Math.ceil(this.HelmMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.GOLD_LEGGINGS)) {
            amtIron = Math.ceil(this.LegsMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          if (mitem.equals(Material.GOLD_CHESTPLATE)) {
            amtIron = Math.ceil(this.ChestMax * mult);
            if (amtIron < 0.0D) {
              amtIron = 1.0D;
            }
          }
          amtIron -= 1.0D;
          if (amtIron > 0.0D)
            try {
              pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "You have salvaged '" + mitem + "' for " + amtIron + " gold ingot(s)");
              Inventory inv = pl.getInventory();
              inv.removeItem(new ItemStack[] { item });
              Material give = Material.GOLD_INGOT;
              int slot = getSlot(give.getId(), inv);
              if (slot == -1) {
                slot = InventoryHelper.getFirstFreeSlot(inv);
              }
              if (slot <= -1) return;
              int amt = 0;
              if (inv.getItem(slot) != null) {
                amt = inv.getItem(slot).getAmount();
              }
              ItemStack itm = new ItemStack(give.getId(), amt + (int)amtIron);
              inv.setItem(slot, itm);
              pl.updateInventory();
            }
            catch (Exception localException2)
            {
            }
          else
            pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "'" + mitem + "' is not a type of gold gear");
        }
    }
    catch (Exception localException3)
    {
    }
  }

  public int getSlot(int id, Inventory inv) {
    int ret = -1;
    for (int i = 0; i <= 35; i++) {
      ItemStack item = inv.getItem(i);
      if (item != null) {
        int type = item.getTypeId();
        if (type == id) {
          ret = i;
        }
      }
    }

    return ret;
  }
  
  @EventHandler(priority=EventPriority.NORMAL)
  public void onPlayerChat(AsyncPlayerChatEvent event) {
    try {
      String msg = event.getMessage();
      Player player = event.getPlayer();
      if (this.plugin.isAdminChatting(player.getName())) {
        this.plugin.sendAdminMessage(player.getName(), msg);
        event.setCancelled(true);
      }
      if (this.plugin.isCouncilChatting(player.getName())) {
          this.plugin.sendCouncilMessage(player.getName(), msg);
          event.setCancelled(true);
      }
    }
    catch (Exception localException)
    {
    }
  }
}
