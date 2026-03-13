package ganymedes01.etfuturum.blocks.ores.modded;

import net.minecraft.block.Block;

import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;

public class BlockDeepslateBiomesOPlentyOre extends BaseSubtypesDeepslateOre {

    public BlockDeepslateBiomesOPlentyOre() {
        super(
            "deepslate_ruby_ore",
            "deepslate_peridot_ore",
            "deepslate_topaz_ore",
            "deepslate_tanzanite_ore",
            "deepslate_malachite_ore",
            "deepslate_sapphire_ore",
            "deepslate_amber_ore");
    }

    @Override
    public String getTextureSubfolder() {
        return "bop";
    }

    @Override
    public Block getBase(int meta) {
        return ExternalContent.Blocks.BOP_GEM_ORE.get();
    }

    @Override
    public int getBaseMeta(int meta) {
        switch (meta) {
            case 0:
                return 2;
            case 1:
                return 4;
            case 2:
                return 6;
            case 3:
                return 8;
            case 4:
                return 10;
            case 5:
                return 12;
            case 6:
                return 14;
        }
        return 0;
    }
}
