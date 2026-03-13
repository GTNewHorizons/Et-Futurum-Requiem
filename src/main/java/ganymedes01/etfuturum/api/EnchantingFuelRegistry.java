package ganymedes01.etfuturum.api;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ganymedes01.etfuturum.core.utils.ItemStackSet;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.recipes.ModRecipes;

public class EnchantingFuelRegistry {

    private static final ItemStackSet FUEL_REGISTRY = new ItemStackSet();

    static {
        FUEL_REGISTRY.add(new ItemStack(Items.dye, 1, 4));
    }

    public static void registerFuel(Object itemObj) {
        if (ModRecipes.validateItems(itemObj)) {
            if (itemObj instanceof ItemStack) {
                FUEL_REGISTRY.add(((ItemStack) itemObj).copy());
            } else if (itemObj instanceof String) {
                for (ItemStack oreStack : OreDictionary.getOres((String) itemObj)) {
                    FUEL_REGISTRY.add(oreStack.copy());
                }
            } else if (itemObj instanceof Item) {
                FUEL_REGISTRY.add(new ItemStack((Item) itemObj, 1, OreDictionary.WILDCARD_VALUE));
            } else if (itemObj instanceof Block && Item.getItemFromBlock((Block) itemObj) != null) {
                FUEL_REGISTRY
                    .add(new ItemStack(Item.getItemFromBlock((Block) itemObj), 1, OreDictionary.WILDCARD_VALUE));
            } else {
                throw new IllegalArgumentException(
                    "Tried to add " + itemObj
                        + " as an enchanting fuel, which is not an Itemstack, item, block or string.");
            }
        }
    }

    public static void registerFuel(List<Object> list, int count) {
        list.forEach(EnchantingFuelRegistry::registerFuel);
    }

    public static void remove(String fuelOreDict) {
        for (ItemStack stack : OreDictionary.getOres(fuelOreDict)) {
            remove(stack);
        }
    }

    public static void remove(ItemStack fuel) {
        FUEL_REGISTRY.remove(fuel);
    }

    public static boolean isFuel(String fuelOreDict) {
        for (ItemStack stack : OreDictionary.getOres(fuelOreDict)) {
            if (isFuel(stack)) return true;
        }
        return false;
    }

    public static boolean isFuel(ItemStack fuel) {
        return FUEL_REGISTRY.isEmpty() || FUEL_REGISTRY.contains(fuel);
    }

    public static ItemStackSet getFuels() {
        return FUEL_REGISTRY;
    }

    /**
     * Print all entries in composting registry. Used for debugging purposes.
     */
    public void printFuels() {
        getFuels().forEach(
            (key, value) -> Logger.info(
                "Enchanting fuel entry: " + Item.itemRegistry.getNameForObject(key.getItem())
                    + " Meta: "
                    + (key.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "any" : key.getItemDamage())));
    }
}
