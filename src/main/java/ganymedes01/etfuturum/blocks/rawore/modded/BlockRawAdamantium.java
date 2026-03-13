package ganymedes01.etfuturum.blocks.rawore.modded;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import ganymedes01.etfuturum.blocks.rawore.BaseRawOreBlock;
import ganymedes01.etfuturum.compat.ExternalContent;

public class BlockRawAdamantium extends BaseRawOreBlock {

    public BlockRawAdamantium() {
        super(Material.rock);
        setNames("raw_adamantium_block");
    }

    @Override
    public String getTextureSubfolder() {
        return "simpleores";
    }

    @Override
    protected Block getBase() {
        return ExternalContent.Blocks.SIMPLEORES_ADAMANTIUM_BLOCK.get();
    }
}
