package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.ItemConsumeEffect;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAddConsumeEffect extends Effect {

    static {
        Skript.registerEffect(EffAddConsumeEffect.class,
                "add consume effect with display %string% to [mvitem] %mvitem%");
    }
    Expression<MVItem> item;
    Expression<String> display;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        if(!i.baseItem.isEdible())
            Skript.error("A consume effect can only be added to an editable item!");
        else
            i.withConsumeEffect(new ItemConsumeEffect(display.getSingle(event)));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[1];
        display = (Expression<String>) expressions[0];
        return true;
    }
}
