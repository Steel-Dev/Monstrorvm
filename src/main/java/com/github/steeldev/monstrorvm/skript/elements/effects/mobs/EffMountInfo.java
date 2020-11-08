package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import com.github.steeldev.monstrorvm.util.mobs.MountInfo;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffMountInfo extends Effect {

    static {
        Skript.registerEffect(EffMountInfo.class,
                "set %entitytype% as %mvmob%'s [possible] mount with [a] chance of %integer% [percent]");
    }

    Expression<MVMob> mob;

    Expression<EntityType> mount;

    Expression<Integer> chance;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            org.bukkit.entity.EntityType actType = Util.convertEntityTypeFromSkriptToBukkit(this.mount.getSingle(event));
            int chance = this.chance.getSingle(event).intValue();

            m.withMount(new MountInfo(actType, chance));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mount = (Expression<EntityType>) expressions[0];
        mob = (Expression<MVMob>) expressions[1];
        chance = (Expression<Integer>) expressions[2];
        return true;
    }
}