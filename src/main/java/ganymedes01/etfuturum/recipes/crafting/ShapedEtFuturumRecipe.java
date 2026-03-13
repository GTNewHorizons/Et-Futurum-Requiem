package ganymedes01.etfuturum.recipes.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedEtFuturumRecipe extends ShapedOreRecipe {

    /**
     * Used to sort all Et Futurum recipes before the default ones to solve recipe conflicts
     */
    public ShapedEtFuturumRecipe(ItemStack result, Object[] recipe) {
        super(result, recipe);
    }

}
