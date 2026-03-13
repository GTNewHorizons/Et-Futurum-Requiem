package ganymedes01.etfuturum.blocks.ores;

import net.minecraft.block.BlockOre;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockCopperOre extends BlockOre {

    public BlockCopperOre() {
        super();
        setHardness(3);
        setResistance(3);
        setBlockName(Utils.getUnlocalisedName("copper_ore"));
        setBlockTextureName("copper_ore");
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }
}
