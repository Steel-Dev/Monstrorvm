package com.github.steeldev.monstrorvm.skript.elements.expressions.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMVMobInfo extends SimpleExpression {

    static {
        Skript.registerExpression(ExprMVMobInfo.class,
                String.class,
                ExpressionType.COMBINED,
                "%mvmob%'s (0¦hp|1¦move speed|2¦death exp|3¦spawn environments|4¦anger|5¦display name|6¦key|7¦spawn chance)");
    }

    Expression<MVMob> mob;
    int infoReq;

    @Nullable
    @Override
    protected String[] get(Event event) {
        return getValue(event);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return this.mob.toString(event,b) + "'s " + getValue(event).toString();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        infoReq = parseResult.mark;
        return true;
    }

    String[] getValue(Event event){
        MVMob m = mob.getSingle(event);
        if(m == null) return null;
        switch(infoReq){
            case 0:
                return new String[]{String.valueOf(m.maxHP)};
            case 1:
                return new String[]{String.valueOf(m.moveSpeed)};
            case 2:
                return new String[]{String.valueOf(m.deathEXP)};
            case 3:
                return new String[]{String.valueOf(m.validSpawnWorlds)};
            case 4:
                return new String[]{String.valueOf(m.angry)};
            case 5:
                return new String[]{String.valueOf(m.entityName)};
            case 6:
                return new String[]{String.valueOf(m.key)};
            case 7:
                return new String[]{String.valueOf(m.spawnChance)};
        }
        return null;
    }
}
