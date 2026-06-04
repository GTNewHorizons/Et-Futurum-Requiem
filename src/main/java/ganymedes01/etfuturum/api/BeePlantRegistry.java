package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.api.crops.IBeeGrowable;
import ganymedes01.etfuturum.core.utils.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;
import roadhog360.hogutils.api.hogtags.helpers.ItemTags;

/// This class is deprecated and its functions are all now stubs that redirect to the new tag system.
@Deprecated
public class BeePlantRegistry {
	/// Deprecated; Use HogUtils tagging
	///
	/// Tags: `minecraft:bee_food` (item) and `minecraft:bee_attractive` (block)
	@Deprecated
	public static void addFlower(Block block, int meta) {
		BlockTags.addTags(block, meta, "minecraft:bee_attractive");
		Item item = Item.getItemFromBlock(block);
		if(item != null) {
			ItemTags.addTags(item, meta, "minecraft:bee_food");
		}
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Tags: `minecraft:bee_growables` (block)
	@Deprecated
	public static void addCrop(Block block) {
		if (!(block instanceof IGrowable) && !(block instanceof IBeeGrowable)) {
			Logger.warn("Bee crops can only be instance of IGrowable or IBeeGrowable; this entry will do nothing!");
		}
		BlockTags.addTags(block, OreDictionary.WILDCARD_VALUE, "minecraft:bee_growables");
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Tags: `minecraft:bee_food` (item) and `minecraft:bee_attractive` (block)
	@Deprecated
	public static void removeFlower(Block block, int meta) {
		BlockTags.removeTags(block, meta, "minecraft:bee_attractive");
		Item item = Item.getItemFromBlock(block);
		if(item != null) {
			ItemTags.removeTags(item, meta, "minecraft:bee_food");
		}
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Tags: `minecraft:bee_growables` (block)
	@Deprecated
	public static void removeCrop(Block block) {
		BlockTags.removeTags(block, OreDictionary.WILDCARD_VALUE, "minecraft:bee_growables");
	}

	/// Deprecated; Use HogUtils tagging
	/// Returns `TRUE` if this is either a bee food OR bee flower.
	/// This is because the previous registry did not have separate lists for bee breedables, and pollinateable flowers.
	///
	/// Tags: `minecraft:bee_food` (item) and `minecraft:bee_attractive` (block)
	@Deprecated
	public static boolean isFlower(Block block, int meta) {
		Item item = Item.getItemFromBlock(block);
		if(item != null && ItemTags.hasTag(item, meta, "minecraft:bee_food")) {
			return true;
		}
		return BlockTags.hasTag(block, meta, "minecraft:bee_attractive");
	}

	/// Deprecated; Use HogUtils tagging
	@Deprecated
	public static boolean isCrop(Block block) {
		return BlockTags.hasTag(block, OreDictionary.WILDCARD_VALUE, "minecraft:bee_growables");
	}

	@Deprecated
	@ApiStatus.Internal
	public static void init() {
	}
}
