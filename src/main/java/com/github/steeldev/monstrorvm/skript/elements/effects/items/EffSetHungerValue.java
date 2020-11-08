package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSetHungerValue extends Effect {

    static {
        Skript.registerEffect(EffSetHungerValue.class,
                "set [custom] hunger value of [the] consume effect of [mvitem] %mvitem% to %integer%");
    }
    Expression<MVItem> item;
    Expression<Integer> hungerVal;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        if(i.consumeEffect == null)
            Skript.error("Cannot edit hunger value of a non existent consume effect!");
        else
            i.consumeEffect.hungerValue = hungerVal.getSingle(event).intValue();
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        hungerVal = (Expression<Integer>) expressions[1];
        return true;
    }
}
