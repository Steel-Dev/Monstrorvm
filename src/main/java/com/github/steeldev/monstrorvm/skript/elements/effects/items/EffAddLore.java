package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EffAddLore extends Effect {

    static{
        Skript.registerEffect(EffAddLore.class, "add %strings% to [the] lore of mvitem %mvitem%");
    }

    Expression<MVItem> item;
    Expression<String> lore;

    @Override
    protected void execute(Event event) {
        MVItem i = this.item.getSingle(event);
        List<String> lore = Arrays.asList(this.lore.getAll(event));
        for(String line : lore){
            i.withLore(line);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        lore = (Expression<String>) expressions[0];
        item = (Expression<MVItem>) expressions[1];
        return true;
    }
}
