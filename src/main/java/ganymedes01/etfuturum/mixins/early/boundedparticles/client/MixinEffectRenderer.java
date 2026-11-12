package ganymedes01.etfuturum.mixins.early.boundedparticles.client;

import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = EffectRenderer.class, priority = 500) // Low priority for normal mixins to apply on top of overwrite.
public abstract class MixinEffectRenderer {

	@Shadow
	protected World worldObj;

	@Shadow
	public abstract void addEffect(EntityFX p_78873_1_);


	/**
	 * The addBlockDestroyEffects code from 1.18, ported to 1.7.10. Makes block destroy particles stay within the outline of the block.
	 * Code found by makamys and ported by Roadhog360.
     * 
	 * @author Roadhog360
	 * @author makamys
     * @author nicksitnikov
     * @reason Changes control flow - loops over every box in the bounding boxes of the block & emits particles at each.
	 */
	@Overwrite
	public void addBlockDestroyEffects(int x, int y, int z, Block block, int p_147215_5_) {
		if (!block.isAir(worldObj, x, y, z) && !block.addDestroyEffects(worldObj, x, y, z, p_147215_5_, (EffectRenderer) (Object) this)) {
            for (AxisAlignedBB box : efr$getBoxes(x, y, z, block)) {
				double sizeX = Math.min(1.0, box.maxX - box.minX);
				double sizeY = Math.min(1.0, box.maxY - box.minY);
				double sizeZ = Math.min(1.0, box.maxZ - box.minZ);
				int gridSizeX = Math.max(2, MathHelper.ceiling_double_int(sizeX * 4));
				int gridSizeY = Math.max(2, MathHelper.ceiling_double_int(sizeY * 4));
				int gridSizeZ = Math.max(2, MathHelper.ceiling_double_int(sizeZ * 4));
				for (int xx = 0; xx < gridSizeX; ++xx) {
					for (int yy = 0; yy < gridSizeY; ++yy) {
						for (int zz = 0; zz < gridSizeZ; ++zz) {
							double offsetX = ((double) xx + 0.5D) / (double) gridSizeX;
							double offsetY = ((double) yy + 0.5D) / (double) gridSizeY;
							double offsetZ = ((double) zz + 0.5D) / (double) gridSizeZ;
							double posX = offsetX * sizeX + box.minX;
							double posY = offsetY * sizeY + box.minY;
							double posZ = offsetZ * sizeZ + box.minZ;
							this.addEffect(new EntityDiggingFX(this.worldObj, posX, posY, posZ, offsetX - 0.5, offsetY - 0.5, offsetZ - 0.5, block, p_147215_5_).applyColourMultiplier(x, y, z));
						}
					}
				}
			}
		}
	}

    @Unique
    private List<AxisAlignedBB> efr$getBoxes(int x, int y, int z, Block block) {
        AxisAlignedBB fullBox = AxisAlignedBB.getBoundingBox(x, y, z, (double) x + 1, (double) y + 1, (double) z + 1);
        
        final List<AxisAlignedBB> list = new ArrayList<>();
        try {
            // Attempt to obtain all of this block's collision boxes (within this block space)
            block.addCollisionBoxesToList(worldObj, x, y, z, fullBox, list, null);
        } catch (NullPointerException e) {
            list.clear(); //If any elements were already added to the list, let's clear them so we don't get half-assed breaking particles
        }
        //If it throws an NPE (the collision code wanted an entity) or has no collision that means we cannot reasonably determine the block's bounds.
        //In that case this just falls back to code similar to the old one which only bounds it by the block's selection bounding box.
        if (list.isEmpty()) {
            AxisAlignedBB aabb = block.getSelectedBoundingBoxFromPool(worldObj, x, y, z);
            if (aabb != null) {
                list.add(aabb);
            } else {
                // Fall back to the original logic of spawning particles in the full block
                list.add(fullBox);
            }
        }

        return list;
    }
}