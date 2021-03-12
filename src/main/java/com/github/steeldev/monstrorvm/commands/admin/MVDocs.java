package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class MVDocs implements CommandExecutor {
    final Monstrorvm main = Monstrorvm.getInstance();
    String docLink = "https://github.com/Steel-Dev/Monstrorvm/wiki";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Message.DOCS.send(sender, true);
        if(sender instanceof Player) {
            Player player = (Player) sender;
            TextComponent link = new TextComponent(docLink);
            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, docLink));
            player.spigot().sendMessage(link);
        }else{
            sender.sendMessage(docLink);
        }
        return true;
    }
}
