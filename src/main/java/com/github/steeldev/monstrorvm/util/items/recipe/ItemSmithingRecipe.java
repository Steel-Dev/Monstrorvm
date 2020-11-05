package com.github.steeldev.monstrorvm.util.items.recipe;

import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;

public class ItemSmithingRecipe extends ItemRecipe {
    public String smithingItemNeeded;
    public String smithingBaseMat;

    public ItemSmithingRecipe(String smithingItemNeeded,
                              String smithingBaseMat) {
        super(ItemRecipeType.SMITHING, 1);
        this.smithingItemNeeded = smithingItemNeeded;
        this.smithingBaseMat = smithingBaseMat;
    }
}
