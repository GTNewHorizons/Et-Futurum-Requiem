package ganymedes01.etfuturum.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.lib.RenderIDs;

public class BaseTrapdoor extends BlockTrapDoor {

    public BaseTrapdoor(Material material, String type) {
        super(material);
        disableStats();
        setHardness(3.0F);
        setBlockName(Utils.getUnlocalisedName(type + "_trapdoor"));
        setBlockTextureName(type + "_trapdoor");
        setCreativeTab(EtFuturum.creativeTabBlocks);
        setBlockSound(getMaterial() == Material.iron ? Block.soundTypeMetal : Block.soundTypeWood);
    }

    public BaseTrapdoor(String type) {
        this(Material.wood, type);
    }

    public BaseTrapdoor setBlockSound(SoundType type) {
        Utils.setBlockSound(this, type);
        return this;
    }

    @Override
    public int getRenderType() {
        return RenderIDs.TRAPDOOR;
    }

}
