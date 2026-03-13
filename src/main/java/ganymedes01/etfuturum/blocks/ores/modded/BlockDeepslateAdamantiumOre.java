package ganymedes01.etfuturum.blocks.ores.modded;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.blocks.ores.BaseDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;

public class BlockDeepslateAdamantiumOre extends BaseDeepslateOre {

    public BlockDeepslateAdamantiumOre() {
        setNames("deepslate_adamantium_ore");
    }

    @Override
    public String getTextureSubfolder() {
        return "simpleores";
    }

    @Override
    public Block getBase() {
        return ExternalContent.Blocks.SIMPLEORES_ADAMANTIUM_ORE.get();
    }
}
