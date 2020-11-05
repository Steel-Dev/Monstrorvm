package com.github.steeldev.monstrorvm.skript.elements.expressions.mobs;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewMVMob extends SimpleExpression {

    static {
        Skript.registerExpression(ExprNewMVMob.class,
                MVMob.class,
                ExpressionType.COMBINED,
                "[a] [new] mvmob of type %entitytype% with key %string% named %string% with [a] spawn chance of %integer% [percent]");
    }

    private Expression<EntityType> entityType;
    private Expression<String> key;
    private Expression<String> displayName;
    private Expression<Integer> spawnChance;

    @Nullable
    @Override
    protected MVMob[] get(Event event) {
        EntityType type = this.entityType.getSingle(event);
        String key = this.key.getSingle(event);
        String displayName = this.displayName.getSingle(event);
        int spawnChance = this.spawnChance.getSingle(event);
        return new MVMob[]{new MVMob(key, Util.convertEntityTypeFromSkriptToBukkit(type), displayName, spawnChance)};
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
        return "mvmob of type " + entityType.toString(event,b) + " with key " + key.getSingle(event) + " named " + displayName.getSingle(event) + " with spawn chance of " + spawnChance.getSingle(event);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("The creation of a custom mob can only be used in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        entityType = (Expression<EntityType>) expressions[0];
        key = (Expression<String>) expressions[1];
        displayName = (Expression<String>) expressions[2];
        spawnChance = (Expression<Integer>) expressions[3];
        return true;
    }
}
