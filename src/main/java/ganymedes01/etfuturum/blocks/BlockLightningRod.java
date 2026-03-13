package ganymedes01.etfuturum.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.tileentities.TileEntityLightningRod;

public class BlockLightningRod extends BaseBlock {

    public BlockLightningRod() {
        super(Material.iron);
        setHardness(3);
        setResistance(6);
        setHarvestLevel("pickaxe", 1);
        setNames("lightning_rod");
        setBlockSound(ModSounds.soundCopper);
        setTickRandomly(true);
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityLightningRod();
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
