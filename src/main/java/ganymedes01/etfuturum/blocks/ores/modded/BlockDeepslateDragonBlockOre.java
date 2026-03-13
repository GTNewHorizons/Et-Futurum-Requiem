package ganymedes01.etfuturum.blocks.ores.modded;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;

public class BlockDeepslateDragonBlockOre extends BaseSubtypesDeepslateOre {

    public BlockDeepslateDragonBlockOre() {
        super("deepslate_warenai_ore", "deepslate_jjay_ore", "deepslate_dlog_ore", "deepslate_lehnori_ore");
    }

    @Override
    public Block getBase(int meta) {
        switch (meta) {
            case 1:
                return ExternalContent.Blocks.DBC_JJAY_ORE.get();
            case 2:
                return ExternalContent.Blocks.DBC_DLOG_ORE.get();
            case 3:
                return ExternalContent.Blocks.DBC_LEHNORI_ORE.get();
            default:
            case 0:
                return ExternalContent.Blocks.DBC_WARENAI_ORE.get();
        }
    }

    @Override
    public String getTextureSubfolder() {
        return "dbc";
    }
}
