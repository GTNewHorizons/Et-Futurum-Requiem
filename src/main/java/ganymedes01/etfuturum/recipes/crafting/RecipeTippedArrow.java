package ganymedes01.etfuturum.recipes.crafting;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.oredict.ShapedOreRecipe;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.items.ItemLingeringPotion;

public class RecipeTippedArrow extends ShapedOreRecipe {

    public RecipeTippedArrow(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid) {
        ItemStack potion = grid.getStackInRowAndColumn(1, 1);
        List<PotionEffect> effects = ((ItemLingeringPotion) ModItems.LINGERING_POTION.get()).getEffects(potion);

        if (potion.getItemDamage() == 0 && effects.isEmpty()) return null;

        ItemStack stack = ModItems.TIPPED_ARROW.newItemStack(8, potion.getItemDamage());
        if (!effects.isEmpty() && potion.hasTagCompound()
            && potion.getTagCompound()
                .hasKey("CustomPotionEffects", 9)) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag(
                "CustomPotionEffects",
                potion.getTagCompound()
                    .getTagList("CustomPotionEffects", 10)
                    .copy());
            stack.setTagCompound(tag);
        }

        return stack;
    }
}
