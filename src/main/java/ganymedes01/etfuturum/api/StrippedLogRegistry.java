package ganymedes01.etfuturum.api;

import cpw.mods.fml.common.registry.GameRegistry;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.HashMap;
import java.util.Map;

public class StrippedLogRegistry {

	private static final Map<RegistryMapping<Block>, RegistryMapping<Block>> strippedLogs = new HashMap<>();

	/**
	 * Adds a specified log and its metadata to be converted to another specified log and its metadata.
	 * This is wrapped by 4, so you don't have to add each log multiple times for every different rotation.
	 * For example, log:0 is mapped to convert to stripped_log:0, but the other rotations, 4 and 8 also work.
	 *
	 * @param from
	 * @param fromMeta Wrapped by 4
	 * @param to
	 * @param toMeta   Wrapped by 4
	 */
	public static void addLog(Block from, int fromMeta, Block to, int toMeta) {
		addLog(new RegistryMapping<>(from, fromMeta & 3), new RegistryMapping<>(to, toMeta & 3));
	}

	private static void addLog(RegistryMapping<Block> from, RegistryMapping<Block> to) {
		if (!Utils.isMetaInBlockBoundsIgnoreWildcard(from.getMeta()) || !Utils.isMetaInBlockBoundsIgnoreWildcard(to.getMeta())) {
			throw new IllegalArgumentException("Meta must be between " + Utils.getMinMetadata() + " and " + Utils.getMaxMetadata() + " (inclusive).");
		}
		strippedLogs.put(from, to);
	}

	/**
	 * @param block
	 * @param meta  Wrapped by 4
	 * @return True if this log and its metadata has a stripped variant.
	 */
	public static boolean hasLog(Block block, int meta) {
		return hasLog(RegistryMapping.getKeyFor(block, meta & 3));
	}

	private static boolean hasLog(RegistryMapping<Block> map) {
		return strippedLogs.containsKey(map);
	}

	/**
	 * @param block
	 * @param meta  Wrapped by 4
	 * @return A mapping containing the stripped alternative of the input block. This is
	 * an instance of the BlockAndMetadataMapping class, containing a variable with
	 * the block instance and the meta data it should be replaced with.
	 */
	public static RegistryMapping<Block> getLog(Block block, int meta) {
		return getLog(RegistryMapping.getKeyFor(block, meta & 3));
	}

	private static RegistryMapping<Block> getLog(RegistryMapping<Block> map) {
		return strippedLogs.get(map);
	}

	/**
	 * @return The entire stripped log mapping, where a metadata/block pair is the key.
	 * The key's return value is of the class BlockAndMetadataMapping, which just store
	 * a Block instance, and a metadata value.
	 * <p>
	 * Do not use this to add or get items from the map this way,
	 * in case the key changes.
	 */
	public static Map<RegistryMapping<Block>, RegistryMapping<Block>> getLogMap() {
		return strippedLogs;
	}

	public static void init() {
		if (ModBlocks.LOG_STRIPPED.isEnabled()) {
			addLog(Blocks.log, 0, ModBlocks.LOG_STRIPPED.get(), 0);
			addLog(Blocks.log, 1, ModBlocks.LOG_STRIPPED.get(), 1);
			addLog(Blocks.log, 2, ModBlocks.LOG_STRIPPED.get(), 2);
			addLog(Blocks.log, 3, ModBlocks.LOG_STRIPPED.get(), 3);
		}
		if (ModBlocks.LOG2_STRIPPED.isEnabled()) {
			addLog(Blocks.log2, 0, ModBlocks.LOG2_STRIPPED.get(), 0);
			addLog(Blocks.log2, 1, ModBlocks.LOG2_STRIPPED.get(), 1);
		}

		if (ModBlocks.BARK.isEnabled() && ModBlocks.WOOD_STRIPPED.isEnabled()) {
			addLog(ModBlocks.BARK.get(), 0, ModBlocks.WOOD_STRIPPED.get(), 0);
			addLog(ModBlocks.BARK.get(), 1, ModBlocks.WOOD_STRIPPED.get(), 1);
			addLog(ModBlocks.BARK.get(), 2, ModBlocks.WOOD_STRIPPED.get(), 2);
			addLog(ModBlocks.BARK.get(), 3, ModBlocks.WOOD_STRIPPED.get(), 3);
		}

		if (ModBlocks.BARK2.isEnabled() && ModBlocks.WOOD2_STRIPPED.isEnabled()) {
			addLog(ModBlocks.BARK2.get(), 0, ModBlocks.WOOD2_STRIPPED.get(), 0);
			addLog(ModBlocks.BARK2.get(), 1, ModBlocks.WOOD2_STRIPPED.get(), 1);
		}

		if (ModBlocks.CRIMSON_STEM.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.CRIMSON_STEM.get(), 0, ModBlocks.CRIMSON_STEM.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.CRIMSON_STEM.get(), 1, ModBlocks.CRIMSON_STEM.get(), 3);
				}
			}
		}

		if (ModBlocks.WARPED_STEM.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.WARPED_STEM.get(), 0, ModBlocks.WARPED_STEM.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.WARPED_STEM.get(), 1, ModBlocks.WARPED_STEM.get(), 3);
				}
			}
		}

		if (ModBlocks.MANGROVE_LOG.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.MANGROVE_LOG.get(), 0, ModBlocks.MANGROVE_LOG.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.MANGROVE_LOG.get(), 1, ModBlocks.MANGROVE_LOG.get(), 3);
				}
			}
		}

		if (ModBlocks.CHERRY_LOG.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.CHERRY_LOG.get(), 0, ModBlocks.CHERRY_LOG.get(), 2);
				if (ConfigBlocksItems.enableBarkLogs) {
					addLog(ModBlocks.CHERRY_LOG.get(), 1, ModBlocks.CHERRY_LOG.get(), 3);
				}
			}
		}

		if (ModBlocks.BAMBOO_BLOCK.isEnabled()) {
			if (ConfigBlocksItems.enableStrippedLogs) {
				addLog(ModBlocks.BAMBOO_BLOCK.get(), 0, ModBlocks.BAMBOO_BLOCK.get(), 1);
			}
		}
		if(ModBlocks.BOP_LOG_STRIPPED.isEnabled()) {
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs1"), 0, ModBlocks.BOP_LOG_STRIPPED.get(), 0);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs1"), 1, ModBlocks.BOP_LOG_STRIPPED.get(), 1);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs1"), 2, ModBlocks.BOP_LOG_STRIPPED.get(), 2);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs1"), 3, ModBlocks.BOP_LOG_STRIPPED.get(), 3);
		}

		if(ModBlocks.BOP_LOG_STRIPPED2.isEnabled()) {
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs2"), 0, ModBlocks.BOP_LOG_STRIPPED2.get(), 0);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs2"), 1, ModBlocks.BOP_LOG_STRIPPED2.get(), 1);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs2"), 2, ModBlocks.BOP_LOG_STRIPPED2.get(), 2);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs2"), 3, ModBlocks.BOP_LOG_STRIPPED2.get(), 3);
		}

		if(ModBlocks.BOP_LOG_STRIPPED3.isEnabled()) {
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs3"), 0, ModBlocks.BOP_LOG_STRIPPED3.get(), 0);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs3"), 1, ModBlocks.BOP_LOG_STRIPPED3.get(), 1);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs3"), 2, ModBlocks.BOP_LOG_STRIPPED3.get(), 2);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs3"), 3, ModBlocks.BOP_LOG_STRIPPED3.get(), 3);
		}

		if(ModBlocks.BOP_LOG_STRIPPED4.isEnabled()) {
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs4"), 0, ModBlocks.BOP_LOG_STRIPPED4.get(), 0);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs4"), 1, ModBlocks.BOP_LOG_STRIPPED4.get(), 1);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs4"), 2, ModBlocks.BOP_LOG_STRIPPED4.get(), 2);
			addLog(GameRegistry.findBlock("BiomesOPlenty", "logs4"), 3, ModBlocks.BOP_LOG_STRIPPED4.get(), 3);
		}

		if(ModBlocks.THAUMCRAFT_LOG_STRIPPED.isEnabled()) {
			addLog(GameRegistry.findBlock("Thaumcraft", "blockMagicalLog"), 0, ModBlocks.THAUMCRAFT_LOG_STRIPPED.get(), 0);
			addLog(GameRegistry.findBlock("Thaumcraft", "blockMagicalLog"), 1, ModBlocks.THAUMCRAFT_LOG_STRIPPED.get(), 1);
		}

		if(ModBlocks.WITCHERY_LOG_STRIPPED.isEnabled()) {
			addLog(GameRegistry.findBlock("witchery", "witchlog"), 0, ModBlocks.WITCHERY_LOG_STRIPPED.get(), 0);
			addLog(GameRegistry.findBlock("witchery", "witchlog"), 1, ModBlocks.WITCHERY_LOG_STRIPPED.get(), 1);
			addLog(GameRegistry.findBlock("witchery", "witchlog"), 2, ModBlocks.WITCHERY_LOG_STRIPPED.get(), 2);
		}

		if(ModBlocks.BOP_WOOD.isEnabled() && ModBlocks.BOP_WOOD_STRIPPED.isEnabled()) {
			addLog(ModBlocks.BOP_WOOD.get(), 0, ModBlocks.BOP_WOOD_STRIPPED.get(), 0);
			addLog(ModBlocks.BOP_WOOD.get(), 1, ModBlocks.BOP_WOOD_STRIPPED.get(), 1);
			addLog(ModBlocks.BOP_WOOD.get(), 2, ModBlocks.BOP_WOOD_STRIPPED.get(), 2);
			addLog(ModBlocks.BOP_WOOD.get(), 3, ModBlocks.BOP_WOOD_STRIPPED.get(), 3);
		}

		if(ModBlocks.BOP_WOOD2.isEnabled() && ModBlocks.BOP_WOOD_STRIPPED2.isEnabled()) {
			addLog(ModBlocks.BOP_WOOD2.get(), 0, ModBlocks.BOP_WOOD_STRIPPED2.get(), 0);
			addLog(ModBlocks.BOP_WOOD2.get(), 1, ModBlocks.BOP_WOOD_STRIPPED2.get(), 1);
			addLog(ModBlocks.BOP_WOOD2.get(), 2, ModBlocks.BOP_WOOD_STRIPPED2.get(), 2);
			addLog(ModBlocks.BOP_WOOD2.get(), 3, ModBlocks.BOP_WOOD_STRIPPED2.get(), 3);
		}

		if(ModBlocks.BOP_WOOD3.isEnabled() && ModBlocks.BOP_WOOD_STRIPPED3.isEnabled()) {
			addLog(ModBlocks.BOP_WOOD3.get(), 0, ModBlocks.BOP_WOOD_STRIPPED3.get(), 0);
			addLog(ModBlocks.BOP_WOOD3.get(), 1, ModBlocks.BOP_WOOD_STRIPPED3.get(), 1);
			addLog(ModBlocks.BOP_WOOD3.get(), 2, ModBlocks.BOP_WOOD_STRIPPED3.get(), 2);
			addLog(ModBlocks.BOP_WOOD3.get(), 3, ModBlocks.BOP_WOOD_STRIPPED3.get(), 3);
		}

		if(ModBlocks.BOP_WOOD4.isEnabled() && ModBlocks.BOP_WOOD_STRIPPED4.isEnabled()) {
			addLog(ModBlocks.BOP_WOOD4.get(), 0, ModBlocks.BOP_WOOD_STRIPPED4.get(), 0);
			addLog(ModBlocks.BOP_WOOD4.get(), 1, ModBlocks.BOP_WOOD_STRIPPED4.get(), 1);
			addLog(ModBlocks.BOP_WOOD4.get(), 2, ModBlocks.BOP_WOOD_STRIPPED4.get(), 2);
			addLog(ModBlocks.BOP_WOOD4.get(), 3, ModBlocks.BOP_WOOD_STRIPPED4.get(), 3);
		}

		if(ModBlocks.THAUMCRAFT_WOOD.isEnabled() && ModBlocks.THAUMCRAFT_WOOD_STRIPPED.isEnabled()) {
			addLog(ModBlocks.THAUMCRAFT_WOOD.get(), 0, ModBlocks.THAUMCRAFT_WOOD_STRIPPED.get(), 0);
			addLog(ModBlocks.THAUMCRAFT_WOOD.get(), 1, ModBlocks.THAUMCRAFT_WOOD_STRIPPED.get(), 1);
		}

		if(ModBlocks.WITCHERY_WOOD.isEnabled() && ModBlocks.WITCHERY_WOOD_STRIPPED.isEnabled()) {
			addLog(ModBlocks.WITCHERY_WOOD.get(), 0, ModBlocks.WITCHERY_WOOD_STRIPPED.get(), 0);
			addLog(ModBlocks.WITCHERY_WOOD.get(), 1, ModBlocks.WITCHERY_WOOD_STRIPPED.get(), 1);
			addLog(ModBlocks.WITCHERY_WOOD.get(), 2, ModBlocks.WITCHERY_WOOD_STRIPPED.get(), 2);
		}
	}
}
