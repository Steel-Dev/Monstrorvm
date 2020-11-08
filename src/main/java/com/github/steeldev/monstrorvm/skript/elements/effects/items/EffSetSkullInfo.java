package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

public class EffSetSkullInfo extends Effect {

    static {
        Skript.registerEffect(EffSetSkullInfo.class,
                "set skull owner of mvitem %mvitem% to (0¦player|1¦base64) %string%");
    }
    Expression<MVItem> item;
    Expression<String> input;

    boolean usingBase64;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        ItemMeta baseItemMeta = Bukkit.getItemFactory().getItemMeta(i.baseItem);
        String input = this.input.getSingle(event);
        if(!(baseItemMeta instanceof SkullMeta))
            Skript.error("Cannot assign a skull owner/base 64 to a non-skull item!");
        else{
            if(usingBase64)
                i.withSkullOwnerByBase64(input);
            else
                i.withSkullOwnerByName(input);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        input = (Expression<String>) expressions[1];
        if(parseResult.mark == 1)
            usingBase64 = true;
        return true;
    }
}
