package ganymedes01.etfuturum.compat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CompatVillageNames {

	private static final String[] DEPRECATED_BLOCKS = { "concrete", "concretePowder", "glazedTerracotta", "glazedTerracotta2",
			"glazedTerracotta3", "glazedTerracotta4" };
	private static final String DEPRECATION_TOOLTIP = "\u00a7cDeprecated: \u00a77will be removed in the next major update";

	public static final CompatVillageNames INSTANCE = new CompatVillageNames();

	private static List<Block> deprecatedBlocks;
	private static List<Item> deprecatedItems;

	public static boolean isActive() {
		return ModsList.VILLAGE_NAMES.isLoaded()
				&& ConfigModCompat.deprecateVillageNamesBlocks
				&& ModBlocks.CONCRETE.isEnabled()
				&& ModBlocks.CONCRETE_POWDER.isEnabled()
				&& ModBlocks.WHITE_GLAZED_TERRACOTTA.isEnabled();
	}

	public static List<Block> getDeprecatedBlocks() {
		if (deprecatedBlocks == null) {
			deprecatedBlocks = new ArrayList<>();
			deprecatedItems = new ArrayList<>();
			if (isActive()) {
				for (String name : DEPRECATED_BLOCKS) {
					Block block = GameRegistry.findBlock("VillageNames", name);
					if (block != null) {
						deprecatedBlocks.add(block);
						Item item = Item.getItemFromBlock(block);
						if (item != null) {
							deprecatedItems.add(item);
						}
					}
				}
			}
		}
		return deprecatedBlocks;
	}

	private static boolean isDeprecatedItem(Item item) {
		getDeprecatedBlocks();
		return item != null && deprecatedItems.contains(item);
	}

	public static void deprecate() {
		if (getDeprecatedBlocks().isEmpty()) {
			return;
		}

		Iterator<IRecipe> iterator = CraftingManager.getInstance().getRecipeList().iterator();
		while (iterator.hasNext()) {
			ItemStack output = iterator.next().getRecipeOutput();
			if (output != null && isDeprecatedItem(output.getItem())) {
				iterator.remove();
			}
		}

		Map<ItemStack, ItemStack> smelting = FurnaceRecipes.smelting().getSmeltingList();
		for (Map.Entry<ItemStack, ItemStack> entry : smelting.entrySet()) {
			ItemStack output = entry.getValue();
			if (output != null && isDeprecatedItem(output.getItem())) {
				entry.setValue(ModBlocks.TERRACOTTA[output.getItemDamage() & 15].newItemStack());
			}
		}
	}

	@SubscribeEvent
	public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
		if (event.block != null && getDeprecatedBlocks().contains(event.block)) {
			event.drops.clear();
			event.dropChance = 0;
		}
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event) {
		if (event.itemStack != null && isDeprecatedItem(event.itemStack.getItem())) {
			event.toolTip.add(DEPRECATION_TOOLTIP);
		}
	}
}
