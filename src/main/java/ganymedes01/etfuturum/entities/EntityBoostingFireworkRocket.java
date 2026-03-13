package ganymedes01.etfuturum.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import ganymedes01.etfuturum.elytra.IElytraPlayer;

public class EntityBoostingFireworkRocket extends EntityFireworkRocket {

    private static final int BOOSTED_ENTITY_ID = 9;
    private EntityLivingBase boostedEntity;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(BOOSTED_ENTITY_ID, 0);
    }

    public EntityBoostingFireworkRocket(World world) {
        super(world);
    }

    public EntityBoostingFireworkRocket(World world, ItemStack stack, EntityLivingBase entityToBoost) {
        super(world, entityToBoost.posX, entityToBoost.posY, entityToBoost.posZ, stack);
        this.dataWatcher.updateObject(9, entityToBoost.getEntityId());
        this.boostedEntity = entityToBoost;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isAttachedToEntity()) {
            if (this.boostedEntity == null) {
                Entity entity = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(BOOSTED_ENTITY_ID));

                if (entity instanceof EntityLivingBase) {
                    this.boostedEntity = (EntityLivingBase) entity;
                }
            }

            if (this.boostedEntity != null) {
                if (this.boostedEntity instanceof IElytraPlayer
                    && ((IElytraPlayer) this.boostedEntity).etfu$isElytraFlying()) {
                    Vec3 vec3d = this.boostedEntity.getLookVec();
                    this.boostedEntity.motionX += vec3d.xCoord * 0.1D
                        + (vec3d.xCoord * 1.5D - this.boostedEntity.motionX) * 0.5D;
                    this.boostedEntity.motionY += vec3d.yCoord * 0.1D
                        + (vec3d.yCoord * 1.5D - this.boostedEntity.motionY) * 0.5D;
                    this.boostedEntity.motionZ += vec3d.zCoord * 0.1D
                        + (vec3d.zCoord * 1.5D - this.boostedEntity.motionZ) * 0.5D;
                } else {
                    this.setDead();
                }

                this.setPosition(this.boostedEntity.posX, this.boostedEntity.posY, this.boostedEntity.posZ);
                this.motionX = this.boostedEntity.motionX;
                this.motionY = this.boostedEntity.motionY;
                this.motionZ = this.boostedEntity.motionZ;
            }
        }
    }

    public boolean isAttachedToEntity() {
        return this.dataWatcher.getWatchableObjectInt(BOOSTED_ENTITY_ID) > 0;
    }
}
