package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.BabyInfo;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffBabyInfo extends Effect {

    static {
        Skript.registerEffect(EffBabyInfo.class, "make %mvmob% (0¦unable|1¦able) to be a baby [with [a] chance of %integer% [percent]]");
    }

    Expression<MVMob> mob;

    boolean canBeBaby;
    Expression<Integer> chance;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            boolean canbe = this.canBeBaby;
            int chance = this.chance.getSingle(event).intValue();
            m.setBaby(new BabyInfo(canbe,chance));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "make " + mob.toString(event,b) + ((canBeBaby) ? " able " : " unable ") + " to be a baby " + ((canBeBaby) ? "with a chance of " + chance.getSingle(event) : "");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        if(parseResult.mark == 0)
            canBeBaby = false;
        else if(parseResult.mark == 1)
            canBeBaby = true;
        chance = (Expression<Integer>) expressions[1];
        return true;
    }
}
