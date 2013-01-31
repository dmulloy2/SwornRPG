package net.dmulloy2.swornrpg.listeners;

import net.dmulloy2.swornrpg.main;

import net.dmulloy2.swornrpg.PermissionsInterface.PermissionInterface;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author orange451
 * @editor dmulloy2
 */

public class PluginPlayerListener
  implements Listener {

  private main plugin;
  int ChestMax = 8;
  int LegsMax = 7;
  int HelmMax = 5;
  int bootsMax = 4;

  int ironMax = 155;
  int diamondMax = 155;

  public PluginPlayerListener(main plugin) {
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
              pl.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.GRAY + "You have salvaged '" + mitem + "' for " + amtIron + " gold(s)");
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
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    try {
      Player player = event.getPlayer();
      String[] split = event.getMessage().split(" ");
      split[0] = split[0].substring(1);
      String label = split[0];
      String[] args = new String[split.length - 1];
      for (int i = 1; i < split.length; i++) {
        args[(i - 1)] = split[i];
      }

      if ((label.equalsIgnoreCase("a")) && 
        (args.length != 0))
      {
        if (PermissionInterface.checkPermission(player, this.plugin.adminChatPerm)) {
          int amt = args.length;
          String str = "";
          for (int i = 0; i < amt; i++) {
            str = str + args[i] + " ";
          }
          this.plugin.sendAdminMessage(player.getName(), str);
        }
      }

      if (((label.equalsIgnoreCase("srpg"))) || ((label.equalsIgnoreCase("swornrpg")))){
    	  player.sendMessage(ChatColor.DARK_RED + "======" + ChatColor.GOLD + " SwornRPG " + ChatColor.DARK_RED + "======");
    	  player.sendMessage(ChatColor.DARK_RED + "/swornrpg" + ChatColor.GOLD + " (srpg) " + ChatColor.YELLOW + "Displays this help menu");
    	  //player.sendMessage(ChatColor.DARK_RED + "/srpg " + ChatColor.GOLD + "level" + ChatColor.YELLOW + " Displays your current level");
    	  if (player.hasPermission("srpg.ride")){
    		  player.sendMessage(ChatColor.DARK_RED + "/ride " + ChatColor.YELLOW + "Ride another player");}
    	  player.sendMessage(ChatColor.DARK_RED + "/hat " + ChatColor.YELLOW + "Get a new hat!");
    	  if (player.hasPermission("srpg.adminchat")){
    		  player.sendMessage(ChatColor.DARK_RED + "/a " + ChatColor.YELLOW + "Talk in admin chat");}
    	  if (player.hasPermission("srpg.asay")){
    		  player.sendMessage(ChatColor.DARK_RED + "/adm " + ChatColor.YELLOW + "Alternate admin say command");}
    	  if (player.getName().contains("dmulloy2")){
        	  player.sendMessage(ChatColor.DARK_RED + "/dmu " + ChatColor.YELLOW + "dmulloy's special chat");
    	  }
      }
      if ((label.equalsIgnoreCase("ride")) && 
        (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)) && 
        (args.length == 1)) {
        Player to = Util.MatchPlayer(args[0]);
        to.setPassenger(player);
      }

      if ((label.equalsIgnoreCase("unride")) && 
        (PermissionInterface.checkPermission(player, this.plugin.adminRidePerm)) && 
        (args.length == 1)) {
        Player to = Util.MatchPlayer(args[0]);
        to.setPassenger(null);
      }
      if ((label.equalsIgnoreCase("adm")) && 
    	  (PermissionInterface.checkPermission(player, this.plugin.adminSayPerm))){
    	  int amt = args.length;
    	  String str = "";
    	  for (int i = 0; i < amt; i++) {
    	  str = str + args[i] + " ";
        }
        this.plugin.sendMessageAll(ChatColor.DARK_PURPLE + "[" + ChatColor.DARK_RED + "Announcement" + ChatColor.DARK_PURPLE + "] " + ChatColor.DARK_PURPLE + str);
      }

      if ((label.equalsIgnoreCase("dmu")) && 
        (player.getName().contains("dmulloy2"))) {
        int amt = args.length;
        String str = "";
        for (int i = 0; i < amt; i++) {
          str = str + args[i] + " ";
        }
        this.plugin.sendMessageAll(ChatColor.AQUA + "[" + ChatColor.DARK_GRAY + "Announcement" + ChatColor.AQUA + "] " + ChatColor.AQUA + str);
      }

      if (label.equalsIgnoreCase("hat"))
        try {
          ItemStack itm = player.getItemInHand();
          ItemStack toHead = itm.clone();
          toHead.setAmount(1);
          int type = itm.getTypeId();
          if (type < 256) {
            if (itm.getAmount() > 1) {
              itm.setAmount(itm.getAmount() - 1);
              player.setItemInHand(itm);
            } else {
              player.setItemInHand(null);
            }
            player.getInventory().setHelmet(toHead);
          } else {
            player.sendMessage(ChatColor.GOLD + "[SwornRPG] " + ChatColor.RED + "Error: This is not a block!");
          }
        }
        catch (Exception localException)
        {
        }
    }
    catch (Exception localException1)
    {
    }
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
    }
    catch (Exception localException)
    {
    }
  }
}
