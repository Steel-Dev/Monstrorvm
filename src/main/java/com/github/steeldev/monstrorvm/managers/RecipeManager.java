package com.github.steeldev.monstrorvm.managers;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.items.recipe.types.CraftType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.SmeltType;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RecipeManager {

    final static Monstrorvm main = Monstrorvm.getInstance();

    public static void addSmithingRecipe(String key, ItemStack result, RecipeChoice baseMat, RecipeChoice itemNeeded) {
        NamespacedKey smithingRecKey = new NamespacedKey(main, key);
        SmithingRecipe smithingRec = new SmithingRecipe(smithingRecKey, result, baseMat, itemNeeded);
        addRecipe(smithingRec);
    }

    public static void addSmeltingRecipe(String key, SmeltType type, Material result, int resultAmount, Material smelted, int EXP, int time) {
        NamespacedKey smeltingRecKey = new NamespacedKey(main, key);
        int am = resultAmount;
        if (am > 64) am = 64;
        if (am < 1) am = 1;
        ItemStack resultItem = new ItemStack(result, am);

        Recipe recToAdd;

        switch (type) {
            case FURNACE:
                recToAdd = new FurnaceRecipe(smeltingRecKey, resultItem, smelted, EXP, time);
                break;
            case BLASTING:
                recToAdd = new BlastingRecipe(smeltingRecKey, resultItem, smelted, EXP, time);
                break;
            case SMOKER:
                recToAdd = new SmokingRecipe(smeltingRecKey, resultItem, smelted, EXP, time);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        addRecipe(recToAdd);
    }

    public static void addSmeltingRecipe(String key, SmeltType type, RecipeChoice result, int resultAmount, RecipeChoice smelted, int EXP, int time) {
        NamespacedKey smeltingRecKey = new NamespacedKey(main, key);
        int am = resultAmount;
        if (am > 64) am = 64;
        if (am < 1) am = 1;
        ItemStack resultItem = result.getItemStack();
        resultItem.setAmount(am);

        Recipe recToAdd;

        switch (type) {
            case FURNACE:
                recToAdd = new FurnaceRecipe(smeltingRecKey, resultItem, smelted, EXP, time);
                break;
            case BLASTING:
                recToAdd = new BlastingRecipe(smeltingRecKey, resultItem, smelted, EXP, time);
                break;
            case SMOKER:
                recToAdd = new SmokingRecipe(smeltingRecKey, resultItem, smelted, EXP, time);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        addRecipe(recToAdd);
    }

    public static void addCraftingRecipe(String key, CraftType type, Material result, int resultAmount, List<String> rows, Map<Character, Material> ingredients) {
        NamespacedKey craftingRecKey = new NamespacedKey(main, key);
        int am = resultAmount;
        if (am > 64) am = 64;
        if (am < 1) am = 1;
        ItemStack resultItem = new ItemStack(result, am);

        Recipe recToAdd;

        switch (type) {
            case SHAPED:
                recToAdd = new ShapedRecipe(craftingRecKey, resultItem);
                ((ShapedRecipe) recToAdd).shape(rows.get(0), rows.get(1), rows.get(2));
                for (Character ingKey : ingredients.keySet()) {
                    ((ShapedRecipe) recToAdd).setIngredient(ingKey, ingredients.get(ingKey));
                }
                break;
            case SHAPELESS:
                recToAdd = new ShapelessRecipe(craftingRecKey, resultItem);
                for (Character ingKey : ingredients.keySet()) {
                    ((ShapelessRecipe) recToAdd).addIngredient(ingredients.get(ingKey));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        addRecipe(recToAdd);
    }

    public static void addCraftingRecipe(String key, CraftType type, RecipeChoice result, int resultAmount, List<String> rows, Map<Character, RecipeChoice> ingredients) {
        NamespacedKey craftingRecKey = new NamespacedKey(main, key);
        int am = resultAmount;
        if (am > 64) am = 64;
        if (am < 1) am = 1;

        ItemStack resultItem = result.getItemStack();
        resultItem.setAmount(am);

        Recipe recToAdd;

        switch (type) {
            case SHAPED:
                recToAdd = new ShapedRecipe(craftingRecKey, resultItem);
                ((ShapedRecipe) recToAdd).shape(rows.get(0), rows.get(1), rows.get(2));
                for (Character ingKey : ingredients.keySet()) {
                    ((ShapedRecipe) recToAdd).setIngredient(ingKey, ingredients.get(ingKey));
                }
                break;
            case SHAPELESS:
                recToAdd = new ShapelessRecipe(craftingRecKey, resultItem);
                for (Character ingKey : ingredients.keySet()) {
                    ((ShapelessRecipe) recToAdd).addIngredient(ingredients.get(ingKey));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        addRecipe(recToAdd);
    }

    public static void addRecipe(Recipe recipe) {
        Bukkit.addRecipe(recipe);
        if (Config.DEBUG)
            main.getLogger().info(String.format("&aRecipe &e%s&a has been &2added.", ((Keyed) recipe).getKey()));
    }

    public static void removeRecipe(String key) {
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe rec = it.next();
            if (rec != null) {
                if (((Keyed) rec).getKey().toString().equals(key)) {
                    if (Config.DEBUG)
                        main.getLogger().info(String.format("&aRecipe &e%s&a has been &cremoved.", key));
                    it.remove();
                }
            }
        }
    }
}
