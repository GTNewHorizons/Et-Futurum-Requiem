package ganymedes01.etfuturum.blocks.ores.modded;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.blocks.ores.BaseDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;

public class BlockDeepslateDraconiumOre extends BaseDeepslateOre {

    public BlockDeepslateDraconiumOre() {
        super();
        setNames("deepslate_draconium_ore");
    }

    @Override
    public String getTextureSubfolder() {
        return "draconic";
    }

    @Override
    public Block getBase() {
        return ExternalContent.Blocks.DRACONIUM_ORE.get();
    }
}
