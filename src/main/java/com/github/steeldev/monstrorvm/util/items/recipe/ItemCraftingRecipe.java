package com.github.steeldev.monstrorvm.util.items.recipe;

import com.github.steeldev.monstrorvm.util.items.recipe.types.CraftType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class ItemCraftingRecipe extends ItemRecipe {
    public CraftType craftType;
    public List<String> craftingPattern;
    public Map<Character, Material> craftingIngredients;

    public ItemCraftingRecipe(CraftType craftType,
                              List<String> craftingPattern,
                              Map<Character, Material> craftingIngredients,
                              int resultAmount) {
        super(ItemRecipeType.CRAFTING, resultAmount);
        this.craftType = craftType;
        this.craftingPattern = craftingPattern;
        this.craftingIngredients = craftingIngredients;
    }

    public ItemCraftingRecipe(CraftType craftType,
                              Map<Character, Material> craftingIngredients,
                              int resultAmount) {
        super(ItemRecipeType.CRAFTING, resultAmount);
        this.craftType = craftType;
        this.craftingIngredients = craftingIngredients;
    }
}
