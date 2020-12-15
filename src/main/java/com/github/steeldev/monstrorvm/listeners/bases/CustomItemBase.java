package com.github.steeldev.monstrorvm.listeners.bases;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.items.ItemUseEffectType;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static com.github.steeldev.monstrorvm.util.Util.chanceOf;
import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class CustomItemBase implements Listener {
    Monstrorvm main = Monstrorvm.getInstance();

    MVItem item;

    public CustomItemBase(String itemID) {
        item = ItemManager.getItem(itemID);
    }

    @EventHandler
    public void customItemAttack(EntityDamageByEntityEvent event) {
        if (item == null) return;
        if (event.isCancelled()) return;
        if (event.getDamager() instanceof Player) {
            ItemStack attackItem = ((Player) event.getDamager()).getInventory().getItemInMainHand();
            if (attackItem.getType().equals(Material.AIR)) return;
            if (attackItem.getType() != item.baseItem) return;
            if (!Util.isMVItem(attackItem,item.key)) return;

            if (item.attackEffect == null || item.attackEffect.size() < 1) return;

            if (event.getEntity() instanceof LivingEntity) {
                for (MVPotionEffect entry : item.attackEffect) {
                    LivingEntity victim = (LivingEntity) event.getEntity();
                    if (chanceOf(entry.chance)) {
                        victim.addPotionEffect(entry.getPotionEffect(), false);
                        if (Config.DEBUG)
                            main.getLogger().info(String.format("&aCustom item &6%s &cinflicted &e%s &cwith &4%s&c!", item.displayName, victim.getName(), entry.getPotionEffect().toString()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void customItemUse(PlayerInteractEvent event) {
        if (item == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.isCancelled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK &&
                event.getAction() != Action.RIGHT_CLICK_AIR) return;
        Player player = event.getPlayer();
        ItemStack useItem = event.getPlayer().getInventory().getItemInMainHand();
        if (useItem.getType().equals(Material.AIR)) return;
        if (useItem.getType() != item.baseItem) return;
        if (!Util.isMVItem(useItem,item.key)) return;

        if (item.useEffect == null) return;

        event.setCancelled(true);

        if (item.useEffect.type == ItemUseEffectType.SPAWN_CUSTOM_MOB) {
            if (event.getClickedBlock() == null) return;
            if (event.getClickedBlock().getType() == Material.AIR) return;
            if (item.useEffect.mobID.equals("")) return;

            MVMob mobToSpawn = MobManager.getMob(item.useEffect.mobID);

            if (mobToSpawn != null)
                mobToSpawn.spawnMob(event.getClickedBlock().getLocation().add(0, 1, 0), null);
        } else if (item.useEffect.type == ItemUseEffectType.EFFECT_HOLDER) {
            for (MVPotionEffect effect : item.useEffect.potionEffects) {
                if (chanceOf(effect.chance)) {
                    player.addPotionEffect(effect.getPotionEffect(), false);
                    if (Config.DEBUG)
                        main.getLogger().info(String.format("&aCustom Item &6%s &cinflicted &e%s &cwith &4%s&c!", item.displayName, event.getPlayer().getName(), effect.getPotionEffect().toString()));
                }
            }
        }
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && item.useEffect.consumeItemOnUse ||
                (event.getPlayer().getGameMode() != GameMode.CREATIVE && item.useEffect.type == ItemUseEffectType.SPAWN_CUSTOM_MOB))
            useItem.setAmount(useItem.getAmount() - 1);
    }

    @EventHandler
    public void customItemUseOnEntity(PlayerInteractAtEntityEvent event) {
        if (item == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        ItemStack useItem = event.getPlayer().getInventory().getItemInMainHand();
        if (useItem.getType().equals(Material.AIR)) return;
        if (useItem.getType() != item.baseItem) return;
        if (!Util.isMVItem(useItem,item.key)) return;

        if (item.useEffect == null) return;

        event.setCancelled(true);

        if (item.useEffect.type == ItemUseEffectType.EFFECT_CLICKED) {
            if (!(event.getRightClicked() instanceof LivingEntity))
                return;
            for (MVPotionEffect effect : item.useEffect.potionEffects) {
                LivingEntity victim = (LivingEntity) event.getRightClicked();
                if (chanceOf(effect.chance)) {
                    victim.addPotionEffect(effect.getPotionEffect(), false);
                    if (Config.DEBUG)
                        main.getLogger().info(String.format("&aCustom Item &6%s &aheld by &e%s &cinflicted &e%s &cwith &4%s&c!", item.displayName, player.getName(), victim.getName(), effect.getPotionEffect().toString()));
                }
            }
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && item.useEffect.consumeItemOnUse ||
                (event.getPlayer().getGameMode() != GameMode.CREATIVE && item.useEffect.type == ItemUseEffectType.SPAWN_CUSTOM_MOB))
            useItem.setAmount(useItem.getAmount() - 1);
    }

    @EventHandler
    public void customItemConsume(PlayerItemConsumeEvent event) {
        if (item == null) return;
        if (event.isCancelled()) return;

        ItemStack consumedItem = event.getItem();
        if (consumedItem.getType().equals(Material.AIR)) return;
        if (consumedItem.getType() != item.baseItem) return;
        if (!Util.isMVItem(consumedItem,item.key)) return;

        if (item.consumeEffect == null) return;

        Player player = event.getPlayer();

        boolean effected = false;

        if (item.consumeEffect.hungerValue > 0)
            player.setFoodLevel(player.getFoodLevel() + item.consumeEffect.hungerValue);

        for (MVPotionEffect effect : item.consumeEffect.potionEffects) {
            if (chanceOf(effect.chance)) {
                event.getPlayer().addPotionEffect(effect.getPotionEffect(), false);
                if (Config.DEBUG)
                    main.getLogger().info(String.format("&aCustom Item &6%s &cinflicted &e%s &cwith &4%s&c!", item.displayName, event.getPlayer().getName(), item.consumeEffect.effectDisplay));
                effected = true;
            }
        }
        if (effected)
            event.getPlayer().sendMessage(colorize(String.format("&7You ate %s &7and got effected with %s", item.displayName, item.consumeEffect.effectDisplay)));
    }
}
