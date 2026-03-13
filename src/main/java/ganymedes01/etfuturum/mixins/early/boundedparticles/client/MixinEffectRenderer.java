package ganymedes01.etfuturum.mixins.early.boundedparticles.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public class MixinEffectRenderer {

    @Shadow
    protected World worldObj;

    @Shadow
    public void addEffect(EntityFX p_78873_1_) {}

    /**
     * The addBlockDestroyEffects code from 1.18, ported to 1.7.10. Makes block destroy particles stay within the
     * outline of the block.
     * Code found by makamys and ported by Roadhog360.
     * <p>
     * Injects at head and returns after the custom code.
     * This is because we're rewriting it, but @Overwrite is conflict-prone, so we'll do this to soft-fail instead.
     * We also use the original code in specific cases where all of our fallbacks fail.
     *
     * @author Roadhog360
     * @author makamys
     */
    @Inject(method = "addBlockDestroyEffects", at = @At(value = "HEAD"), cancellable = true)
    public void addNewBlockDestroyEffects(int x, int y, int z, Block block, int p_147215_5_, CallbackInfo ci) {
        if (!block.isAir(worldObj, x, y, z)
            && !block.addDestroyEffects(worldObj, x, y, z, p_147215_5_, (EffectRenderer) (Object) this)) {
            AxisAlignedBB FULL_AABB = AxisAlignedBB
                .getBoundingBox(x, y, z, (double) x + 1, (double) y + 1, (double) z + 1);
            List<AxisAlignedBB> list = new ArrayList<>();
            try {
                // Check if the block's collision boxes are within a single block estate.
                block.addCollisionBoxesToList(worldObj, x, y, z, FULL_AABB, list, null);
            } catch (NullPointerException e) {
                list.clear(); // If any elements were already added to the list, let's clear them so we don't get
                              // half-assed breaking particles
            }
            // If it throws an NPE (the collision code wanted an entity) or has no collision that means we cannot
            // reasonably determine the block's bounds.
            // In that case this just falls back to code similar to the old one which only bounds it by the block's
            // selection bounding box.
            if (list.isEmpty()) {
                AxisAlignedBB aabb = block.getSelectedBoundingBoxFromPool(worldObj, x, y, z);
                if (aabb != null) {
                    list.add(aabb);
                } else {
                    // This will skip the callbackinfo return at the end, which will cause the original code to run.
                    // This can only be reached if there were no collisions, the block's collision logic requires a
                    // collider, AND the block has no selection box.
                    return;
                }
            }
            for (AxisAlignedBB box : list) {
                box.minX -= x;
                box.maxX -= x;
                box.minY -= y;
                box.maxY -= y;
                box.minZ -= z;
                box.maxZ -= z;
                double d = Math.min(1.0, box.maxX - box.minX);
                double e = Math.min(1.0, box.maxY - box.minY);
                double f = Math.min(1.0, box.maxZ - box.minZ);
                int i = Math.max(2, MathHelper.ceiling_double_int(d * 4));
                int j = Math.max(2, MathHelper.ceiling_double_int(e * 4));
                int k = Math.max(2, MathHelper.ceiling_double_int(f * 4));
                for (int l = 0; l < i; ++l) {
                    for (int m = 0; m < j; ++m) {
                        for (int n = 0; n < k; ++n) {
                            double g = ((double) l + 0.5D) / (double) i;
                            double h = ((double) m + 0.5D) / (double) j;
                            double o = ((double) n + 0.5D) / (double) k;
                            double p = g * d + box.minX;
                            double q = h * e + box.minY;
                            double r = o * f + box.minZ;
                            this.addEffect(
                                new EntityDiggingFX(
                                    this.worldObj,
                                    x + p,
                                    y + q,
                                    z + r,
                                    g - 0.5,
                                    h - 0.5,
                                    o - 0.5,
                                    block,
                                    p_147215_5_).applyColourMultiplier(x, y, z));
                        }
                    }
                }
            }
            ci.cancel();
        }
    }
}
