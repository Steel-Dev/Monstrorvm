package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffRemoveFromDrops extends Effect {

    static {
        Skript.registerEffect(EffRemoveFromDrops.class, "remove %itemtypes% from drops of %mvmob%");
    }

    Expression<MVMob> mob;

    Expression<ItemType> materials;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            ItemType[] drops = materials.getArray(event);
            for(ItemType type : drops){
                m.withDropToRemove(type.getMaterial());
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "remove " + materials.getArray(event) + " from drops of " + mob.toString(event,b);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        materials = (Expression<ItemType>) expressions[0];
        mob = (Expression<MVMob>) expressions[1];
        return true;
    }
}
