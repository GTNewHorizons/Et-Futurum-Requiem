package ganymedes01.etfuturum.recipes;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.core.utils.ItemStackSet;

public class SmokerRecipes {

    private static final SmokerRecipes smeltingBase = new SmokerRecipes();
    private boolean reloadingCT;

    /**
     * The list of smelting results.
     */
    public final ItemStackMap<ItemStack> smeltingList = new ItemStackMap<ItemStack>();
    public final ItemStackMap<Float> experienceList = new ItemStackMap<Float>();
    public final ItemStackSet smeltingBlacklist = new ItemStackSet();

    public final ItemStackMap<ItemStack> smeltingListCache = new ItemStackMap<ItemStack>();
    public final ItemStackMap<Float> experienceListCache = new ItemStackMap<Float>();

    /**
     * Used to call methods addSmelting and getSmeltingResult.
     */
    public static SmokerRecipes smelting() {
        return smeltingBase;
    }

    public void clearLists() {
        smeltingListCache.clear();
        experienceListCache.clear();
    }

    public void setReloadingCT(boolean val) {
        reloadingCT = val;
    }

    /**
     * Returns the smelting result of an item.
     */
    public ItemStack getSmeltingResult(ItemStack input) {
        if (smeltingBlacklist.contains(input)) return null;

        if (!smeltingListCache.containsKey(input)) {
            if (!smeltingList.containsKey(input)) {
                ItemStack result = FurnaceRecipes.smelting()
                    .getSmeltingResult(input);
                if (canAdd(input, result)) {
                    if (!reloadingCT) smeltingListCache.put(input, result);
                    return result;
                }
            }
            ItemStack CTResult = smeltingList.get(input);
            if (!reloadingCT) smeltingListCache.put(input, CTResult);
            return CTResult;
        }
        return smeltingListCache.get(input);
    }

    public float getSmeltingExperience(ItemStack result) {
        float ret = result.getItem()
            .getSmeltingExperience(result);
        if (ret != -1) return ret;

        if (!experienceListCache.containsKey(result)) {
            if (!experienceList.containsKey(result)) {
                float exp = FurnaceRecipes.smelting()
                    .func_151398_b(result); // getSmeltingExperience
                if (!reloadingCT) experienceListCache.put(result, exp);
                return exp;
            }
            float expCT = experienceList.get(result);
            if (!reloadingCT) experienceListCache.put(result, expCT);
            return expCT;
        }
        return experienceListCache.get(result);
    }

    public void addRecipe(ItemStack input, ItemStack output, float exp) {
        smeltingList.put(input, output);
        experienceList.put(output, exp);
        smeltingBlacklist.remove(input);
    }

    public void removeRecipe(ItemStack input) {
        experienceList.remove(smeltingList.get(input));
        smeltingList.remove(input);
        smeltingBlacklist.add(input);
    }

    public boolean canAdd(ItemStack input, ItemStack result) {
        if (ConfigFunctions.enableAutoAddSmoker) {
            // Make sure there is no Nullpointers in there, yes there can be invalid Recipes in the Furnace List.
            // That was why DragonAPI somehow fixed a Bug in here, because Reika removes nulls from the List!
            if (input != null && result != null) {
                // If the result is a food, allow smelting.
                return result.getItem() instanceof ItemFood
                    && ((ItemFood) result.getItem()).func_150905_g/* getHealAmount */(result) > 0;
            }
        }
        return false;
    }
}
