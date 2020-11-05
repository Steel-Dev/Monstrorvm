package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSetMVMobSpawnEnvs extends Effect {

    static {
        Skript.registerEffect(EffSetMVMobSpawnEnvs.class, "set [valid] spawn environment[s] of %mvmob% to %strings%");
    }

    Expression<MVMob> mob;

    Expression<String> environments;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            String[] envs = environments.getArray(event);
            for (String environment : envs) {
                World.Environment environmentToAdd = World.Environment.NORMAL;
                if (environment.toLowerCase().equals("end") || environment.toLowerCase().equals("the end"))
                    environmentToAdd = World.Environment.THE_END;
                else if (environment.toLowerCase().equals("nether"))
                    environmentToAdd = World.Environment.NETHER;
                m.withValidSpawnWorld(environmentToAdd);
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "set spawn environments of " + mob.toString(event,b) + " to " + environments.getArray(event);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("You can only modify aspects of a custom mob in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        mob = (Expression<MVMob>) expressions[0];
        environments = (Expression<String>) expressions[1];
        return true;
    }
}
