package com.github.steeldev.monstrorvm.skript.elements.expressions.items;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprNewMVItem extends SimpleExpression {

    static {
        Skript.registerExpression(ExprNewMVItem.class,
                MVItem.class,
                ExpressionType.SIMPLE,
                "[a] [new] mvitem of type %itemtype% with key %string%",
                "[a] [new] mvitem of type %itemtype% with key %string% with [item] category %string%");
    }

    Expression<ItemType> type;
    Expression<String> key;

    Expression<String> category;

    @Nullable
    @Override
    protected MVItem[] get(Event event) {
        Material item = this.type.getSingle(event).getMaterial();
        String key = this.key.getSingle(event);
        String category = "";
        if(this.category != null)
            category = this.category.getSingle(event);

        MVItem mvitem = new MVItem(key,item);

        if(category != null && !category.equals(""))
            mvitem.withCategory(category);

        return new MVItem[]{mvitem};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return MVItem.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("The creation of a custom item can only be used in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        type = (Expression<ItemType>) expressions[0];
        key = (Expression<String>) expressions[1];
        if(i == 1)
            category = (Expression<String>) expressions[2];
        return true;
    }
}
