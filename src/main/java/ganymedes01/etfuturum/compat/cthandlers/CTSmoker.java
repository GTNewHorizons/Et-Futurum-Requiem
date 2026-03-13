package ganymedes01.etfuturum.compat.cthandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.recipes.SmokerRecipes;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.mc1710.item.MCItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/**
 * Remember smoker recipes are output, THEN input, not input then output
 * mods.etfuturum.smoker.addRecipe(<minecraft:gold_block>, <minecraft:iron_block>, 4.0D);
 * mods.etfuturum.smoker.remove(<minecraft:gold_ore>, <minecraft:gold_ingot>);
 */

@ZenClass("mods.etfuturum.smoker")
public class CTSmoker {

    @ZenMethod
    public static void remove(IItemStack output, IIngredient input) {
        if (output == null) throw new IllegalArgumentException("output cannot be null");

        Map<ItemStack, ItemStack> furnaceSmeltingList = FurnaceRecipes.smelting()
            .getSmeltingList();
        ItemStackMap<ItemStack> smeltingList = new ItemStackMap<>();
        smeltingList.putAll(furnaceSmeltingList);
        smeltingList.putAll(SmokerRecipes.smelting().smeltingList);

        smeltingList.entrySet()
            .removeIf(stack -> SmokerRecipes.smelting().smeltingBlacklist.contains(stack.getKey()));

        List<ItemStack> toRemove = new ArrayList<>();
        List<ItemStack> toRemoveValues = new ArrayList<>();
        List<Float> toRemoveExperiences = new ArrayList<>();
        for (Map.Entry<ItemStack, ItemStack> entry : smeltingList.entrySet()) {
            if (output.matches(new MCItemStack(entry.getValue()))
                && (input == null || input.matches(new MCItemStack(entry.getKey())))) {
                toRemove.add(entry.getKey());
                toRemoveValues.add(entry.getValue());
                toRemoveExperiences.add(
                    SmokerRecipes.smelting()
                        .getSmeltingExperience(entry.getValue()));
            }
        }

        if (toRemove.isEmpty()) {
            MineTweakerAPI.logWarning("No smoker recipes for " + output);
        } else {
            MineTweakerAPI.apply(new RemoveAction(toRemove, toRemoveValues, toRemoveExperiences));
        }
    }

    @ZenMethod
    public static void remove(IItemStack output) {
        remove(output, null);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input) {
        addRecipe(output, input, 0);
    }

    @ZenMethod
    public static void addRecipe(IItemStack output, IIngredient input, double xp) {
        List<IItemStack> items = input.getItems();
        if (items == null) {
            MineTweakerAPI.logError("Cannot turn " + input + " into a smoker recipe");
        }

        ItemStack[] items2 = CompatCraftTweaker.getItemStacks(items);
        ItemStack output2 = CompatCraftTweaker.getItemStack(output);
        MineTweakerAPI.apply(new AddRecipeAction(input, items2, output2, xp));
    }

    // ######################
    // ### Action classes ###
    // ######################

    private static class RemoveAction implements IUndoableAction {

        private final List<ItemStack> items;
        private final List<ItemStack> values;
        private final List<Float> exps;

        public RemoveAction(List<ItemStack> items, List<ItemStack> values, List<Float> exps) {
            this.items = items;
            this.values = values;
            this.exps = exps;
        }

        @Override
        public void apply() {
            for (ItemStack item : items) {
                SmokerRecipes.smelting()
                    .removeRecipe(item);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (int i = 0; i < items.size(); i++) {
                SmokerRecipes.smelting()
                    .addRecipe(items.get(i), values.get(i), exps.get(i));
            }
        }

        @Override
        public String describe() {
            return "Removing " + items.size() + " smoker recipes";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + items.size() + " smoker recipes";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class AddRecipeAction implements IUndoableAction {

        private final IIngredient ingredient;
        private final ItemStack[] input;
        private final ItemStack output;
        private final double xp;

        public AddRecipeAction(IIngredient ingredient, ItemStack[] input, ItemStack output, double xp) {
            this.ingredient = ingredient;
            this.input = input;
            this.output = output;
            this.xp = xp;
        }

        @Override
        public void apply() {
            for (ItemStack inputStack : input) {
                SmokerRecipes.smelting()
                    .addRecipe(inputStack, output, (float) xp);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack inputStack : input) {
                SmokerRecipes.smelting()
                    .removeRecipe(inputStack);
            }
        }

        @Override
        public String describe() {
            return "Adding smoker recipe for " + ingredient;
        }

        @Override
        public String describeUndo() {
            return "Removing smoker recipe for " + ingredient;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
