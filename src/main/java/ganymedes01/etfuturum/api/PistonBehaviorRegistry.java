package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.Tags;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.ApiStatus;
import roadhog360.hogutils.api.hogtags.helpers.BlockTags;
import roadhog360.hogutils.api.utils.GenericUtils;

/// This class is deprecated and its functions are all now stubs that redirect to the new tag system.
@Deprecated
public class PistonBehaviorRegistry {
	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slick_blocks`, `etfuturum:piston_slime_blocks`, `etfuturum:piston_honey_blocks`
	/// @param action {@link PistonAction#NON_STICKY}, {@link PistonAction#BOUNCES_ENTITIES}, {@link PistonAction#PULLS_ENTITIES}
	/// will be converted to the above tags, respectively.
	@Deprecated
	public static void addPistonBehavior(Block block, PistonAction action) {
		addPistonBehavior(block, OreDictionary.WILDCARD_VALUE, action);
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slick_blocks`, `etfuturum:piston_slime_blocks`, `etfuturum:piston_honey_blocks`
	/// @param action {@link PistonAction#NON_STICKY}, {@link PistonAction#BOUNCES_ENTITIES}, {@link PistonAction#PULLS_ENTITIES}
	/// will be converted to the above tags, respectively.
	@Deprecated
	public static void addPistonBehavior(Block block, int meta, PistonAction action) {
		if (GenericUtils.isBlockMetaInBoundsIgnoreWildcard(meta)) {
			throw new IllegalArgumentException("Meta must be between "
					+ GenericUtils.getMinBlockMetadata() + " and " + GenericUtils.getMaxBlockMetadata() + " (inclusive)");
		}
		if (block != null && block != Blocks.air) {
			String tag = switch (action) {
				case NON_STICKY -> Tags.MOD_ID + ":piston_slick_blocks";
				case BOUNCES_ENTITIES -> Tags.MOD_ID + ":piston_slime_blocks";
				case PULLS_ENTITIES -> Tags.MOD_ID + ":piston_honey_blocks";
			};
			BlockTags.addTags(block, meta, tag);
		}
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slick_blocks`, `etfuturum:piston_slime_blocks`, `etfuturum:piston_honey_blocks`
	/// @param action `NON_STICKY`, `BOUNCES_ENTITIES`, `PULLS_ENTITIES` will be converted to the above tags, respectively.
	@Deprecated
	public static void addPistonBehavior(Block block, String action) {
		addPistonBehavior(block, OreDictionary.WILDCARD_VALUE, PistonAction.valueOf(action));
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slick_blocks`, `etfuturum:piston_slime_blocks`, `etfuturum:piston_honey_blocks`
	/// @param action `NON_STICKY`, `BOUNCES_ENTITIES`, `PULLS_ENTITIES` will be converted to the above tags, respectively.
	@Deprecated
	public static void addPistonBehavior(Block block, int meta, String action) {
		if (!action.equals("NON_STICKY") && !action.equals("BOUNCES_ENTITIES") && !action.equals("PULLS_ENTITIES")) {
			throw new IllegalArgumentException("Action must be NON_STICKY, BOUNCES_ENTITIES, or PULLS_ENTITIES");
		}
		addPistonBehavior(block, meta, PistonAction.valueOf(action));
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slick_blocks`, `etfuturum:piston_slime_blocks`, `etfuturum:piston_honey_blocks`
	///
	/// The above tags will be removed from any block passed into this function.
	@Deprecated
	public static void remove(Block block, int meta) {
		BlockTags.removeTags(block, meta, Tags.MOD_ID + ":piston_slick_blocks", Tags.MOD_ID + ":piston_honey_blocks", Tags.MOD_ID + ":piston_slime_blocks");
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slick_blocks`
	@Deprecated
	public static boolean isNonStickyBlock(Block block, int meta) {
		return BlockTags.hasTag(block, meta, Tags.MOD_ID + ":piston_slick_blocks");
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slime_blocks`, `etfuturum:piston_honey_blocks`
	@Deprecated
	public static boolean isStickyBlock(Block block, int meta) {
		if (BlockTags.hasTag(block, meta, Tags.MOD_ID + ":piston_honey_blocks")) return true;
		return BlockTags.hasTag(block, meta, Tags.MOD_ID + ":piston_slime_blocks");
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_honey_blocks`
	@Deprecated
	public static boolean pullsEntities(Block block, int meta) {
		return BlockTags.hasTag(block, meta, Tags.MOD_ID + ":piston_honey_blocks");
	}

	/// Deprecated; Use HogUtils tagging
	///
	/// Block Tags: `etfuturum:piston_slime_blocks`
	@Deprecated
	public static boolean bouncesEntities(Block block, int meta) {
		return BlockTags.hasTag(block, meta, Tags.MOD_ID + ":piston_slime_blocks");
	}

//		BlockTags.addTags(ModBlocks.SLIME.get(), Tags.MOD_ID + ":piston_slime_blocks");
//		BlockTags.addTags(ModBlocks.HONEY_BLOCK.get(), Tags.MOD_ID + ":piston_honey_blocks");
//		for (ModBlocks mb : ModBlocks.TERRACOTTA) {
//		BlockTags.addTags(mb.get(), Tags.MOD_ID + ":piston_slick_blocks");
//	}

	@Deprecated
	@ApiStatus.Internal
	public static void init() {
	}

	@Deprecated
	public enum PistonAction {
		NON_STICKY(),
		BOUNCES_ENTITIES(),
		PULLS_ENTITIES();
	}
}
