package ganymedes01.etfuturum.mixins.early.fencewallconnect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockWall.class)
public class MixinBlockWall {

    /**
     * @author Roadhog360
     * @reason Corrects hardcoded wall logic, to connect with modded ones.
     */
    @Overwrite
    public boolean canConnectWallTo(IBlockAccess p_150091_1_, int p_150091_2_, int p_150091_3_, int p_150091_4_) {
        Block block = p_150091_1_.getBlock(p_150091_2_, p_150091_3_, p_150091_4_);
        return block instanceof BlockWall && block.getMaterial() == ((BlockWall) (Object) this).blockMaterial
            || block instanceof BlockFenceGate
            || (block.blockMaterial.isOpaque() && block.renderAsNormalBlock() && block.blockMaterial != Material.gourd);
    }
}
