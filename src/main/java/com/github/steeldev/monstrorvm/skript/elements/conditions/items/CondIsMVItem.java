package com.github.steeldev.monstrorvm.skript.elements.conditions.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.MobManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class CondIsMVItem extends Condition {

    static {
        Skript.registerCondition(CondIsMVItem.class, "%itemstack% [is] [(a|an)] mvitem");
    }

    Expression<ItemStack> item;

    @Override
    public boolean check(Event event) {
        ItemStack itemStack = item.getSingle(event);
        NBTItem itemStackNBT = new NBTItem(itemStack);
        if(itemStackNBT.hasKey("MVItem"))
            return true;
        return false;
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        item = (Expression<ItemStack>) expressions[0];
        return true;
    }
}
