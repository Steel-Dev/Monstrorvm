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
import com.github.steeldev.monstrorvm.util.mobs.BurningInfo;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffMVMobBurnInfo extends Effect {

    static {
        Skript.registerEffect(EffMVMobBurnInfo.class, "add burning effect to %mvmob% for(0¦ %integer% tick[s]|1¦ %integer% second[s]|2¦ever)");
    }

    Expression<MVMob> mob;

    Expression<Integer> time;

    boolean usingSeconds;
    boolean forever;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            int finalTime = (forever) ? Integer.MAX_VALUE : (usingSeconds) ? (time.getSingle(event).intValue() * 20) : time.getSingle(event).intValue();
            m.withBurningEffect(new BurningInfo(true,finalTime));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "add burning effect to " + mob.toString(event,b) + ((forever) ? "forever" : " for " + ((usingSeconds) ? (this.time.getSingle(event).intValue() * 20) + " seconds" : (this.time.getSingle(event).intValue()) + " ticks"));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("You can only modify aspects of a custom mob in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        mob = (Expression<MVMob>) expressions[0];
        if(parseResult.mark == 0)
            usingSeconds = false;
        else if(parseResult.mark == 1)
            usingSeconds = true;
        else if(parseResult.mark == 2){
            usingSeconds = false;
            forever = true;
        }
        if(expressions.length > 1)
            time = (Expression<Integer>) expressions[1];
        return true;
    }
}
