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

public class EffSetAnger extends Effect {

    static {
        Skript.registerEffect(EffSetAnger.class, "set (0¦always angry|1¦anger) of %mvmob% to %boolean%");
    }

    Expression<MVMob> mob;

    Expression<Boolean> angry;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            boolean angry = this.angry.getSingle(event).booleanValue();
            m.withAnger(angry);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "set anger of " + mob.toString(event,b) + " to " + angry.getSingle(event);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        angry = (Expression<Boolean>) expressions[1];
        return true;
    }
}
