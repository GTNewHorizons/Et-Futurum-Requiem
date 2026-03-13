package ganymedes01.etfuturum.mixins.early.blockfallingparticles.client;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.fallingdustcolor.IFallingDustColor;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.compat.ExternalContent;

@Mixin(BlockFalling.class)
public abstract class MixinBlockFalling extends Block implements IFallingDustColor {

    protected MixinBlockFalling(Material materialIn) {
        super(materialIn);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (rand.nextInt(16) == 0) {
            if (BlockFalling.func_149831_e(world, x, y - 1, z)) { // canFallBelow
                double d0 = (float) x + rand.nextFloat();
                double d1 = (double) y - .05D;
                double d2 = (float) z + rand.nextFloat();
                CustomParticles
                    .spawnFallingDustParticle(world, d0, d1, d2, getDustColor(world.getBlockMetadata(x, y, z)));
            }
        }
    }

    @Override
    public int getDustColor(int meta) {
        if (this == ExternalContent.Blocks.NATURA_HEAT_SAND.get()) {
            return 0xFFD87F33;
        }
        if (this == ExternalContent.Blocks.NETHERLICIOUS_NETHER_GRAVEL.get()) {
            return 0xFF191919;
        }
        if (this == Blocks.gravel || this == ModBlocks.OLD_GRAVEL.get()
            || this == ExternalContent.Blocks.TCON_GRAVEL_ORE.get()) {
            return 0xFF807C7B;
        }
        return getMapColor(meta).colorValue | 0xFF000000;
    }
}
