package com.github.steeldev.monstrorvm.skript.elements.effects.items;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemCraftingRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemSmeltingRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemSmithingRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.types.CraftType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.SmeltType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class EffAddRecipe extends Effect {

    static{
        //Skript.registerEffect(EffAddRecipe.class,"add [a] new (0¦shaped|1¦shapeless|2¦furnace|3¦smoker|4¦blasting|5¦smithing) recipe with key %string% to mvitem %mvitem%");
    }

    Expression<MVItem> item;
    Expression<String> key;

    RecipeType selectedRec;

    enum RecipeType{
        SHAPED_CRAFTING,
        SHAPELESS_CRAFTING,
        FURNACE_SMELTING,
        SMOKER_SMELTING,
        BLASTING_SMELTING,
        SMITHING
    }

    @Override
    protected void execute(Event event) {
        MVItem i = this.item.getSingle(event);
        String key = this.key.getSingle(event);
        if(selectedRec.equals(RecipeType.SHAPED_CRAFTING))
            i.withRecipe(new ItemCraftingRecipe(CraftType.SHAPED,null,null,0, key));
        else if(selectedRec.equals(RecipeType.SHAPELESS_CRAFTING))
            i.withRecipe(new ItemCraftingRecipe(CraftType.SHAPELESS,null,null,0, key));
        else if(selectedRec.equals(RecipeType.FURNACE_SMELTING))
            i.withRecipe(new ItemSmeltingRecipe(SmeltType.FURNACE,"",0,0, 0, key));
        else if(selectedRec.equals(RecipeType.SMOKER_SMELTING))
            i.withRecipe(new ItemSmeltingRecipe(SmeltType.SMOKER,"",0,0, 0, key));
        else if(selectedRec.equals(RecipeType.BLASTING_SMELTING))
            i.withRecipe(new ItemSmeltingRecipe(SmeltType.BLASTING,"",0,0, 0, key));
        else if(selectedRec.equals(RecipeType.SMITHING))
            i.withRecipe(new ItemSmithingRecipe("","", key));
    }

    @Override
    public String toString(@Nullable Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        key = (Expression<String>) expressions[0];
        item = (Expression<MVItem>) expressions[1];
        if(parseResult.mark == 0) selectedRec = RecipeType.SHAPED_CRAFTING;
        else if(parseResult.mark == 1) selectedRec = RecipeType.SHAPELESS_CRAFTING;
        else if(parseResult.mark == 2) selectedRec = RecipeType.FURNACE_SMELTING;
        else if(parseResult.mark == 3) selectedRec = RecipeType.SMOKER_SMELTING;
        else if(parseResult.mark == 4) selectedRec = RecipeType.BLASTING_SMELTING;
        else if(parseResult.mark == 5) selectedRec = RecipeType.SMITHING;
        return true;
    }
}
