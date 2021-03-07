package com.github.steeldev.monstrorvm.util.items.mvitems;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import static com.github.steeldev.monstrorvm.util.Util.*;

public class DebugStick implements Listener {
    @EventHandler
    public void useDebugStickOnItemAlt(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (e.getClickedBlock() != null
                && !mainHand.getType().equals(Material.AIR)
                && isMVItem(mainHand, "mv_debug_stick")
                && !e.getClickedBlock().getType().equals(Material.AIR)
                && e.getHand().equals(EquipmentSlot.HAND)
                && e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && !player.isSneaking()) {
            player.sendMessage(colorize("&7Block Information:"));
            player.sendMessage(colorize("&7Type: &e" + e.getClickedBlock().getType()));
            player.sendMessage(colorize("&7Location: &e" + e.getClickedBlock().getLocation()));
        }

        if (player.isSneaking()
                && !mainHand.getType().equals(Material.AIR)
                && isMVItem(mainHand, "mv_debug_stick")
                && e.getHand().equals(EquipmentSlot.HAND)
                && e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            NBTItem mainHandNBT = new NBTItem(mainHand);
            String disp = (mainHand.getItemMeta() != null && mainHand.getItemMeta().getDisplayName() != null) ? mainHand.getItemMeta().getDisplayName() : mainHand.getType().toString();
            player.sendMessage(colorize("&7Item Information:"));
            player.sendMessage(colorize("&7Name: &e" + disp));
            player.sendMessage(colorize("&7Type: &e" + mainHand.getType()));
            player.sendMessage(colorize("&7NBT: &e" + mainHandNBT.asNBTString()));
            String isMV = (isMVItem(mainHand)) ? "&2True" : "&cFalse";
            player.sendMessage(colorize("&7MVItem: " + isMV));
        }
    }

    @EventHandler
    public void useDebugStickOnEntity(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if(e.getRightClicked() instanceof LivingEntity) {
            if(e.getHand().equals(EquipmentSlot.HAND)
                    && !mainHand.getType().equals(Material.AIR)
                    && isMVItem(mainHand, "mv_debug_stick")) {
                LivingEntity entity = (LivingEntity) e.getRightClicked();
                String disp = (entity.getCustomName() != null) ? entity.getCustomName() : entity.getName();
                player.sendMessage(colorize("&7Entity Information:"));
                player.sendMessage(colorize("&7Name: &e"+disp));
                player.sendMessage(colorize("&7Type: &e"+entity.getType()));
                player.sendMessage(colorize("&7Alive For(in ticks): &e"+entity.getTicksLived()));
                player.sendMessage(colorize("&7Move Speed: &e"+entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()));
                player.sendMessage(colorize("&7Health: &e"+entity.getHealth() + "&7/&e"+entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                String isMV = (isMVMob(entity)) ? "&2True" : "&cFalse";
                player.sendMessage(colorize("&7MVMob: "+isMV));
            }
        }
    }
}
