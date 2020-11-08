package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class EffAddAttackEffect extends Effect {

    static {
        Skript.registerEffect(EffAddAttackEffect.class,
                "add %potioneffecttype% of tier %integer% for %integer% (0¦ticks|1¦seconds) with a chance of %integer% [percent] to attack effect of [mvitem] %mvitem%");
    }
    Expression<MVItem> item;
    Expression<PotionEffectType> type;
    Expression<Integer> tier;
    Expression<Integer> time;
    Expression<Integer> chance;

    boolean usingSeconds;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        PotionEffectType type = this.type.getSingle(event);
        int tier = this.tier.getSingle(event).intValue() - 1;
        int time = (usingSeconds) ? this.time.getSingle(event).intValue() * 20 : this.time.getSingle(event).intValue();
        int chance = this.chance.getSingle(event).intValue();
        i.withAttackEffect(new MVPotionEffect(type, chance, tier, time));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        type = (Expression<PotionEffectType>) expressions[0];
        tier = (Expression<Integer>) expressions[1];
        time = (Expression<Integer>) expressions[2];
        chance = (Expression<Integer>) expressions[3];
        item = (Expression<MVItem>) expressions[4];
        if (parseResult.mark == 0)
            usingSeconds = false;
        else if (parseResult.mark == 1)
            usingSeconds = true;
        return true;
    }
}