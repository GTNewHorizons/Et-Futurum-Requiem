package ganymedes01.etfuturum.compat.cthandlers;

import static ganymedes01.etfuturum.compat.CompatCraftTweaker.getInternal;

import java.util.List;

import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.api.EnchantingFuelRegistry;
import ganymedes01.etfuturum.compat.CompatCraftTweaker;
import ganymedes01.etfuturum.core.utils.ItemStackSet;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.mc1710.item.MCItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.etfuturum.enchantingFuel")
public class CTEnchantingFuels {

    @ZenMethod
    public static void remove(IIngredient fuel) {

        Object internal = getInternal(fuel);

        if (((internal instanceof String && EnchantingFuelRegistry.isFuel((String) internal))
            || (internal instanceof ItemStack && EnchantingFuelRegistry.isFuel((ItemStack) internal)))) {
            final ItemStackSet toRemove = new ItemStackSet();
            for (ItemStack stack : EnchantingFuelRegistry.getFuels()
                .keySet()) {
                if (fuel.matches(new MCItemStack(stack))) {
                    toRemove.add(stack);
                }
            }
            MineTweakerAPI.apply(new RemoveAction(toRemove));
        } else {
            MineTweakerAPI.logWarning("No enchanting fuels for " + fuel);
        }
    }

    @ZenMethod
    public static void addFuel(IIngredient fuel) {
        List<IItemStack> items = fuel.getItems();
        if (items == null) {
            MineTweakerAPI.logError("Cannot turn " + fuel + " into a enchanting fuel");
            return;
        }

        final ItemStack[] toAdd = CompatCraftTweaker.getItemStacks(items);
        MineTweakerAPI.apply(new AddAction(fuel, toAdd));
    }

    // ######################
    // ### Action classes ###
    // ######################

    private static class RemoveAction implements IUndoableAction {

        private final ItemStackSet items;

        public RemoveAction(ItemStackSet items) {
            this.items = items;
        }

        @Override
        public void apply() {
            for (ItemStack item : items.keySet()) {
                EnchantingFuelRegistry.remove(item);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack stack : items.keySet()) {
                EnchantingFuelRegistry.registerFuel(stack);
            }
        }

        @Override
        public String describe() {
            return "Removing " + items.size() + " enchanting fuels";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + items.size() + " enchanting fuels";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class AddAction implements IUndoableAction {

        private final IIngredient ingredient;
        private final ItemStack[] fuels;

        public AddAction(IIngredient ingredient, ItemStack[] fuels) {
            this.ingredient = ingredient;
            this.fuels = fuels;
        }

        @Override
        public void apply() {
            for (ItemStack inputStack : fuels) {
                EnchantingFuelRegistry.registerFuel(inputStack);
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack inputStack : fuels) {
                EnchantingFuelRegistry.remove(inputStack);
            }
        }

        @Override
        public String describe() {
            return "Adding enchanting fuel for " + ingredient;
        }

        @Override
        public String describeUndo() {
            return "Removing enchanting fuel for " + ingredient;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
