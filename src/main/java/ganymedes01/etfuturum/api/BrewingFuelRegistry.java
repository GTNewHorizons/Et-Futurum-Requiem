package ganymedes01.etfuturum.api;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.recipes.ModRecipes;

public class BrewingFuelRegistry {

    private static final ItemStackMap<Integer> FUEL_REGISTRY = new ItemStackMap<>();

    static {
        FUEL_REGISTRY.put(new ItemStack(Items.blaze_powder), 30);
    }

    public static void registerFuel(Object itemObj, int count) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                "Tried to add a brewing fuel with " + count
                    + " cycles??? It must be able to at least brew 1 set of potions...");
        }

        if (ModRecipes.validateItems(itemObj)) {
            if (itemObj instanceof ItemStack) {
                FUEL_REGISTRY.put(((ItemStack) itemObj).copy(), count);
            } else if (itemObj instanceof String) {
                for (ItemStack oreStack : OreDictionary.getOres((String) itemObj)) {
                    FUEL_REGISTRY.put(oreStack.copy(), count);
                }
            } else if (itemObj instanceof Item) {
                FUEL_REGISTRY.put(new ItemStack((Item) itemObj, 1, OreDictionary.WILDCARD_VALUE), count);
            } else if (itemObj instanceof Block && Item.getItemFromBlock((Block) itemObj) != null) {
                FUEL_REGISTRY
                    .put(new ItemStack(Item.getItemFromBlock((Block) itemObj), 1, OreDictionary.WILDCARD_VALUE), count);
            } else {
                throw new IllegalArgumentException(
                    "Tried to add " + itemObj
                        + " as a brewing fuel, which is not an Itemstack, item, block or string.");
            }
        }
    }

    public static void registerFuel(List<Object> list, int count) {
        list.forEach(o -> registerFuel(o, count));
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
        return getBrewingAmount(fuel) > 0;
    }

    public static int getBrewingAmount(ItemStack fuel) {
        Integer time = FUEL_REGISTRY.get(fuel);
        if (time != null) {
            return time;
        }

        return 0;
    }

    public static ItemStackMap<Integer> getFuels() {
        return FUEL_REGISTRY;
    }

    /**
     * Print all entries in composting registry. Used for debugging purposes.
     */
    public void printFuels() {
        getFuels().forEach(
            (key, value) -> Logger.info(
                "Brewing fuel entry: " + Item.itemRegistry.getNameForObject(key.getItem())
                    + " Meta: "
                    + (key.getItemDamage() == OreDictionary.WILDCARD_VALUE ? "any" : key.getItemDamage())));
    }
}
