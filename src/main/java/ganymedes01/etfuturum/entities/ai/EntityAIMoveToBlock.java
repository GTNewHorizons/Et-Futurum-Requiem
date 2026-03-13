package ganymedes01.etfuturum.entities.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;

public abstract class EntityAIMoveToBlock extends EntityAIBase {

    private final EntityCreature theEntity;
    private final double movementSpeed;
    protected int runDelay;
    private int timeoutCounter;
    private int maxStayTicks;
    protected BlockPos destinationBlock;
    private boolean isAboveDestination;
    private final int searchLength;

    public EntityAIMoveToBlock(EntityCreature creature, double speedIn, int length) {
        destinationBlock = BlockPos.ORIGIN;
        theEntity = creature;
        movementSpeed = speedIn;
        searchLength = length;
        setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (runDelay > 0) {
            runDelay--;
            return false;
        }
        runDelay = 200 + theEntity.getRNG()
            .nextInt(200);
        return searchForDestination();
    }

    @Override
    public boolean continueExecuting() {
        return timeoutCounter >= -maxStayTicks && timeoutCounter <= 1200
            && shouldMoveTo(theEntity.worldObj, destinationBlock);
    }

    @Override
    public void startExecuting() {
        theEntity.getNavigator()
            .tryMoveToXYZ(
                destinationBlock.getX() + 0.5D,
                destinationBlock.getY() + 1,
                destinationBlock.getZ() + 0.5D,
                movementSpeed);
        timeoutCounter = 0;
        maxStayTicks = theEntity.getRNG()
            .nextInt(
                theEntity.getRNG()
                    .nextInt(1200) + 1200)
            + 1200;
    }

    @Override
    public void resetTask() {}

    @Override
    public void updateTask() {
        if (theEntity.getDistanceSq(
            destinationBlock.up()
                .getX(),
            destinationBlock.up()
                .getY(),
            destinationBlock.up()
                .getZ())
            > 1.0D) {
            isAboveDestination = false;
            timeoutCounter++;

            if (timeoutCounter % 40 == 0) theEntity.getNavigator()
                .tryMoveToXYZ(
                    destinationBlock.getX() + 0.5D,
                    destinationBlock.getY() + 1,
                    destinationBlock.getZ() + 0.5D,
                    movementSpeed);
        } else {
            isAboveDestination = true;
            timeoutCounter--;
        }
    }

    protected boolean getIsAboveDestination() {
        return isAboveDestination;
    }

    private boolean searchForDestination() {
        int i = searchLength;
        BlockPos blockpos = new BlockPos(theEntity);

        for (int j = 0; j <= 1; j = j > 0 ? -j : 1 - j)
            for (int k = 0; k < i; ++k) for (int l = 0; l <= k; l = l > 0 ? -l : 1 - l)
                for (int i1 = l < k && l > -k ? k : 0; i1 <= k; i1 = i1 > 0 ? -i1 : 1 - i1) {
                    BlockPos blockpos1 = blockpos.add(l, j - 1, i1);

                    if (theEntity.isWithinHomeDistance(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ())
                        && shouldMoveTo(theEntity.worldObj, blockpos1)) {
                        destinationBlock = blockpos1;
                        return true;
                    }
                }

        return false;
    }

    protected abstract boolean shouldMoveTo(World worldIn, BlockPos pos);
}
