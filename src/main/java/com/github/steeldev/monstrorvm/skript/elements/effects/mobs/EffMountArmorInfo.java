package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EffMountArmorInfo extends Effect {

    static {
        Skript.registerEffect(EffMountArmorInfo.class,
                "set possible armor of %mvmob%'s mount to %itemtypes% with [a] chance of %integer% [percent]");
    }

    Expression<MVMob> mob;

    Expression<ItemType> armorTypes;

    Expression<Integer> chance;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        if (MobManager.getMob(m.key) != null) {
            Skript.error("You cannot modify aspects of a mob after it's been registered!");
        } else {
            if(m.mountInfo != null) {
                int chance = this.chance.getSingle(event).intValue();
                ItemType[] mountArmor = this.armorTypes.getArray(event);
                List<Material> armorTypes = new ArrayList<>();
                for(ItemType item : mountArmor){
                    armorTypes.add(item.getMaterial());
                }
                m.mountInfo.armorTypes = armorTypes;
                m.mountInfo.armorChance = chance;
            }
            else{
                Skript.error("Cannot add armor information to a non-existing mount! Be sure to set the mount first!");
            }
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return "";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        mob = (Expression<MVMob>) expressions[0];
        armorTypes = (Expression<ItemType>) expressions[1];
        chance = (Expression<Integer>) expressions[2];
        return true;
    }
}
