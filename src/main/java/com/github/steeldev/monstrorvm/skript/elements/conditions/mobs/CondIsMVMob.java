package com.github.steeldev.monstrorvm.skript.elements.conditions.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class CondIsMVMob extends Condition {

    static {
        Skript.registerCondition(CondIsMVMob.class, "%entity% [is] [(a|an)] mvmob");
    }

    Expression<Entity> entity;

    @Override
    public boolean check(Event event) {
        Entity ent = entity.getSingle(event);
        if(ent instanceof LivingEntity){
            return ent.getPersistentDataContainer().has(MobManager.customMobKey,PersistentDataType.STRING);
        }
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return entity.getSingle(event) + " is an mvmob";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        entity = (Expression<Entity>) expressions[0];
        return true;
    }
}
