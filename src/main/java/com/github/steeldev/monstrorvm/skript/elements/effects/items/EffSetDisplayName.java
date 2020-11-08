package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSetDisplayName extends Effect {

    static{
        Skript.registerEffect(EffSetDisplayName.class, "set name of mvitem %mvitem% to %string%");
    }

    Expression<MVItem> item;
    Expression<String> name;

    @Override
    protected void execute(Event event) {
        MVItem i = this.item.getSingle(event);
        String name = this.name.getSingle(event);
        i.withDisplayName(name);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        name = (Expression<String>) expressions[1];
        return true;
    }
}
