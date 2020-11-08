package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.skript.elements.effects.mobs.EffRegisterMVMob;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffRegisterMVItem  extends Effect {

    static {
        Skript.registerEffect(EffRegisterMVItem.class, "register [a] [new] mvitem %mvitem%");
    }

    Expression<MVItem> item;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        if (ItemManager.getItem(i.key) != null) {
            Skript.error("You cannot register multiple items under the same key!");
        } else {
            ItemManager.registerNewItem(i, Skript.getInstance());
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "register mvitem " + item.toString(event,b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("The registration of a custom item can only be used in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        item = (Expression<MVItem>) expressions[0];
        return true;
    }
}
