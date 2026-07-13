package ganymedes01.etfuturum.pose;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public interface IPlayerPose {
    default float getWidth() {
        return 0.6f;
    }

    default float getHeight() {
        return 1.8f;
    }

    default float getEyeHeight() {
        return 1.65f;
    }

    default boolean isLowProfile() {
        return getEyeHeight() < 1.65f;
    }

    default boolean canApply(EntityPlayer player) {
        return true;
    }

    default boolean canFallback(EntityPlayer player) {
        if (!isPoseClear(player, this)) return false;
        if (isLowProfile()) {
            return ((IPoseablePlayer) player).etfu$getPose() == this;
        }
        return true;
    }

    float getPriority();

    static boolean isPoseClear(EntityPlayer player, IPlayerPose pose) {
        float scale = ((IPoseablePlayer) player).etfu$getScale();
        float halfWidth = 0.6F / 2.0F * scale;
        AxisAlignedBB poseBox = AxisAlignedBB.getBoundingBox(
                player.posX - halfWidth,
                player.boundingBox.minY,
                player.posZ - halfWidth,
                player.posX + halfWidth,
                player.boundingBox.minY + pose.getHeight() * scale,
                player.posZ + halfWidth).contract(1.0E-7D, 1.0E-7D, 1.0E-7D);
        return player.worldObj.getCollidingBoundingBoxes(player, poseBox).isEmpty();
    }
}
