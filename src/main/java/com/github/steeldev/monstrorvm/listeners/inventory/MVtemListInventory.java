package com.github.steeldev.monstrorvm.listeners.inventory;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.util.config.Lang;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.google.common.collect.Lists;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

import static com.github.steeldev.monstrorvm.util.Util.*;

public class MVtemListInventory implements Listener {
    public static String INVENTORY_NAME = "&cMonstrorvm &eItems";
    static Monstrorvm main = Monstrorvm.getInstance();

    public static void openListInventory(Player player, int page) {
        Inventory bnItems = Bukkit.getServer().createInventory(player, 54, colorize(INVENTORY_NAME + " &7| Page: &e" + (page + 1)));

        for (int i = 0; i < 53; i++) {
            ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = (blackGlass.getItemMeta() == null) ? Bukkit.getItemFactory().getItemMeta(blackGlass.getType()) : blackGlass.getItemMeta();
            meta.setDisplayName(colorize("&7"));
            blackGlass.setItemMeta(meta);
            bnItems.setItem(i, blackGlass);
        }

        List<ItemStack> listedItems = new ArrayList<>();

        for (String item : ItemManager.getValidItemList()) {
            MVItem customItem = ItemManager.getItem(item);
            ItemStack bnItem = customItem.getItem(false);
            ItemMeta bnItemMeta = bnItem.getItemMeta();
            List<String> lore = (bnItemMeta.getLore() == null) ? new ArrayList<>() : bnItemMeta.getLore();
            if(customItem.category != null){
                lore.add("");
                lore.add(colorize("&7&oCategory:"));
                lore.add(colorize("<#2883d2>&o" + customItem.category));
            }
            lore.add(colorize("&7&oAdded By:"));
            lore.add(colorize("<#2883d2>&o" + ((customItem.registeredBy != null) ? customItem.registeredBy.getName() : "<#2883d2>&o"+main.getName())));
            lore.add("");
            lore.add(colorize("&7&oClick to give item."));
            bnItemMeta.setLore(lore);
            bnItem.setItemMeta(bnItemMeta);
            NBTItem bnItemNBT = new NBTItem(bnItem);
            bnItemNBT.addCompound("InventoryAction");
            bnItemNBT.setString("InventoryAction", "GIVE_ITEM");
            bnItemNBT.addCompound("ItemToGive");
            bnItemNBT.setString("ItemToGive", customItem.key);
            bnItem = bnItemNBT.getItem();
            listedItems.add(bnItem);
        }

        listedItems.sort((o1, o2) -> getUncoloredItemName(o1).compareToIgnoreCase(getUncoloredItemName(o2)));

        final List<List<ItemStack>> pages = Lists.partition(listedItems, 51);

        List<ItemStack> content = pages.get(page);

        for (int i = 0; i < content.size(); i++) {
            bnItems.setItem(i, content.get(i));
        }

        ItemStack closeInv = new ItemStack(Material.BARRIER);
        ItemMeta closeInvMeta = closeInv.getItemMeta();
        closeInvMeta.setDisplayName(colorize("&cClose"));
        closeInv.setItemMeta(closeInvMeta);
        NBTItem closeInvNBT = new NBTItem(closeInv);
        closeInvNBT.addCompound("InventoryAction");
        closeInvNBT.setString("InventoryAction", "CLOSE");
        closeInv = closeInvNBT.getItem();
        bnItems.setItem(53, closeInv);

        boolean showNextPage = false;
        boolean showLastPage = false;

        if (page > 0 && page < (pages.size() - 1)) {
            showNextPage = true;
            showLastPage = true;
        } else if (page == 0 && pages.size() > 1) {
            showNextPage = true;
            showLastPage = false;
        } else if (page == (pages.size() - 1) && pages.size() > 1) {
            showNextPage = false;
            showLastPage = true;
        }

        if (showNextPage) {
            ItemStack nextPage = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta nextPageMeta = (SkullMeta) nextPage.getItemMeta();
            nextPageMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_arrowright"));
            nextPageMeta.setDisplayName(colorize("&2Next Page"));
            nextPage.setItemMeta(nextPageMeta);
            NBTItem nextPageNBT = new NBTItem(nextPage);
            nextPageNBT.addCompound("InventoryAction");
            nextPageNBT.setString("InventoryAction", "NEXT_PAGE");
            nextPageNBT.addCompound("CurrentPage");
            nextPageNBT.setInteger("CurrentPage", page);
            nextPage = nextPageNBT.getItem();
            bnItems.setItem(52, nextPage);
        }

        if (showLastPage) {
            ItemStack lastPage = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta lastPageMeta = (SkullMeta) lastPage.getItemMeta();
            lastPageMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_arrowleft"));
            lastPageMeta.setDisplayName(colorize("&2Last Page"));
            lastPage.setItemMeta(lastPageMeta);
            NBTItem lastPageNBT = new NBTItem(lastPage);
            lastPageNBT.addCompound("InventoryAction");
            lastPageNBT.setString("InventoryAction", "LAST_PAGE");
            lastPageNBT.addCompound("CurrentPage");
            lastPageNBT.setInteger("CurrentPage", page);
            lastPage = lastPageNBT.getItem();
            bnItems.setItem(51, lastPage);
        }

        player.openInventory(bnItems);
    }

    @EventHandler
    public void bnItemListInvClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        if (p.getOpenInventory().getTitle().contains(colorize(INVENTORY_NAME))) {
            event.setCancelled(true);
            if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }

            ItemStack clickedItem = event.getCurrentItem();
            NBTItem clickedItemNBT = new NBTItem(clickedItem);

            if (clickedItemNBT.hasKey("InventoryAction")) {
                if (clickedItemNBT.getString("InventoryAction").equals("CLOSE")) {
                    p.closeInventory();
                    return;
                }

                if (clickedItemNBT.getString("InventoryAction").equals("GIVE_ITEM")) {
                    MVItem bnItem = ItemManager.getItem(clickedItemNBT.getString("ItemToGive"));
                    ItemStack itemToGive = bnItem.getItem(false);

                    if (p.getInventory().firstEmpty() != -1) {
                        p.getInventory().addItem(itemToGive);
                        p.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_ITEM_GIVEN_MSG
                                .replace("ITEMNAME", (bnItem.displayName == null) ? formalizedString(bnItem.baseItem.toString()) : bnItem.displayName).replace("PLAYERNAME", p.getDisplayName())
                                .replace("ITEMAMOUNT", String.valueOf(1)))));
                    } else {
                        p.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_ITEM_PLAYER_INVENTORY_FULL_MSG
                                .replace("ITEMNAME", (bnItem.displayName == null) ? formalizedString(bnItem.baseItem.toString()) : bnItem.displayName).replace("PLAYERNAME", p.getDisplayName())
                                .replace("ITEMAMOUNT", String.valueOf(1)))));
                    }
                }

                if (clickedItemNBT.getString("InventoryAction").equals("NEXT_PAGE")) {
                    int curPage = clickedItemNBT.getInteger("CurrentPage") + 1;

                    openListInventory(p, curPage);
                }
                if (clickedItemNBT.getString("InventoryAction").equals("LAST_PAGE")) {
                    int curPage = clickedItemNBT.getInteger("CurrentPage") - 1;

                    openListInventory(p, curPage);
                }
            }
        }
    }
}
