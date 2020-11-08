package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.github.steeldev.monstrorvm.util.mobs.ItemChance;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Nullable;

public class EffSetEquipment extends Effect {

    static {
        Skript.registerEffect(EffSetEquipment.class,
                "set (0¦helmet|1¦chest[plate]|2¦leg[ging[s]]|3¦boots|4¦hand|5¦offhand) of %mvmob% to item %itemtype% with [a] drop chance of %float% [percent]",
                "set (0¦helmet|1¦chest[plate]|2¦leg[ging[s]]|3¦boots|4¦hand|5¦offhand) of %mvmob% to mvitem %string% with [a] drop chance of %float% [percent]");
    }

    Expression<MVMob> mob;

    Expression<ItemType> item;
    Expression<String> mvitem;
    Expression<Float> chance;

    EquipmentSlot slot;

    boolean usingCustom;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);

        ItemType type;
        String mvitem;
        float chance = this.chance.getSingle(event).floatValue();
        ItemChance itemChance;
        if(usingCustom) {
            mvitem = this.mvitem.getSingle(event);
            itemChance = new ItemChance(ItemManager.getItem(mvitem), chance);
        }
        else {
            type = this.item.getSingle(event);
            itemChance = new ItemChance(type.getMaterial(), chance);
        }

        if(slot.equals(EquipmentSlot.HEAD))
            m.withHelmet(itemChance);
        else if(slot.equals(EquipmentSlot.CHEST))
            m.withChestplate(itemChance);
        else if(slot.equals(EquipmentSlot.LEGS))
            m.withLeggings(itemChance);
        else if(slot.equals(EquipmentSlot.FEET))
            m.withBoots(itemChance);
        else if(slot.equals(EquipmentSlot.HAND))
            m.withMainHandItem(itemChance);
        else if(slot.equals(EquipmentSlot.OFF_HAND))
            m.withOffhandItem(itemChance);
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if(parseResult.mark == 0)
            slot = EquipmentSlot.HEAD;
        else if(parseResult.mark == 1)
            slot = EquipmentSlot.CHEST;
        else if(parseResult.mark == 2)
            slot = EquipmentSlot.LEGS;
        else if(parseResult.mark == 3)
            slot = EquipmentSlot.FEET;
        else if(parseResult.mark == 4)
            slot = EquipmentSlot.HAND;
        else if(parseResult.mark == 5)
            slot = EquipmentSlot.OFF_HAND;

        mob = (Expression<MVMob>) expressions[0];

        if(i == 0)
            item = (Expression<ItemType>) expressions[1];
        else if(i == 1) {
            mvitem = (Expression<String>) expressions[1];
            usingCustom = true;
        }

        chance = (Expression<Float>) expressions[2];
        return true;
    }
}
