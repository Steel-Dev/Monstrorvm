package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffAddOppEffToTargetEffect extends Effect {

    static {
        Skript.registerEffect(EffAddOppEffToTargetEffect.class,
                "add opponent effect %potioneffecttype% of tier %integer% for %integer% (0¦ticks|1¦seconds) with a chance of %integer% [percent] to target effect of %mvmob%");
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

                List<MVPotionEffect> curTargEffect = (m.targetEffect.targetEffects != null) ? m.targetEffect.targetEffects : new ArrayList<>();
                curTargEffect.add(new MVPotionEffect(type,chance,tier,time));

                m.targetEffect.targetEffects = curTargEffect;
            }
            else{
                Skript.error("Cannot add opponent effect information to a non-existing target effect! Be sure to set the target effect first!");
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
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
