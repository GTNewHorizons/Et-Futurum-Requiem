package ganymedes01.etfuturum.compat;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.compat.nei.BannerPatternHandler;
import ganymedes01.etfuturum.compat.nei.BlastFurnaceRecipeHandler;
import ganymedes01.etfuturum.compat.nei.ComposterHandler;
import ganymedes01.etfuturum.compat.nei.SmokerRecipeHandler;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.BlockSlab;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class NEIEtFuturumConfig implements IConfigureNEI {

	private static final BlastFurnaceRecipeHandler BLAST_FURNACE_HANDLER = new BlastFurnaceRecipeHandler();
	private static final SmokerRecipeHandler SMOKER_HANDLER = new SmokerRecipeHandler();

	@Override
	public void loadConfig() {
		if (ConfigWorld.tileReplacementMode != -1) {
			if (ModBlocks.BREWING_STAND.isEnabled()) {
				API.hideItem(ModBlocks.BREWING_STAND.newItemStack());
			}

			if (ModBlocks.BEACON.isEnabled()) {
				API.hideItem(ModBlocks.BEACON.newItemStack());
			}

			if (ModBlocks.ANVIL.isEnabled()) {
				API.hideItem(ModBlocks.ANVIL.newItemStack());
				API.hideItem(ModBlocks.ANVIL.newItemStack(1, 1));
				API.hideItem(ModBlocks.ANVIL.newItemStack(1, 2));
			}

			if (ModBlocks.ENCHANTMENT_TABLE.isEnabled()) {
				API.hideItem(ModBlocks.ENCHANTMENT_TABLE.newItemStack());
			}
		}

		API.hideItem(new ItemStack(Items.fireworks));
		for (byte i = 1; i <= 3; i++) {
			ItemStack firework = new ItemStack(Items.fireworks);
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound nbt2 = new NBTTagCompound();
			nbt2.setByte("Flight", i);
			nbt.setTag("Fireworks", nbt2);
			firework.setTagCompound(nbt);
			API.addItemListEntry(firework);
		}

		for (ModBlocks mb : ModBlocks.VALUES) {
			if (mb.isEnabled() && mb.get() instanceof BlockSlab && mb.name().toLowerCase().contains("double")) {
				API.hideItem(mb.newItemStack(1, OreDictionary.WILDCARD_VALUE));
			}
		}

		if (ModBlocks.BANNER.isEnabled()) {
			API.registerRecipeHandler(new BannerPatternHandler());
			API.registerUsageHandler(new BannerPatternHandler());
		}

		if (ModBlocks.SMOKER.isEnabled()) {
			// Smoker
			FurnaceRecipeHandler handler = SMOKER_HANDLER;
			// make it possible to use the "R" Key
			GuiCraftingRecipe.craftinghandlers.add(handler);
			// make it possible to use the "U" Key
			GuiUsageRecipe.usagehandlers.add(handler);
		}

		if (ModBlocks.BLAST_FURNACE.isEnabled()) {
			// Blast Furnace
			FurnaceRecipeHandler handler = BLAST_FURNACE_HANDLER;
			// make it possible to use the "R" Key
			GuiCraftingRecipe.craftinghandlers.add(handler);
			// make it possible to use the "U" Key
			GuiUsageRecipe.usagehandlers.add(handler);
		}

		if (ModBlocks.COMPOSTER.isEnabled()) {
			ComposterHandler handler = new ComposterHandler();
			GuiCraftingRecipe.craftinghandlers.add(handler);
			GuiUsageRecipe.usagehandlers.add(handler);
		}
	}

	public static void clearCaches() {
		BLAST_FURNACE_HANDLER.clearCache();
		SMOKER_HANDLER.clearCache();
	}

	@Override
	public String getName() {
		return Tags.MOD_NAME;
	}

	@Override
	public String getVersion() {
		return Tags.VERSION;
	}
}