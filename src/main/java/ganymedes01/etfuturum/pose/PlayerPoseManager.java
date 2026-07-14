package ganymedes01.etfuturum.pose;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

import static ganymedes01.etfuturum.pose.IPlayerPose.isPoseClear;

public class PlayerPoseManager {
    static private final List<IPlayerPose> poses = new ArrayList<>();

    static public void register(IPlayerPose pose) {
        poses.add(pose);
        poses.sort((p1, p2) -> Float.compare(p2.getPriority(), p1.getPriority()));
    }

    static public IPlayerPose getPose(EntityPlayer player) {
        if (player.getHealth() <= 0.0F || player.isPlayerSleeping()) {
            return PlayerPose.STANDING;
        }
        IPlayerPose deserdPose = null;
        for (IPlayerPose pose : poses) {
            if (pose.canApply(player)) {
                deserdPose = pose;
                break;
            }
        }
        if (deserdPose == null) deserdPose = PlayerPose.STANDING;
        if (player.noClip && !(player instanceof EntityOtherPlayerMP) || player.isRiding() || isPoseClear(player, deserdPose)) {
            return deserdPose;
        }
        for (int i = poses.size() - 1; i >= 0; i--) {
            IPlayerPose pose = poses.get(i);
            if (pose.canFallback(player)) {
                return pose;
            }
        }
        var playerPose = ((IPoseablePlayer) player).etfu$getPose();
        if (playerPose.isLowProfile() && isPoseClear(player, playerPose)) {
            return playerPose;
        }
        return PlayerPose.STANDING;
    }

    static {
        PlayerPoseManager.register(PlayerPose.STANDING);
        PlayerPoseManager.register(PlayerPose.CROUCHING);
        PlayerPoseManager.register(PlayerPose.CRAWLING);
        PlayerPoseManager.register(PlayerPose.SWIMMING);
        PlayerPoseManager.register(PlayerPose.FALL_FLYING);
    }
}
