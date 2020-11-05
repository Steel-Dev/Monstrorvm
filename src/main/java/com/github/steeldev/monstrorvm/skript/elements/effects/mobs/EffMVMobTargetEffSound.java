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
import com.github.steeldev.monstrorvm.util.misc.MVSound;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import com.github.steeldev.monstrorvm.util.mobs.MobTargetEffect;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffMVMobTargetEffSound extends Effect {

    static {
        Skript.registerEffect(EffMVMobTargetEffSound.class,
                "set target sound of target effect of %mvmob% to %string% in %string% category with [a] volume of %float% and [a] pitch of %float%");
    }

    Expression<MVMob> mob;

    Expression<String> sound;
    Expression<String> soundCategory;
    Expression<Float> volume;
    Expression<Float> pitch;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            if (m.targetEffect != null) {
                Sound sound = Sound.valueOf(this.sound.getSingle(event).toUpperCase().replace(" ", "_"));
                SoundCategory category = SoundCategory.valueOf(this.soundCategory.getSingle(event).toUpperCase().replace(" ", "_"));
                float volume = this.volume.getSingle(event).floatValue();
                float pitch = this.pitch.getSingle(event).floatValue();

                m.targetEffect.targetSound = new MVSound(sound, category, volume, pitch);
            } else {
                Skript.error("Cannot add sound information to a non-existing target effect! Be sure to set the target effect first!");
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
        sound = (Expression<String>) expressions[1];
        soundCategory = (Expression<String>) expressions[2];
        volume = (Expression<Float>) expressions[3];
        pitch = (Expression<Float>) expressions[4];
        return true;
    }
}
