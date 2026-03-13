package ganymedes01.etfuturum.recipes.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.commons.lang3.ArrayUtils;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.itemblocks.ItemBlockShulkerBox;
import ganymedes01.etfuturum.recipes.ModRecipes;

public class RecipeDyedShulkerBox extends ShapelessOreRecipe {

    public RecipeDyedShulkerBox(ItemStack result, Object[] recipe) {
        super(result, recipe);
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (int i = 0; i <= 16; i++) {
            ItemStack shulker = new ItemStack(ModBlocks.SHULKER_BOX.get());
            if (i > 0) {
                shulker.setTagCompound(new NBTTagCompound());
                shulker.getTagCompound()
                    .setByte("Color", (byte) i);
            }
            stacks.add(shulker);
        }
        getInput().set(0, stacks);
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean dye = false;
        boolean box = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);

            if (slot != null) {
                for (int oreID : OreDictionary.getOreIDs(slot)) {
                    if (OreDictionary.getOreName(oreID)
                        .startsWith("dye")) {
                        if (!dye) {
                            dye = true;
                        } else {
                            dye = false;
                            return false;
                        }
                        break;
                    }
                }
                if (slot.getItem() == Item.getItemFromBlock(ModBlocks.SHULKER_BOX.get())) {
                    if (!box) {
                        box = true;
                    } else if (box) {
                        box = false;
                        return false;
                    }
                }
            }
        }
        return box && dye;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        byte color = 0;
        ItemStack box = null;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack slot = inv.getStackInSlot(i);

            if (slot != null && slot.getItem() != null) {
                if (color == 0) {
                    for (int oreID : OreDictionary.getOreIDs(slot)) {
                        if (OreDictionary.getOreName(oreID)
                            .startsWith("dye")) {
                            color = (byte) ((~ArrayUtils.indexOf(ModRecipes.ore_dyes, OreDictionary.getOreName(oreID))
                                & 15) + 1);
                            break;
                        }
                    }
                }

                if (box == null && slot.getItem() instanceof ItemBlockShulkerBox) {
                    box = slot;
                }
            }

            if (box != null && color > 0) {
                break;
            }
        }

        if (box == null) return null;

        ItemStack newBox = box.copy();

        if (newBox != null) {
            if (!newBox.hasTagCompound()) {
                newBox.setTagCompound(new NBTTagCompound());
            }
            newBox.getTagCompound()
                .setByte("Color", color);
        }

        return newBox;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return super.getRecipeOutput();
    }

}
