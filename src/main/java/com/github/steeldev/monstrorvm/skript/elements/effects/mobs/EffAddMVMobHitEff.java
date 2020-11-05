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
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class EffAddMVMobHitEff extends Effect {

    static {
        Skript.registerEffect(EffAddMVMobHitEff.class,
                "add [hit effect] to %mvmob% of type %potioneffecttype% of tier %integer% with a chance of %integer% [percent] for %integer% (0¦ticks|1¦seconds)");
    }

    Expression<MVMob> mob;

    Expression<PotionEffectType> type;
    Expression<Integer> tier;
    Expression<Integer> time;
    Expression<Integer> chance;

    boolean usingSeconds;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            PotionEffectType type = this.type.getSingle(event);
            int tier = this.tier.getSingle(event).intValue()-1;
            int time = (usingSeconds) ? this.time.getSingle(event).intValue() * 20 : this.time.getSingle(event).intValue();
            int chance = this.chance.getSingle(event).intValue();

            m.withHitEffect(new MVPotionEffect(type,chance,tier,time));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "add " + type.getSingle(event) + " of tier " + (tier.getSingle(event).intValue()-1) + " with a chance of " + chance.getSingle(event).intValue() + " percent for " + ((usingSeconds) ? (this.time.getSingle(event).intValue() * 20) + " seconds" : (this.time.getSingle(event).intValue()) + " ticks") + " to " + mob.toString(event,b) + "'s hit effects";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("You can only modify aspects of a custom mob in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }
        mob = (Expression<MVMob>) expressions[0];
        type = (Expression<PotionEffectType>) expressions[1];
        tier = (Expression<Integer>) expressions[2];
        chance = (Expression<Integer>) expressions[3];
        if (parseResult.mark == 0)
            usingSeconds = false;
        else if (parseResult.mark == 1)
            usingSeconds = true;
        time = (Expression<Integer>) expressions[4];
        return true;
    }
}
