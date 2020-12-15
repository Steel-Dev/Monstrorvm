package com.github.steeldev.monstrorvm.util.items.recipe;

import com.github.steeldev.monstrorvm.util.items.recipe.types.CraftType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.Map;

public class ItemCraftingRecipe extends ItemRecipe {
    public CraftType craftType;
    public List<String> craftingPattern;
    public Map<Character, Material> craftingIngredients;
    public Map<Character, RecipeChoice> craftingIngredientsChoice;

    public ItemCraftingRecipe(CraftType craftType,
                              List<String> craftingPattern,
                              Map<Character, RecipeChoice> craftingIngredientsChoice,
                              int resultAmount,
                              String key) {
        super(ItemRecipeType.CRAFTING, resultAmount, key);
        this.craftType = craftType;
        this.craftingPattern = craftingPattern;
        this.craftingIngredientsChoice = craftingIngredientsChoice;
    }

    public ItemCraftingRecipe(CraftType craftType,
                              Map<Character, RecipeChoice> craftingIngredientsChoice,
                              int resultAmount,
                              String key) {
        super(ItemRecipeType.CRAFTING, resultAmount, key);
        this.craftType = craftType;
        this.craftingIngredientsChoice = craftingIngredientsChoice;
    }
}
