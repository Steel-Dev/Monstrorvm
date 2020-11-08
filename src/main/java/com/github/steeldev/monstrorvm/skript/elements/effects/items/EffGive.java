package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffGive extends Effect {

    static{
        Skript.registerEffect(EffGive.class,"give %integer% of mvitem %string% to %player%");
    }

    Expression<Integer> amount;
    Expression<String> mvitem;
    Expression<Player> player;

    @Override
    protected void execute(Event event) {
        MVItem item = ItemManager.getItem(mvitem.getSingle(event));
        Player player = this.player.getSingle(event);

        int amount = this.amount.getSingle(event).intValue();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(item.getItem(false));
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        amount = (Expression<Integer>) expressions[0];
        mvitem = (Expression<String>) expressions[1];
        player = (Expression<Player>) expressions[2];
        return true;
    }
}
