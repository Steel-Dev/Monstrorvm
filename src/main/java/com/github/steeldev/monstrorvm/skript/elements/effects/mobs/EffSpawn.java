package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSpawn extends Effect {

    static {
        Skript.registerEffect(EffSpawn.class, "spawn mvmob %mvmob% at %location%");
    }

    Expression<MVMob> mob;
    Expression<Location> location;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        Location l = location.getSingle(event);
        ch.njol.skript.effects.EffSpawn.lastSpawned = m.spawnMob(l, null);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        location = (Expression<Location>) expressions[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "spawn mvmob " + this.mob.toString(event,b) + " at " + this.location.toString(event,b);
    }
}
