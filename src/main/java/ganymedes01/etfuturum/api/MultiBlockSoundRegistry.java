package ganymedes01.etfuturum.api;

import ganymedes01.etfuturum.api.mappings.BasicMultiBlockSound;
import ganymedes01.etfuturum.api.mappings.MultiBlockSoundContainer;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import roadhog360.hogutils.api.blocksanditems.block.IMultiBlockSound;
import roadhog360.hogutils.api.utils.RecipeHelper;

import java.util.HashMap;
import java.util.Map;

/// Implement the {@link IMultiBlockSound} interface on your block instead.
/// If you want to do it for another modded block, use mixins.
/// If you want it for your own block optionally, you may annotate your class with {@link cpw.mods.fml.common.Optional} or use mixins.
@Deprecated
public class MultiBlockSoundRegistry {

	@Deprecated
	public static final Map<Block, MultiBlockSoundContainer> multiBlockSounds = new HashMap<>();

	@Deprecated
	public static void addBasic(Block block, Block.SoundType type, int... metas) {
		if (RecipeHelper.validateItems(block)) {
			MultiBlockSoundContainer mbs = multiBlockSounds.computeIfAbsent(block, o -> new BasicMultiBlockSound());
			if (mbs instanceof BasicMultiBlockSound basicMultiBlockSound) {
				basicMultiBlockSound.setTypes(Utils.getSound(type), metas);
			}
		}
	}

	@Deprecated
	public enum BlockSoundType {
		BREAK,
		PLACE,
		WALK,
		HIT
	}
}