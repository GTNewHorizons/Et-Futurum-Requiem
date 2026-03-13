package ganymedes01.etfuturum.compat.cthandlers;

import static ganymedes01.etfuturum.compat.CompatCraftTweaker.getInternal;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.api.CompostingRegistry;
import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.mc1710.item.MCItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.etfuturum.composting")
public class CTComposting {

    @ZenMethod
    public static void remove(IIngredient stack) {

        Object internal = getInternal(stack);

        if (((internal instanceof String && CompostingRegistry.isCompostable((String) internal))
            || (internal instanceof ItemStack && CompostingRegistry.isCompostable((ItemStack) internal)))) {
            final ItemStackMap<Integer> toRemove = new ItemStackMap<>();
            for (Map.Entry<ItemStack, Integer> compostEntry : CompostingRegistry.getComposts()
                .entrySet()) {
                if (stack.matches(new MCItemStack(compostEntry.getKey()))) {
                    toRemove.put(compostEntry.getKey(), compostEntry.getValue());
                }
            }
            MineTweakerAPI.apply(new RemoveAction(toRemove));
        } else {
            MineTweakerAPI.logWarning("No compostables for " + stack);
        }
    }

    @ZenMethod
    public static void addCompostable(IIngredient stack, int count) {
        List<IItemStack> items = stack.getItems();
        if (items == null) {
            MineTweakerAPI.logError("Cannot make " + stack + " compostable");
            return;
        }

        final ItemStack[] toAdd = CompatCraftTweaker.getItemStacks(items);
        MineTweakerAPI.apply(new AddAction(stack, toAdd, count));
    }

    @ZenMethod
    public static void removeAll() {
        MineTweakerAPI.apply(new RemoveAllAction());
    }

    // ######################
    // ### Action classes ###
    // ######################

    private static class RemoveAllAction implements IUndoableAction {

        private final ItemStackMap<Integer> items;

        public RemoveAllAction() {
            ItemStackMap<Integer> items = new ItemStackMap<>();
            items.putAll(CompostingRegistry.getComposts());
            this.items = items;
        }

        @Override
        public void apply() {
            for (ItemStack item : items.keySet()) {
                CompostingRegistry.remove(item);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
                CompostingRegistry.registerCompostable(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public String describe() {
            return "Removing " + items.size() + " compostables";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + items.size() + " compostables";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveAction implements IUndoableAction {

        private final ItemStackMap<Integer> items;

        public RemoveAction(ItemStackMap<Integer> items) {
            this.items = items;
        }

        @Override
        public void apply() {
            for (ItemStack item : items.keySet()) {
                CompostingRegistry.remove(item);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (Map.Entry<ItemStack, Integer> entry : items.entrySet()) {
                CompostingRegistry.registerCompostable(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public String describe() {
            return "Removing " + items.size() + " compostables";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + items.size() + " compostables";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class AddAction implements IUndoableAction {

        private final IIngredient ingredient;
        private final ItemStack[] stacks;
        private final int count;

        public AddAction(IIngredient ingredient, ItemStack[] stacks, int count) {
            this.ingredient = ingredient;
            this.stacks = stacks;
            this.count = count;
        }

        @Override
        public void apply() {
            for (ItemStack inputStack : stacks) {
                CompostingRegistry.registerCompostable(inputStack, count);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack inputStack : stacks) {
                CompostingRegistry.remove(inputStack);
            }
        }

        @Override
        public String describe() {
            return "Making " + ingredient + " compostable";
        }

        @Override
        public String describeUndo() {
            return "Making " + ingredient + " compostable";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
