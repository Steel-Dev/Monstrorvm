package com.github.steeldev.monstrorvm.util.items.recipe;

import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;

public class ItemRecipe {
    public ItemRecipeType type;
    public int resultAmount;
    public String key;

    public ItemRecipe(ItemRecipeType type,
                      int resultAmount,
                      String key) {
        this.type = type;
        this.resultAmount = resultAmount;
        this.key = key;
    }
}
