package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.ItemUseEffectType;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSetMobToSpawn extends Effect {

    static {
        Skript.registerEffect(EffSetMobToSpawn.class, "set mob [id] to spawn of use effect of %mvitem% to %string%");
    }
    Expression<MVItem> item;
    Expression<String> mobToSpawn;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        if(i.useEffect.type != ItemUseEffectType.SPAWN_CUSTOM_MOB){
            Skript.error("Cannot assign a custom mob to spawn to this items use effect as it isn't set to spawn a custom mob!");
        }else {
            i.useEffect.mobID = mobToSpawn.getSingle(event);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        mobToSpawn = (Expression<String>) expressions[1];
        return true;
    }
}
