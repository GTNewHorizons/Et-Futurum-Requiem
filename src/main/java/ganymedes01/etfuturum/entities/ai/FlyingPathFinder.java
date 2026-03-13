package ganymedes01.etfuturum.entities.ai;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class FlyingPathFinder extends PathFinder {

    private final Map<PathPoint, Float> costMalusMap = new WeakHashMap<>();

    public FlyingPathFinder(IBlockAccess p_i2137_1_, boolean p_i2137_2_, boolean p_i2137_3_, boolean p_i2137_4_,
        boolean p_i2137_5_) {
        super(p_i2137_1_, p_i2137_2_, p_i2137_3_, p_i2137_4_, p_i2137_5_);
    }

    @Override
    public int findPathOptions(Entity entity, PathPoint currentPoint, PathPoint offsetPoint/* no idea what this is */,
        PathPoint targetPoint, float maxDistance) {
        int i = 0;
        PathPoint pathpoint = openPointFlying(entity, targetPoint.xCoord, targetPoint.yCoord, targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint)) {
            pathOptions[i++] = pathpoint;
        }

        PathPoint pathpoint1 = openPointFlying(entity, targetPoint.xCoord - 1, targetPoint.yCoord, targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint1)) {
            pathOptions[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = openPointFlying(entity, targetPoint.xCoord + 1, targetPoint.yCoord, targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint2)) {
            pathOptions[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = openPointFlying(entity, targetPoint.xCoord, targetPoint.yCoord, targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint3)) {
            pathOptions[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = openPointFlying(entity, targetPoint.xCoord, targetPoint.yCoord + 1, targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint4)) {
            pathOptions[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = openPointFlying(entity, targetPoint.xCoord, targetPoint.yCoord - 1, targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint5)) {
            pathOptions[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = openPointFlying(
            entity,
            targetPoint.xCoord,
            targetPoint.yCoord + 1,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint6) && this.isSafe(pathpoint) && this.isSafe(pathpoint4)) {
            pathOptions[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord + 1,
            targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint7) && this.isSafe(pathpoint1) && this.isSafe(pathpoint4)) {
            pathOptions[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord + 1,
            targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint8) && this.isSafe(pathpoint2) && this.isSafe(pathpoint4)) {
            pathOptions[i++] = pathpoint8;
        }

        PathPoint pathpoint9 = openPointFlying(
            entity,
            targetPoint.xCoord,
            targetPoint.yCoord + 1,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint9) && this.isSafe(pathpoint3) && this.isSafe(pathpoint4)) {
            pathOptions[i++] = pathpoint9;
        }

        PathPoint pathpoint10 = openPointFlying(
            entity,
            targetPoint.xCoord,
            targetPoint.yCoord - 1,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint10) && this.isSafe(pathpoint) && this.isSafe(pathpoint5)) {
            pathOptions[i++] = pathpoint10;
        }

        PathPoint pathpoint11 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord - 1,
            targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint11) && this.isSafe(pathpoint1) && this.isSafe(pathpoint5)) {
            pathOptions[i++] = pathpoint11;
        }

        PathPoint pathpoint12 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord - 1,
            targetPoint.zCoord);
        if (this.hasNotVisited(pathpoint12) && this.isSafe(pathpoint2) && this.isSafe(pathpoint5)) {
            pathOptions[i++] = pathpoint12;
        }

        PathPoint pathpoint13 = openPointFlying(
            entity,
            targetPoint.xCoord,
            targetPoint.yCoord - 1,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint13) && this.isSafe(pathpoint3) && this.isSafe(pathpoint5)) {
            pathOptions[i++] = pathpoint13;
        }

        PathPoint pathpoint14 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint14) && this.isSafe(pathpoint3) && this.isSafe(pathpoint2)) {
            pathOptions[i++] = pathpoint14;
        }

        PathPoint pathpoint15 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint15) && this.isSafe(pathpoint) && this.isSafe(pathpoint2)) {
            pathOptions[i++] = pathpoint15;
        }

        PathPoint pathpoint16 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint16) && this.isSafe(pathpoint3) && this.isSafe(pathpoint1)) {
            pathOptions[i++] = pathpoint16;
        }

        PathPoint pathpoint17 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint17) && this.isSafe(pathpoint) && this.isSafe(pathpoint1)) {
            pathOptions[i++] = pathpoint17;
        }

        PathPoint pathpoint18 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord + 1,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint18) && this.isSafe(pathpoint14)
            && this.isSafe(pathpoint9)
            && this.isSafe(pathpoint8)) {
            pathOptions[i++] = pathpoint18;
        }

        PathPoint pathpoint19 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord + 1,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint19) && this.isSafe(pathpoint15)
            && this.isSafe(pathpoint6)
            && this.isSafe(pathpoint8)) {
            pathOptions[i++] = pathpoint19;
        }

        PathPoint pathpoint20 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord + 1,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint20) && this.isSafe(pathpoint16)
            && this.isSafe(pathpoint9)
            && this.isSafe(pathpoint7)) {
            pathOptions[i++] = pathpoint20;
        }

        PathPoint pathpoint21 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord + 1,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint21) && this.isSafe(pathpoint17)
            && this.isSafe(pathpoint6)
            && this.isSafe(pathpoint7)) {
            pathOptions[i++] = pathpoint21;
        }

        PathPoint pathpoint22 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord - 1,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint22) && this.isSafe(pathpoint14)
            && this.isSafe(pathpoint13)
            && this.isSafe(pathpoint12)) {
            pathOptions[i++] = pathpoint22;
        }

        PathPoint pathpoint23 = openPointFlying(
            entity,
            targetPoint.xCoord + 1,
            targetPoint.yCoord - 1,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint23) && this.isSafe(pathpoint15)
            && this.isSafe(pathpoint10)
            && this.isSafe(pathpoint12)) {
            pathOptions[i++] = pathpoint23;
        }

        PathPoint pathpoint24 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord - 1,
            targetPoint.zCoord - 1);
        if (this.hasNotVisited(pathpoint24) && this.isSafe(pathpoint16)
            && this.isSafe(pathpoint13)
            && this.isSafe(pathpoint11)) {
            pathOptions[i++] = pathpoint24;
        }

        PathPoint pathpoint25 = openPointFlying(
            entity,
            targetPoint.xCoord - 1,
            targetPoint.yCoord - 1,
            targetPoint.zCoord + 1);
        if (this.hasNotVisited(pathpoint25) && this.isSafe(pathpoint17)
            && this.isSafe(pathpoint10)
            && this.isSafe(pathpoint11)) {
            pathOptions[i++] = pathpoint25;
        }

        return i;
    }

    private boolean isSafe(PathPoint p_227476_1_) {
        return p_227476_1_ != null && costMalusMap.getOrDefault(p_227476_1_, 0.0F) >= 0.0F;
    }

    private boolean hasNotVisited(PathPoint p_227477_1_) {
        return p_227477_1_ != null && !p_227477_1_.isFirst;
    }

    protected final PathPoint openPointFlying(Entity entity, int x, int y, int z) {
        if (!(entity instanceof EntityCreature)) return super.openPoint(x, y, z);

        PathPoint pathpoint = null;
        float f = getPathNodeType((EntityCreature) entity, x, y, z);
        if (f >= 0.0F) {
            pathpoint = super.openPoint(x, y, z);
            if (worldMap.getBlock(x, y, z)
                .isSideSolid(worldMap, x, y, z, ForgeDirection.UP)) {
                ++f;
            }
            costMalusMap.put(pathpoint, f);
        }

        return pathpoint;
    }

    public static float checkNeighborBlocks(EntityCreature entity, int x, int y, int z, float weight) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    if (i != 0 || k != 0) {
                        weight = Math.min(entity.getBlockPathWeight(x + i, y + j, z + k), weight);
                    }
                }
            }
        }

        return weight;
    }

    public float getPathNodeType(EntityCreature entity, int x, int y, int z) {
        float pathweight = entity.getBlockPathWeight(x, y, z);
        if (pathweight >= 0.0F && y >= 1) {
            pathweight = Math.min(entity.getBlockPathWeight(x, y - 1, z), pathweight);
        }

        if (pathweight >= 0.0F) {
            pathweight = checkNeighborBlocks(entity, x, y, z, pathweight);
        }

        return pathweight;
    }
}
