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

public class EffDeathExplosion extends Effect {

    static {
        Skript.registerEffect(EffDeathExplosion.class,
                "add death explosion [effect] to %mvmob% with [a] size of %integer% and [a] chance of %integer% [percent] that (0¦creates fire|1¦doesnt create fire)");
    }

    Expression<MVMob> mob;

    Expression<Integer> size;
    Expression<Integer> chance;
    boolean createsFire;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            m.withDeathExplosion(true, chance.getSingle(event).intValue(),size.getSingle(event).intValue(),createsFire);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "add death explosion effect to " + mob.toString(event,b) + " with a size of " + size.getSingle(event).intValue() + " and a chance of " + chance.getSingle(event).intValue() + " that " + ((createsFire) ? "creates fire" : "doesnt create fire");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        if(parseResult.mark == 0)
            createsFire = true;
        else if(parseResult.mark == 1)
            createsFire = false;
        size = (Expression<Integer>) expressions[1];
        chance = (Expression<Integer>) expressions[2];
        return true;
    }
}
