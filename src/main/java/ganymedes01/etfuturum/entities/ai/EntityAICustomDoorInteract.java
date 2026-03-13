package ganymedes01.etfuturum.entities.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

/*
 * Copy paste from vanilla and adapted to work with other doors
 */
public abstract class EntityAICustomDoorInteract extends EntityAIBase {

    protected EntityLiving theEntity;
    protected int entityPosX;
    protected int entityPosY;
    protected int entityPosZ;
    protected BlockDoor doorBlock;
    boolean hasStoppedDoorInteraction;
    float entityPositionX;
    float entityPositionZ;

    public EntityAICustomDoorInteract(EntityLiving p_i1621_1_) {
        theEntity = p_i1621_1_;
    }

    @Override
    public boolean shouldExecute() {
        if (!theEntity.isCollidedHorizontally) return false;
        PathNavigate pathnavigate = theEntity.getNavigator();
        PathEntity pathentity = pathnavigate.getPath();

        if (pathentity != null && !pathentity.isFinished() && pathnavigate.getCanBreakDoors()) {
            for (int i = 0; i
                < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i) {
                PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
                entityPosX = pathpoint.xCoord;
                entityPosY = pathpoint.yCoord + 1;
                entityPosZ = pathpoint.zCoord;

                if (theEntity.getDistanceSq(entityPosX, theEntity.posY, entityPosZ) <= 2.25D) {
                    doorBlock = getWoodenDoorBlock(entityPosX, entityPosY, entityPosZ);

                    if (doorBlock != null) {
                        return true;
                    }
                }
            }

            entityPosX = MathHelper.floor_double(theEntity.posX);
            entityPosY = MathHelper.floor_double(theEntity.posY + 1.0D);
            entityPosZ = MathHelper.floor_double(theEntity.posZ);
            doorBlock = getWoodenDoorBlock(entityPosX, entityPosY, entityPosZ);
            return doorBlock != null;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return !hasStoppedDoorInteraction;
    }

    @Override
    public void startExecuting() {
        hasStoppedDoorInteraction = false;
        entityPositionX = (float) (entityPosX + 0.5F - theEntity.posX);
        entityPositionZ = (float) (entityPosZ + 0.5F - theEntity.posZ);
    }

    @Override
    public void updateTask() {
        float f = (float) (entityPosX + 0.5F - theEntity.posX);
        float f1 = (float) (entityPosZ + 0.5F - theEntity.posZ);
        float f2 = entityPositionX * f + entityPositionZ * f1;

        if (f2 < 0.0F) hasStoppedDoorInteraction = true;
    }

    private BlockDoor getWoodenDoorBlock(int x, int y, int z) {
        Block block = theEntity.worldObj.getBlock(x, y, z);
        return block instanceof BlockDoor && canOpen((BlockDoor) block, x, y, z) ? (BlockDoor) block : null;
    }

    private boolean canOpen(BlockDoor door, int x, int y, int z) {
        return door != Blocks.iron_door;
    }
}
