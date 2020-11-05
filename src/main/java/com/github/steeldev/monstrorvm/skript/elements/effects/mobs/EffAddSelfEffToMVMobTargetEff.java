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
import com.github.steeldev.monstrorvm.util.mobs.MobTargetEffect;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffAddSelfEffToMVMobTargetEff extends Effect {

    static {
        Skript.registerEffect(EffAddSelfEffToMVMobTargetEff.class,
                "add self effect %potioneffecttype% of tier %integer% for %integer% (0¦ticks|1¦seconds) with a chance of %integer% [percent] to target effect of %mvmob%");
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
            if(m.targetEffect != null) {
                PotionEffectType type = this.type.getSingle(event);
                int tier = this.tier.getSingle(event).intValue() - 1;
                int time = (usingSeconds) ? this.time.getSingle(event).intValue() * 20 : this.time.getSingle(event).intValue();
                int chance = this.chance.getSingle(event).intValue();

                List<MVPotionEffect> curSelfEffects = (m.targetEffect.selfEffects != null) ? m.targetEffect.selfEffects : new ArrayList<>();
                curSelfEffects.add(new MVPotionEffect(type,chance,tier,time));

                m.targetEffect.selfEffects = curSelfEffects;
            }
            else{
                Skript.error("Cannot add self effect information to a non-existing target effect! Be sure to set the target effect first!");
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(SkriptStartEvent.class)) {
            Skript.error("You can only modify aspects of a custom mob in Skript Load events.", ErrorQuality.SEMANTIC_ERROR);
            return false;
        }

        type = (Expression<PotionEffectType>) expressions[0];
        tier = (Expression<Integer>) expressions[1];
        time = (Expression<Integer>) expressions[2];
        chance = (Expression<Integer>) expressions[3];
        if (parseResult.mark == 0)
            usingSeconds = false;
        else if (parseResult.mark == 1)
            usingSeconds = true;
        mob = (Expression<MVMob>) expressions[4];
        return true;
    }
}
