package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.Reference;

public class BlockOldRose extends BaseFlower {

    public BlockOldRose() {
        setBlockName(Utils.getUnlocalisedName("rose"));
        setBlockTextureName(Reference.MOD_ID + ":flower_rose");
    }
}
