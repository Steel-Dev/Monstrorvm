package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.ItemAttributeInfo;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class EffAddAttribute extends Effect {

    static {
        Skript.registerEffect(EffAddAttribute.class, "add attribute %string% of value %integer% in [equipment] slot %string% to [mvitem] %mvitem%");
    }

    Expression<String> attribute;
    Expression<Integer> value;
    Expression<MVItem> item;
    Expression<String> slot;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        Attribute attribute = Attribute.valueOf(this.attribute.getSingle(event).toUpperCase().replace(" ", "_"));
        String attModName = attribute.toString().toLowerCase().replace("generic_", "generic.");
        int value = this.value.getSingle(event).intValue();
        EquipmentSlot slot = EquipmentSlot.valueOf(this.slot.getSingle(event).toUpperCase().replace(" ", "_"));
        i.withAttribute(new ItemAttributeInfo(attModName,slot,attribute,value));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        attribute = (Expression<String>) expressions[0];
        value = (Expression<Integer>) expressions[1];
        slot = (Expression<String>) expressions[2];
        item = (Expression<MVItem>) expressions[3];
        return true;
    }
}
