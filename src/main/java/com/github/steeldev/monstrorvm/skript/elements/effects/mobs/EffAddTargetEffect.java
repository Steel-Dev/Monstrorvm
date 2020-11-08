package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import com.github.steeldev.monstrorvm.util.mobs.MobTargetEffect;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAddTargetEffect extends Effect {

    static {
        Skript.registerEffect(EffAddTargetEffect.class,
                "add target effect to %mvmob% with [a] chance of %integer% [percent]");
    }

    Expression<MVMob> mob;

    Expression<Integer> chance;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            int chance = this.chance.getSingle(event).intValue();

            m.withTargetEffect(new MobTargetEffect(chance));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        chance = (Expression<Integer>) expressions[1];
        return true;
    }
}
