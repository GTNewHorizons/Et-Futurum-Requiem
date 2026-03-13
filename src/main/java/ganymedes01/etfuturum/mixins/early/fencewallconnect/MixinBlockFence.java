package ganymedes01.etfuturum.mixins.early.fencewallconnect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockFence.class, priority = 0)
public class MixinBlockFence {

    /**
     * @author Roadhog360
     * @reason Corrects hardcoded fence logic, to connect with modded ones.
     */
    @Overwrite
    public boolean canConnectFenceTo(IBlockAccess p_149826_1_, int p_149826_2_, int p_149826_3_, int p_149826_4_) {
        Block block = p_149826_1_.getBlock(p_149826_2_, p_149826_3_, p_149826_4_);
        return block instanceof BlockFence && block.getMaterial() == ((BlockFence) (Object) this).blockMaterial
            || block instanceof BlockFenceGate
            || (block.blockMaterial.isOpaque() && block.renderAsNormalBlock() && block.blockMaterial != Material.gourd);
    }
}
