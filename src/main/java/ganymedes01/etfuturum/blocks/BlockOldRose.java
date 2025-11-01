package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockOldRose extends BaseFlower {

	public BlockOldRose() {
		setBlockName(Utils.getUnlocalisedName("rose"));
		setBlockTextureName(Tags.MOD_ID + ":flower_rose");
	}
}