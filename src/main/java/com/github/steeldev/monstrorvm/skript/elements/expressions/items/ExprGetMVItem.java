package com.github.steeldev.monstrorvm.skript.elements.expressions.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprGetMVItem extends SimpleExpression {

    static {
        Skript.registerExpression(ExprGetMVItem.class,
                MVItem.class,
                ExpressionType.SIMPLE,
                "[get] mvitem %string%");
    }

    Expression<String> itemid;

    @Nullable
    @Override
    protected MVItem[] get(Event event) {
        return new MVItem[]{ItemManager.getItem(this.itemid.getSingle(event))};
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
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "mvitem " + this.itemid;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        itemid = (Expression<String>) expressions[0];
        return true;
    }
}
