package ganymedes01.etfuturum.recipes.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessEtFuturumRecipe extends ShapelessOreRecipe {

    /**
     * Used to sort all Et Futurum recipes before the default ones to solve recipe conflicts
     */
    public ShapelessEtFuturumRecipe(ItemStack result, Object[] recipe) {
        super(result, recipe);
    }

}
