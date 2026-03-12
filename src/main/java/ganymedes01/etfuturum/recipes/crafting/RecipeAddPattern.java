package ganymedes01.etfuturum.recipes.crafting;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.itemblocks.ItemBlockBanner;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.EnumColor;
import ganymedes01.etfuturum.tileentities.TileEntityBanner;
import ganymedes01.etfuturum.tileentities.TileEntityBanner.EnumBannerPattern;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeAddPattern implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting grid, World world) {
		boolean flag = false;

		for (int i = 0; i < grid.getSizeInventory(); i++) {
			ItemStack slot = grid.getStackInSlot(i);

			if (slot != null && slot.getItem() == ModBlocks.BANNER.getItem()) {
				if (flag)
					return false;
				if (TileEntityBanner.getPatterns(slot) >= 6)
					return false;
				flag = true;
			}
		}

		if (!flag)
			return false;
		return getPattern(grid) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting grid) {
		ItemStack banner = null;

		for (int i = 0; i < grid.getSizeInventory(); i++) {
			ItemStack slot = grid.getStackInSlot(i);

			if (slot != null && slot.getItem() == ModBlocks.BANNER.getItem()) {
				banner = slot.copy();
				banner.stackSize = 1;
				break;
			}
		}

		EnumBannerPattern pattern = getPattern(grid);
		if (pattern != null) {
			int dyeMeta = 15;
			ItemStack slot;
			for (int i = 0; i < grid.getSizeInventory(); i++) {
				slot = grid.getStackInSlot(i);
				if (slot != null && isDye(slot)) {
					dyeMeta = getDyeIndex(slot);
					break;
				}
			}

			NBTTagCompound nbt = ItemBlockBanner.getSubTag(banner, "BlockEntityTag", true);
			NBTTagList nbttaglist;

			if (nbt.hasKey("Patterns", 9))
				nbttaglist = nbt.getTagList("Patterns", 10);
			else {
				nbttaglist = new NBTTagList();
				nbt.setTag("Patterns", nbttaglist);
			}

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString("Pattern", pattern.getPatternID());
			nbttagcompound.setInteger("Color", dyeMeta);
			nbttaglist.appendTag(nbttagcompound);
		}

		return banner;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	private boolean isDye(ItemStack stack) {
		for (String ore : Utils.getOreNames(stack))
			for (EnumColor colour : EnumColor.VALUES)
				if (ore.equals(colour.getOreName()))
					return true;

		return false;
	}

	private int getDyeIndex(ItemStack stack) {
		for (String ore : Utils.getOreNames(stack))
			for (EnumColor colour : EnumColor.VALUES)
				if (ore.equals(colour.getOreName()))
					return colour.getDamage();

		return -1;
	}

	private EnumBannerPattern getPattern(InventoryCrafting grid) {
		for (EnumBannerPattern pattern : EnumBannerPattern.VALUES)
			if (pattern.hasValidCrafting()) {
				boolean flag = true;

				if (pattern.hasCraftingStack()) {
					boolean flag1 = false;
					boolean flag2 = false;

					for (int i = 0; i < grid.getSizeInventory() && flag; i++) {
						ItemStack slot = grid.getStackInSlot(i);
						if (slot != null && slot.getItem() != ModBlocks.BANNER.getItem())
							if (isDye(slot)) {
								if (flag2) {
									flag = false;
									break;
								}

								flag2 = true;
							} else {
								if (flag1 || !slot.isItemEqual(pattern.getCraftingStack())) {
									flag = false;
									break;
								}

								flag1 = true;
							}
					}

					if (!flag1)
						flag = false;
				} else if (grid.getSizeInventory() != pattern.getCraftingLayers().length * pattern.getCraftingLayers()[0].length())
					flag = false;
				else {
					int k = -1;

					for (int l = 0; l < grid.getSizeInventory() && flag; l++) {
						int i = l / 3;
						int j1 = l % 3;
						ItemStack slot = grid.getStackInSlot(l);
						if (slot != null && slot.getItem() != ModBlocks.BANNER.getItem()) {
							if (!isDye(slot)) {
								flag = false;
								break;
							}

							if (k != -1 && k != getDyeIndex(slot)) {
								flag = false;
								break;
							}

							if (pattern.getCraftingLayers()[i].charAt(j1) == ' ') {
								flag = false;
								break;
							}

							k = getDyeIndex(slot);
						} else if (pattern.getCraftingLayers()[i].charAt(j1) != ' ') {
							flag = false;
							break;
						}
					}
				}

				if (flag)
					return pattern;
			}

		return null;
	}
}