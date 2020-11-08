package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.ItemNBTCompound;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAddNBTCompound extends Effect {

    static {
        Skript.registerEffect(EffAddNBTCompound.class,
                "add nbt compound with key %string% and value %object% to mvitem %mvitem%");
    }
    Expression<MVItem> item;
    Expression<String> key;
    Expression<Object> value;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        String key = this.key.getSingle(event);
        Object value = this.value.getSingle(event);
        i.withNBT(new ItemNBTCompound(key,value));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        key = (Expression<String>) expressions[0];
        value = (Expression<Object>) expressions[1];
        item = (Expression<MVItem>) expressions[2];
        return true;
    }
}
