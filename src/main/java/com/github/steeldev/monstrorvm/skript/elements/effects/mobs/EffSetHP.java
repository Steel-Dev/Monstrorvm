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

public class EffSetHP extends Effect {

    static {
        Skript.registerEffect(EffSetHP.class, "set [max] hp of %mvmob% to %float%");
    }

    Expression<MVMob> mob;

    Expression<Float> hp;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            float hp = this.hp.getSingle(event).floatValue();
            m.withCustomMaxHP(hp);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "set max hp of " + mob.toString(event,b) + " to " + hp.getSingle(event);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        hp = (Expression<Float>) expressions[1];
        return true;
    }
}
