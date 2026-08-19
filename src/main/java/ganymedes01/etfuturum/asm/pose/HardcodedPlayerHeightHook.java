package ganymedes01.etfuturum.asm.pose;

import ganymedes01.etfuturum.pose.IPlayerPose;
import ganymedes01.etfuturum.pose.IPoseablePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class HardcodedPlayerHeightHook {
    public static float adjustHeight(Entity player, float yOffset)
    {
        if (player instanceof IPoseablePlayer p)
        {
            float actualEyeHeight = p.etfu$getPose().getEyeHeight() * p.etfu$getScale();
            // origin: 1.62f - yOffset
            // now: actualEyeHeight - yOffset;
            return 1.62f - actualEyeHeight + yOffset;
        }
        return yOffset;
    }
}
