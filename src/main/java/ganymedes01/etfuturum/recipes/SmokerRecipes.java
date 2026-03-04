package ganymedes01.etfuturum.recipes;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.core.utils.ItemStackMap;
import ganymedes01.etfuturum.core.utils.ItemStackSet;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import static gregtech.api.enums.Materials.MeatRaw;
import static gregtech.common.items.IDMetaItem02.Food_Raw_PotatoChips;
import static gregtech.common.items.IDMetaItem02.Food_Potato_On_Stick;
import static gregtech.common.items.IDMetaItem02.Food_Raw_Pizza_Veggie;
import static gregtech.common.items.IDMetaItem02.Food_Raw_Pizza_Cheese;
import static gregtech.common.items.IDMetaItem02.Food_Raw_Pizza_Meat;
import static gregtech.common.items.IDMetaItem02.Food_Raw_Bun;
import static gregtech.common.items.IDMetaItem02.Food_Raw_Baguette;
import static gregtech.common.items.IDMetaItem02.Food_Raw_Cake;
import gregtech.api.enums.ItemList;
import gregtech.api.util.GTUtility;

public class SmokerRecipes {
	private static final SmokerRecipes smeltingBase = new SmokerRecipes();
	private boolean reloadingCT;

	/**
	 * The list of smelting results.
	 */
	public final ItemStackMap<ItemStack> smeltingList = new ItemStackMap<ItemStack>();
	public final ItemStackMap<Float> experienceList = new ItemStackMap<Float>();
	public final ItemStackSet smeltingBlacklist = new ItemStackSet();
    // set of inputs that for one reason or another don't have ItemFood output but still fit in the smoker
	public final ItemStackSet smokerExtraRecipes = new ItemStackSet();

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
				ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(input);
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
		float ret = result.getItem().getSmeltingExperience(result);
		if (ret != -1) return ret;

		if (!experienceListCache.containsKey(result)) {
			if (!experienceList.containsKey(result)) {
				float exp = FurnaceRecipes.smelting().func_151398_b(result); // getSmeltingExperience
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
    
    private void populateExtraRecipes(){
        smokerExtraRecipes.add(MeatRaw.getDust(1));
        smokerExtraRecipes.add(ItemList.Food_Potato_On_Stick.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_PotatoChips.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_Pizza_Veggie.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_Pizza_Cheese.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_Pizza_Meat.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_Bun.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_Baguette.get(1L));
        smokerExtraRecipes.add(ItemList.Food_Raw_Cake.get(1L));
        // sesame seeds?
        // vanilla bean?
        
    }

	public boolean canAdd(ItemStack input, ItemStack result) {
		if (!ConfigFunctions.enableAutoAddSmoker) return false;
		// Make sure there is no Nullpointers in there, yes there can be invalid Recipes in the Furnace List.
		// That was why DragonAPI somehow fixed a Bug in here, because Reika removes nulls from the List!
        if (input == null || result == null) return false;
        
        //If the result is a food, allow smelting.
        if (result.getItem() instanceof ItemFood &&
            ((ItemFood) result.getItem()).func_150905_g/*getHealAmount*/(result) > 0) return true;
        
        // set up extra recipes on first pass
        if (smokerExtraRecipes.isEmpty()) populateExtraRecipes();
        if (smokerExtraRecipes.contains(input)) return true;
        
		return false;
	}
}
