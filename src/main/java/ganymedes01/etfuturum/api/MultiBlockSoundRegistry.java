package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.api.mappings.BasicMultiBlockSound;
import ganymedes01.etfuturum.api.mappings.MultiBlockSoundContainer;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.HashMap;
import java.util.Map;

public class MultiBlockSoundRegistry {

	public static final Map<Block, MultiBlockSoundContainer> multiBlockSounds = new HashMap<>();

	public static void addBasic(Block block, Block.SoundType type, int... metas) {
		if (RecipeHelper.validateItems(block)) {
			MultiBlockSoundContainer mbs = multiBlockSounds.getOrDefault(block, new BasicMultiBlockSound());
			if (mbs instanceof BasicMultiBlockSound) {
				((BasicMultiBlockSound) mbs).setTypes(Utils.getSound(type), metas);
				if (!multiBlockSounds.containsKey(block)) {
					multiBlockSounds.put(block, mbs);
				}
			}
		}
	}

	public enum BlockSoundType {
		BREAK,
		PLACE,
		WALK,
		HIT
	}
}