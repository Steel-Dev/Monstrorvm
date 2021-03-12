package com.github.steeldev.monstrorvm.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class Message {
    // Messages
    public static final Message PLUGIN_ENABLED = get("&aSuccessfully enabled &2%s &ain &e%s Seconds&a.");
    public static final Message PLUGIN_DISABLED = get("&cSuccessfully disabled!");

    public static final Message PLUGIN_CHECKING_FOR_UPDATE = get("&e&oChecking for a new version...");
    public static final Message PLUGIN_ON_LATEST = get("&2&oYou are on the latest version! &7&o(%s)");
    public static final Message PLUGIN_ON_IN_DEV_PREVIEW = get("&e&oYou are on an in-dev preview version! &7&o(%s)");
    public static final Message PLUGIN_NEW_VERSION_AVAILABLE_CONSOLE = get("&a&oA new version is available! &7&o(Current: %s, Latest: %s) &a&oYou can download the latest version here: &e&o%s");
    public static final Message PLUGIN_NEW_VERSION_AVAILABLE_CHAT = get("&a&oA new version is available! &7&o(Current: %s, Latest: %s)");
    public static final Message PLUGIN_NEW_VERSION_AVAILABLE_CHAT_CLICK = get("&6&lClick here to update");
    public static final Message PLUGIN_UPDATE_CHECK_FAILED = get("&4Failed to check for updates: &c%s");

    public static final Message LOADING_NBT_API = get("&aLoading NBT-API...");
    public static final Message NBT_API_LOADED = get("&aSuccessfully loaded NBT-API!");

    public static final Message STARTING_METRICS = get("&7Starting Metrics. Opt-out using the global bStats config.");

    public static final Message PLUGIN_RELOADING = get("&aReloading configurations, mobs and items...");
    public static final Message PLUGIN_RELOADED = get("&aSuccessfully reloaded all configurations, mobs and items!");
    public static final Message PLUGIN_RELOAD_FAILED = get("&cOh no! An unexpected error occured when attempting to reload! Please check console and report this to the developer!");

    public static final Message DOCS = get("&aYou can view documentation here:");

    public static final Message RELOADED_ITEM_ERRORS = get("&c%s &4errors occurred while reloading custom items!");
    public static final Message RELOADED_ITEM_WARNINGS = get("&6%s &ewarnings occurred while reloading custom items!");

    public static final Message RELOADED_MOB_ERRORS = get("&c%s &4errors occurred while reloading custom mobs!");
    public static final Message RELOADED_MOB_WARNINGS = get("&6%s &ewarnings occurred while reloading custom mobs!");

    public static final Message ONLY_PLAYERS_CAN_EXECUTE = get("&cThis command can only be executed by a player.");
    public static final Message PLAYER_NOT_ONLINE = get("&cThe specified player isnt online!");

    public static final Message EXPECTED_NUMBER = get("&cExpected a number.");

    public static final Message MOB_NOT_VALID = get("&cThe specific mob id, &e%s,&c doesnt exist, or isnt registered!");

    public static final Message MOB_SPAWNED = get("&aSuccessfully spawned a %s&a!");
    public static final Message MOB_FAILED_SPAWNED = get("&cFailed to spawn a %s&c! Check console for any errors and report them, please!");

    public static final Message MOB_KILL_FAILED = get("&cFailed to kill all MV Mobs as there are none spawned.");
    public static final Message MOBS_KILLED = get("&aSuccessfully killed &e%s&a Custom MV Mobs!");

    public static final Message GIVEN_ITEM = get("&aSuccessfully given &7x%s &6%s &ato &e%s&a!");
    public static final Message GIVE_ITEM_FAIL_FULL_INV = get("&cUnable to give &7x%s &6%s &ato &e%s &cas their inventory may be full.");

    public static final Message ITEM_DOESNT_EXIST = get("&cThe specified item id, &e%s,&c doesnt exist, or isnt registered!");

    public static final Message ITEM_REGISTERED_BY = get("&aCustom item &emonstrorvm:%s&a has been &2registered by &2%s&a.");
    public static final Message ITEM_REGISTERED = get("&aCustom item &emonstrorvm:%s&a has been &2registered&a.");
    public static final Message MOB_REGISTERED_BY = get("&aCustom mob &emonstrorvm:%s&a has been &2registered by &2%s&a.");
    public static final Message MOB_REGISTERED = get("&aCustom mob &emonstrorvm:%s&a has been &2registered&a.");

    public static final Message MOB_SPAWNED_DEBUG = get("&aCustom Mob &6%s &aspawned at &e%s,%s,%s &ain &e%s&a!");

    public static final Message MOB_INFLICTED_DEBUG = get("&aCustom Mob &6%s &cinflicted &e%s &cwith &4%s&c!");
    public static final Message ITEM_INFLICTED_DEBUG = get("&aCustom Item &6%s &cinflicted &e%s &cwith &4%s&c!");
    public static final Message ITEM_INFLICTED_HELD_BY_DEBUG = get("&aCustom Item &6%s &aheld by &e%s &cinflicted &e%s &cwith &4%s&c!");

    public static final Message ATE_AND_GOT_EFFECTED = get("&7You ate %s &7and got effected with %s&7.");

    // Message code
    private final String message;

    public Message(String message) {
        this.message = message;
    }

    private static Message get(String message) {
        return new Message(message);
    }

    public void sendActionBar(@Nullable CommandSender receiver, Object... params) {
        if (!(receiver instanceof Player)) return;
        Util.sendActionBar((Player) receiver, message, params);
    }

    public void sendTitle(String title, @Nullable CommandSender receiver, Object... params) {
        if (!(receiver instanceof Player)) return;
        Util.sendActionBar((Player) receiver, message, params);
    }

    public void broadcast(boolean withPrefix, Object... params) {
        Util.broadcast((withPrefix) ? Util.main.config.PREFIX + message : message, params);
    }

    public void send(@Nullable CommandSender receiver, boolean withPrefix, Object... params) {
        String finalMsg;
        if (withPrefix) finalMsg = Util.main.config.PREFIX + message;
        else finalMsg = message;
        Util.sendMessage(receiver, finalMsg, params);
    }

    public void log(Object... params) {
        Util.log(message, params);
    }

    public String toString() {
        return Util.colorize(this.message);
    }
}
