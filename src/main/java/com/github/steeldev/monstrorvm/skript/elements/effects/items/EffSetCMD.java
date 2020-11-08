package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSetCMD extends Effect {

    static{
        Skript.registerEffect(EffSetCMD.class, "set mvitem %mvitem%'s custom model data to %integer%");
    }

    Expression<MVItem> item;
    Expression<Integer> cmd;

    @Override
    protected void execute(Event event) {
        MVItem i = this.item.getSingle(event);
        int cmd = this.cmd.getSingle(event).intValue();
        i.withCustomModelData(cmd);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<MVItem>) expressions[0];
        cmd = (Expression<Integer>) expressions[1];
        return true;
    }
}
