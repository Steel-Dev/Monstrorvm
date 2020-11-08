package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.SkriptColor;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.Nullable;

public class EffSetColor extends Effect {

    static {
        Skript.registerEffect(EffSetColor.class,
                "set color of mvitem %mvitem% to %color%",
                "set color of mvitem %mvitem% to [r] %integer% [g] %integer% [b] %integer%");
    }
    Expression<MVItem> item;
    Expression<SkriptColor> color;
    Expression<Integer> colorR;
    Expression<Integer> colorG;
    Expression<Integer> colorB;

    boolean usingRGB;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        ItemMeta baseItemMeta = Bukkit.getItemFactory().getItemMeta(i.baseItem);
        if(!(baseItemMeta instanceof Colorable) && !(baseItemMeta instanceof LeatherArmorMeta))
            Skript.error("A color cannot be set on a non-colorable item.");
        else{
            if(!usingRGB)
                i.withColor(color.getSingle(event).asBukkitColor());
            else
                i.withColor(Color.fromRGB(colorR.getSingle(event).intValue(),colorG.getSingle(event).intValue(),colorB.getSingle(event).intValue()));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        if(i == 0)
            color = (Expression<SkriptColor>) expressions[1];
        else if(i == 1) {
            colorR = (Expression<Integer>) expressions[1];
            colorG = (Expression<Integer>) expressions[2];
            colorB = (Expression<Integer>) expressions[3];
            usingRGB = true;
        }
        return true;
    }
}
