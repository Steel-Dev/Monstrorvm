package com.github.steeldev.monstrorvm.skript.elements.effects.mobs;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.entity.EntityType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.github.steeldev.monstrorvm.util.mobs.ItemChance;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffAddToDrops extends Effect {

    static {
        Skript.registerEffect(EffAddToDrops.class,
                "add (0¦undamaged|1¦damaged) item %itemtype% with %integer% as max [amount] with [a] chance of %integer% [percent] to drops of %mvmob%",
                "add (0¦undamaged|1¦damaged) mvitem %string% with %integer% as max [amount] with [a] chance of %integer% [percent] to drops of %mvmob%");
    }

    Expression<MVMob> mob;

    Expression<ItemType> item;
    Expression<String> mvitem;
    Expression<Integer> maxAmount;
    Expression<Integer> chance;

    boolean usingCustom;
    boolean damaged;

    @Override
    protected void execute(Event event) {
        MVMob m = mob.getSingle(event);
        int chance = this.chance.getSingle(event).intValue();
        int maxAmount = this.maxAmount.getSingle(event).intValue();
        if(usingCustom) {
            MVItem item = ItemManager.getItem(mvitem.getSingle(event));
            m.withDrop(new ItemChance(item,maxAmount,chance,damaged));
        }
        else{
            Material itemType = this.item.getSingle(event).getMaterial();
            m.withDrop(new ItemChance(itemType,maxAmount,chance,damaged));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if(i == 0)
            item = (Expression<ItemType>) expressions[0];
        else if(i == 1) {
            mvitem = (Expression<String>) expressions[0];
            usingCustom = true;
        }
        maxAmount = (Expression<Integer>) expressions[1];
        chance = (Expression<Integer>) expressions[2];
        mob = (Expression<MVMob>) expressions[3];
        if(parseResult.mark == 1)
            damaged = true;
        return true;
    }
}
