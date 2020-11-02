package com.github.steeldev.monstrorvm.util;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.util.config.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class UpdateCheck {
    static Monstrorvm main = Monstrorvm.getInstance();
    JavaPlugin plugin;
    int resourceID;

    public UpdateCheck(JavaPlugin plugin,
                       int resourceID) {
        this.plugin = plugin;
        this.resourceID = resourceID;
    }

    public static void sendNewUpdateMessageToPlayer(Player player) {
        if (!player.isOp() && !player.hasPermission("betternetherite.admin")) return;

        if (!main.outdated) return;

        player.sendMessage(colorize(String.format("%s&a&oA new version is available! &7&o(Current: %s, Latest: %s)", Lang.PREFIX, main.getDescription().getVersion(), main.newVersion)));
        TextComponent link = new TextComponent(colorize("&6&lClick here to update"));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/better-netherite.84526"));
        player.spigot().sendMessage(link);
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL(String.format("https://api.spigotmc.org/legacy/update.php?resource=%d", this.resourceID)).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info(String.format("&cCannot look for updates: %s", exception.getMessage()));
            }
        });
    }
}
