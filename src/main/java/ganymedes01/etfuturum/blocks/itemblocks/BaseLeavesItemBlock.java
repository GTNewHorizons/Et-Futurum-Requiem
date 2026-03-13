package ganymedes01.etfuturum.blocks.itemblocks;

import net.minecraft.block.Block;

public class BaseLeavesItemBlock extends BaseItemBlock {

    public BaseLeavesItemBlock(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int p_77647_1_) {
        return p_77647_1_ | 4;
    }
}
