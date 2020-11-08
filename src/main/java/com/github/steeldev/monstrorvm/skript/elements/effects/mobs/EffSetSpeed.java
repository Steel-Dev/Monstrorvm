package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSetSpeed extends Effect {

    static {
        Skript.registerEffect(EffSetSpeed.class, "set move speed of %mvmob% to %float%");
    }

    Expression<MVMob> mob;

    Expression<Float> moveSpeed;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            float moveSpeed = this.moveSpeed.getSingle(event).floatValue();
            m.withCustomMoveSpeed(moveSpeed);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "set move speed of " + mob.toString(event,b) + " to " + moveSpeed.getSingle(event);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        moveSpeed = (Expression<Float>) expressions[1];
        return true;
    }
}
