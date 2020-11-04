package com.github.steeldev.monstrorvm.util.items.recipe;

import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;

public class ItemRecipe {
    public ItemRecipeType type;
    public int resultAmount;

    public ItemRecipe(ItemRecipeType type,
                      int resultAmount) {
        this.type = type;
        this.resultAmount = resultAmount;
    }
}
