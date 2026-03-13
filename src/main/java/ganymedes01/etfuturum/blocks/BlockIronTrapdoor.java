package ganymedes01.etfuturum.blocks;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.core.utils.Utils;

public class BlockIronTrapdoor extends BlockTrapDoor {

    public BlockIronTrapdoor() {
        super(Material.iron);
        setHardness(5.0F);
        setStepSound(soundTypeMetal);
        setBlockTextureName("iron_trapdoor");
        setBlockName(Utils.getUnlocalisedName("iron_trapdoor"));
        setCreativeTab(EtFuturum.creativeTabBlocks);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        return false;
    }
}
