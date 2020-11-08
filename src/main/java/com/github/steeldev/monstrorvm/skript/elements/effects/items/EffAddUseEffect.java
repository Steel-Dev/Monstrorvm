package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.ItemUseEffect;
import com.github.steeldev.monstrorvm.util.items.ItemUseEffectType;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAddUseEffect extends Effect {

    static {
        Skript.registerEffect(EffAddUseEffect.class,
                "add use effect that (0¦effects user|1¦effects clicked|2¦spawns custom mob) to [mvitem] %mvitem%",
                "add use effect that (0¦effects user|1¦effects clicked|2¦spawns custom mob) to [mvitem] %mvitem% [that consumes the item on use]");
    }
    Expression<MVItem> item;
    ItemUseEffectType type;
    boolean consume;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        i.withUseEffect(new ItemUseEffect(type,consume));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        if(parseResult.mark == 0)
            type = ItemUseEffectType.EFFECT_HOLDER;
        else if(parseResult.mark == 1)
            type = ItemUseEffectType.EFFECT_CLICKED;
        else if (parseResult.mark == 2)
            type = ItemUseEffectType.SPAWN_CUSTOM_MOB;

        if(i == 1)
            consume = true;
        else
            consume = false;
        return true;
    }
}
