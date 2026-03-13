package ganymedes01.etfuturum.mixins.early.coloredgrassitem.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks {

    @Shadow
    public static boolean fancyGrass;

    @Shadow
    public abstract boolean hasOverrideBlockTexture();

    @Shadow
    public boolean useInventoryTint;

    @Shadow
    public abstract void renderFaceZNeg(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_,
        IIcon p_147761_8_);

    @Shadow
    public abstract void renderFaceZPos(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_,
        IIcon p_147761_8_);

    @Shadow
    public abstract void renderFaceXNeg(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_,
        IIcon p_147761_8_);

    @Shadow
    public abstract void renderFaceXPos(Block p_147761_1_, double p_147761_2_, double p_147761_4_, double p_147761_6_,
        IIcon p_147761_8_);

    /**
     * @author embeddedt
     * @reason render the grass overlay texture with the appropriate colors
     */
    @Inject(method = "renderBlockAsItem", at = @At("RETURN"))
    private void renderColoredGrassSides(Block block, int meta, float p_147800_3_, CallbackInfo ci,
        @Local(ordinal = 0) boolean isGrass, @Local(ordinal = 0) Tessellator tessellator) {
        if (fancyGrass && isGrass
            && block.getRenderType() == 0
            && this.useInventoryTint
            && !this.hasOverrideBlockTexture()) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

            // Set render color
            int k = block.getRenderColor(meta);
            float f2 = (float) (k >> 16 & 255) / 255.0F;
            float f3 = (float) (k >> 8 & 255) / 255.0F;
            float f4 = (float) (k & 255) / 255.0F;
            GL11.glColor4f(f2 * p_147800_3_, f3 * p_147800_3_, f4 * p_147800_3_, 1.0F);

            // Render the four faces

            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            double epsilon = 0.001D;
            this.renderFaceZNeg(block, 0.0D, 0.0D, -epsilon, BlockGrass.getIconSideOverlay());
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            this.renderFaceZPos(block, 0.0D, 0.0D, epsilon, BlockGrass.getIconSideOverlay());
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            this.renderFaceXNeg(block, -epsilon, 0.0D, 0.0D, BlockGrass.getIconSideOverlay());
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            this.renderFaceXPos(block, epsilon, 0.0D, 0.0D, BlockGrass.getIconSideOverlay());
            tessellator.draw();

            GL11.glColor4f(p_147800_3_, p_147800_3_, p_147800_3_, 1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }
}
