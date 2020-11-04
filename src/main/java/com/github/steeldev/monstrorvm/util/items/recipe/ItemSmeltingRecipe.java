package com.github.steeldev.monstrorvm.util.items.recipe;

import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.SmeltType;

public class ItemSmeltingRecipe extends ItemRecipe {
    public SmeltType smeltType;
    public String smeltingResult;
    public int smeltTime;
    public int smeltEXP;

    public ItemSmeltingRecipe(SmeltType smeltType,
                              String smeltingResult,
                              int smeltTime,
                              int smeltEXP,
                              int resultAmount) {
        super(ItemRecipeType.SMELTING, resultAmount);
        this.smeltType = smeltType;
        this.smeltingResult = smeltingResult;
        this.smeltTime = smeltTime;
        this.smeltEXP = smeltEXP;
    }
}
