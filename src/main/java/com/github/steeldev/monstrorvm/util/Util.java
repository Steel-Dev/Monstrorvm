package com.github.steeldev.monstrorvm.util;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.md_5.bungee.api.chat.TextComponent.fromLegacyText;

public class Util {
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    private static final String PREFIX = "&7[&2Monstrorvm&7] ";
    private static final String NBTAPI_PREFIX = "&7[&2NBT&aAPI&7]";

    public static Random rand = new Random();
    public static String latestLog;
    static Monstrorvm main = Monstrorvm.getInstance();

    public static String colorize(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        while (matcher.find()) {
            final net.md_5.bungee.api.ChatColor hexColor = net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            final String before = string.substring(0, matcher.start());
            final String after = string.substring(matcher.end());
            string = before + hexColor + after;
            matcher = HEX_PATTERN.matcher(string);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static boolean chanceOf(int chance) {
        return rand.nextInt(100) < chance;
    }

    public static boolean chanceOf(float chance) {
        return rand.nextFloat() < chance;
    }

    // Just to make, for example; 'DIAMOND_SWORD' look nicer in a string
    //  it'll look like 'Diamond Sword' instead. This is mainly used
    //  for printing an item in chat, and have it not look so
    //  ugly and such for users.
    public static String formalizedString(String string) {
        String[] itemSplit = string.toLowerCase().split("_");
        StringBuilder finalIt = new StringBuilder();
        for (int i = 0; i < itemSplit.length; i++) {
            finalIt.append(itemSplit[i].substring(0, 1).toUpperCase() + itemSplit[i].substring(1));
            if (i < itemSplit.length - 1)
                finalIt.append(" ");
        }
        return finalIt.toString();
    }

    public static int[] getRGB(final String rgb) {
        final int[] ret = new int[3];
        for (int i = 0; i < 3; i++) {
            ret[i] = Integer.parseInt(rgb.substring(i * 2, i * 2 + 2), 16);
        }
        return ret;
    }

    public static String getNbtapiPrefix() {
        return NBTAPI_PREFIX;
    }

    public static String getPrefix() {
        return PREFIX;
    }

    public static String getUncoloredItemName(ItemStack item) {
        String name = (item.getItemMeta() == null) ? formalizedString(item.getType().toString()) : item.getItemMeta().getDisplayName();
        return ChatColor.stripColor(name);
    }

    public static void log(String log) {
        Bukkit.getConsoleSender().sendMessage(colorize(PREFIX + log));
        latestLog = log;
    }

    private static void send(CommandSender receiver, String format, Object... objects) {
        receiver.sendMessage(colorize(String.format(format, objects)));
    }

    public static void log(String format, Object... objects) {
        Bukkit.getConsoleSender().sendMessage(colorize(PREFIX + String.format(format, objects)));
        latestLog = String.format(format, objects);
    }

    public static void sendMessage(CommandSender receiver, String format, Object... objects) {
        if (receiver == null || receiver instanceof ConsoleCommandSender) {
            log(format, objects);
        } else {
            send(receiver, format, objects);
        }
    }

    public static void sendActionBar(Player receiver, String format, Object... objects) {
        if (receiver == null) return;
        receiver.spigot().sendMessage(ChatMessageType.ACTION_BAR, fromLegacyText(colorize(String.format(format, objects))));
    }

    public static void sendTitle(Player receiver, String title, String format, Object... objects) {
        if (receiver == null) return;
        receiver.sendTitle(colorize(title), colorize(String.format(format, objects)));
    }

    public static void broadcast(String format, Object... params) {
        Bukkit.getServer().broadcastMessage(colorize(String.format(format, params)));
    }

    public static boolean isMVMob(Entity mob) {
        return mob.getPersistentDataContainer().has(MobManager.customMobKey, PersistentDataType.STRING);
    }

    public static boolean isMVMob(Entity mob, String customMobKey) {
        if (isMVMob(mob))
            return mob.getPersistentDataContainer().get(MobManager.customMobKey, PersistentDataType.STRING).equals(customMobKey);

        return false;
    }

    public static boolean isMVItem(ItemStack item) {
        NBTItem itemNBT = new NBTItem(item);

        return itemNBT.hasKey("MVItem");
    }

    public static boolean isMVItem(ItemStack item, String customItemKey) {
        NBTItem itemNBT = new NBTItem(item);

        if (isMVItem(item))
            return itemNBT.getString("MVItem").equals(customItemKey);

        return false;
    }

    public static void unregisterEvents(Listener listener) {
        if (main == null) main = Monstrorvm.getInstance();
        HandlerList.unregisterAll(listener);
    }

    public static void registerEvent(Listener listener) {
        if (main == null) main = Monstrorvm.getInstance();
        main.getServer().getPluginManager().registerEvents(listener, main);
    }

    public static void registerCommand(String command, CommandExecutor commandExecutor) {
        if (main == null) main = Monstrorvm.getInstance();
        main.getCommand(command).setExecutor(commandExecutor);
    }
}
