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

public class EffSetEXP extends Effect {

    static {
        Skript.registerEffect(EffSetEXP.class, "set (0¦death exp|1¦exp on death) of %mvmob% to %integer%");
    }

    Expression<MVMob> mob;

    Expression<Integer> exp;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            int exp = this.exp.getSingle(event).intValue();
            m.withCustomDeathEXP(exp);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "set death exp of " + mob.toString(event,b) + " to " + exp.getSingle(event);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        exp = (Expression<Integer>) expressions[1];
        return true;
    }
}
