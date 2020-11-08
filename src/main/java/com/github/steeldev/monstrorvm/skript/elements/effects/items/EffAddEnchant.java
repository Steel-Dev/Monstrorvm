package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.ItemEnchantInfo;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAddEnchant extends Effect {

    static {
        Skript.registerEffect(EffAddEnchant.class, "add enchant[ment] %enchantment% of level %integer% to [mvitem] %mvitem%");
    }

    Expression<Enchantment> enchant;
    Expression<Integer> value;
    Expression<MVItem> item;

    @Override
    protected void execute(Event event) {
        MVItem i = item.getSingle(event);
        Enchantment enchantment = this.enchant.getSingle(event);
        int value = this.value.getSingle(event).intValue();
        i.withEnchant(new ItemEnchantInfo(enchantment,value));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        enchant = (Expression<Enchantment>) expressions[0];
        value = (Expression<Integer>) expressions[1];
        item = (Expression<MVItem>) expressions[2];
        return true;
    }
}
