package com.github.steeldev.monstrorvm.skript.elements.expressions.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprGetMVMob extends SimpleExpression {

    static {
        Skript.registerExpression(ExprGetMVMob.class,
                MVMob.class,
                ExpressionType.SIMPLE,
                "mvmob %string%");
    }

    Expression<String> mobid;

    @Nullable
    @Override
    protected MVMob[] get(Event event) {
        return new MVMob[]{MobManager.getMob(this.mobid.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return MVMob.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "mvmob " + this.mobid;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mobid = (Expression<String>) expressions[0];
        return true;
    }
}
