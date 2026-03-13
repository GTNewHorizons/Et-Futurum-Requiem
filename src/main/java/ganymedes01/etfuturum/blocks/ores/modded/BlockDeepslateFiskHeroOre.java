package ganymedes01.etfuturum.blocks.ores.modded;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;

import ganymedes01.etfuturum.blocks.IEmissiveLayerBlock;
import ganymedes01.etfuturum.blocks.ores.BaseSubtypesDeepslateOre;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.lib.RenderIDs;

public class BlockDeepslateFiskHeroOre extends BaseSubtypesDeepslateOre implements IEmissiveLayerBlock {

    public BlockDeepslateFiskHeroOre() {
        super(
            "deepslate_tutridium_ore",
            "tutridium_deepslate",
            "deepslate_vibranium_ore",
            "deepslate_dwarf_star_ore",
            "deepslate_olivine_ore",
            "deepslate_eternium_ore",
            "eternium_deepslate");
    }

    @Override
    public String getTextureSubfolder() {
        return "fiskheroes";
    }

    @Override
    public Block getBase(int meta) {
        switch (meta) {
            case 1:
                return ExternalContent.Blocks.FISK_TUTRIDIUM_SPECKLED_STONE.get();
            case 2:
                return ExternalContent.Blocks.FISK_VIBRANIUM_ORE.get();
            case 3:
                return ExternalContent.Blocks.FISK_DWARF_STAR_ORE.get();
            case 4:
                return ExternalContent.Blocks.FISK_OLIVINE_ORE.get();
            case 5:
                return ExternalContent.Blocks.FISK_ETERNIUM_ORE.get();
            case 6:
                return ExternalContent.Blocks.FISK_ETERNIUM_INFUSED_STONE.get();
            case 0:
            default:
                return ExternalContent.Blocks.FISK_TUTRIDIUM_ORE.get();
        }
    }

    @Override
    public IIcon getSecondLayerIcon(int side, int meta) {
        return ExternalContent.Blocks.FISK_VIBRANIUM_ORE.get()
            .getIcon(0, 0);
    }

    @Override
    public int getEmissiveMinBrightness(int meta) {
        return 15;
    }

    @Override
    public boolean isMetaNormalBlock(int meta) {
        return meta != 2;
    }

    @Override
    public boolean isSecondLayerAbove(int meta) {
        return true;
    }

    @Override
    public boolean itemBlockGlows(int meta) {
        return true;
    }

    @Override
    public int getRenderType() {
        return RenderIDs.EMISSIVE_DOUBLE_LAYER;
    }
}
