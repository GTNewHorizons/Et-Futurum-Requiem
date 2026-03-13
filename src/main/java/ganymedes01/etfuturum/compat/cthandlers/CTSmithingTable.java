package ganymedes01.etfuturum.compat.cthandlers;

import static ganymedes01.etfuturum.compat.CompatCraftTweaker.getInternal;

import java.util.ArrayList;
import java.util.List;

import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.recipes.SmithingTableRecipes;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.mc1710.item.MCItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Remember smithing table recipes are output, THEN input, not input then output
 * mods.etfuturum.smithingTable.addRecipe(<IC2:itemToolBronzeSword>, <minecraft:iron_sword>, <ore:ingotBronze>);
 * mods.etfuturum.smithingTable.addRecipeNoNBT(<minecraft:stone_sword>, <minecraft:wooden_sword>,
 * <minecraft:cobblestone>);
 * <p>
 * ALSO THIS CODE IS VERY SCUFFED I HAVE NO IDEA WHAT I AM DOING, CRAFTTWEAKER SUPPORT IS CONFUSING BUT NOBODY WOULD
 * STOP ASKING FOR IT
 * SO WARNING IF BAD CODE MAKES YOU CRINGE
 */

@ZenClass("mods.etfuturum.smithingTable")
public class CTSmithingTable {

    @ZenMethod
    public static void remove(IItemStack output) {
        if (output == null) throw new IllegalArgumentException("Removal output cannot be null");

        List<SmithingTableRecipes.SmithingTableRecipe> recipes = SmithingTableRecipes.getInstance()
            .getRecipes();
        List<SmithingTableRecipes.SmithingTableRecipe> toRemove = new ArrayList<>();

        for (SmithingTableRecipes.SmithingTableRecipe recipe : recipes) {
            if (output.matches(new MCItemStack(recipe.getRecipeOutput()))) {
                toRemove.add(recipe);
            }
        }

        if (toRemove.isEmpty()) {
            MineTweakerAPI.logWarning("No smithing table recipes for " + output);
        } else {
            MineTweakerAPI.apply(new RemoveAction(toRemove));
        }
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient toolSlot, IIngredient ingredient) {
        addRecipe(output, ingredient, toolSlot, true);
    }

    @ZenMethod
    public static void addRecipeNoNBT(IItemStack output, IIngredient toolSlot, IIngredient ingredient) {
        addRecipe(output, ingredient, toolSlot, false);
    }

    private static void addRecipe(IItemStack output, IIngredient ingredientSlot, IIngredient toolSlot,
        boolean copyNBT) {
        Object toolSlotItem = getInternal(toolSlot);
        Object ingredientSlotItem = getInternal(ingredientSlot);

        if (toolSlotItem == null || ingredientSlotItem == null || output == null) {
            MineTweakerAPI.logError("One or more smithing table ingredients were invalid or null");
        } else {
            MineTweakerAPI.apply(new AddRecipeAction(toolSlotItem, ingredientSlotItem, output, copyNBT));
        }
    }

    // ######################
    // ### Action classes ###
    // ######################

    private static class RemoveAction implements IUndoableAction {

        List<SmithingTableRecipes.SmithingTableRecipe> recipes;

        public RemoveAction(List<SmithingTableRecipes.SmithingTableRecipe> recipes) {
            this.recipes = recipes;
        }

        @Override
        public void apply() {
            SmithingTableRecipes.getInstance()
                .getRecipes()
                .removeAll(recipes);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            SmithingTableRecipes.getInstance()
                .getRecipes()
                .addAll(recipes);
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " smithing table recipes";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + recipes.size() + " smithing table recipes";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class AddRecipeAction implements IUndoableAction {

        private final SmithingTableRecipes.SmithingTableRecipe recipe;
        private final IItemStack output;

        public AddRecipeAction(Object toolSlot, Object ingredientSlot, IItemStack output, boolean copyNBT) {
            recipe = new SmithingTableRecipes.SmithingTableRecipe(
                toolSlot,
                ingredientSlot,
                CompatCraftTweaker.getItemStack(output),
                copyNBT);
            this.output = output;
        }

        @Override
        public void apply() {
            SmithingTableRecipes.getInstance()
                .addRecipe(recipe);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            SmithingTableRecipes.getInstance()
                .getRecipes()
                .remove(recipe);
        }

        @Override
        public String describe() {
            return "Adding smithing table recipe for " + output;
        }

        @Override
        public String describeUndo() {
            return "Removing smithing table recipe for " + output;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
