package ganymedes01.etfuturum.blocks;

import net.minecraft.util.IIcon;

public interface IDoubleLayerBlock {

    IIcon getSecondLayerIcon(int side, int meta);

    /**
     * Used by Thaumcraft ores so the amber and cinnibar deepslate ores render normally.
     *
     * @param meta
     * @return
     */
    default boolean isMetaNormalBlock(int meta) {
        return false;
    }

    default boolean isSecondLayerAbove(int meta) {
        return false;
    }
}
