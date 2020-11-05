package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.events.bukkit.SkriptStartEvent;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.ErrorQuality;
import ch.njol.skript.util.VisualEffect;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.misc.MVParticle;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import com.github.steeldev.monstrorvm.util.mobs.MobTargetEffect;
import org.bukkit.Particle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffPartToTargetEffectOfMVMob extends Effect {

    static {
        Skript.registerEffect(EffPartToTargetEffectOfMVMob.class,
                "set particle effect of target effect of %mvmob% to %integer% [of] %string%");
    }

    Expression<MVMob> mob;

    Expression<Integer> amount;
    Expression<String> particle;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            if(m.targetEffect != null) {
                Particle particle = Particle.valueOf(this.particle.getSingle(event).toUpperCase().replace(" ", "_"));
                int amount = this.amount.getSingle(event).intValue();

                m.targetEffect.targetParticle = new MVParticle(particle, amount);
            }else{
                Skript.error("Cannot add particle information to a non-existing target effect! Be sure to set the target effect first!");
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
        mob = (Expression<MVMob>) expressions[0];
        amount = (Expression<Integer>) expressions[1];
        particle = (Expression<String>) expressions[2];
        return true;
    }
}
