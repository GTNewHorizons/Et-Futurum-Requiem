package ganymedes01.etfuturum.pose;

import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerPose {
    static public class StandingPose implements IPlayerPose {
        @Override
        public float getPriority() {
            return 0;
        }
    }

    ;

    static public class CrouchingPose implements IPlayerPose {
        @Override
        public float getPriority() {
            return 1;
        }

        @Override
        public float getHeight() {
            return 1.5F;
        }

        @Override
        public float getEyeHeight() {
            return 1.27F;
        }

        @Override
        public boolean canApply(EntityPlayer player) {
            return ConfigMixins.enableModernSneaking && ((IPlayerSwimming) player).etfu$isActuallySneaking() && !player.capabilities.isFlying && !player.isOnLadder();
        }

        @Override
        public boolean canFallback(EntityPlayer player) {
            return IPlayerPose.isPoseClear(player, this);
        }
    }

    ;

    static public class CrawlingPose implements IPlayerPose {
        @Override
        public float getPriority() {
            return 2;
        }

        @Override
        public float getHeight() {
            return 0.6F;
        }

        @Override
        public float getEyeHeight() {
            return 0.4F;
        }

        @Override
        public boolean canApply(EntityPlayer player) {
            return false;
        }

        @Override
        public boolean canFallback(EntityPlayer player) {
            return IPlayerPose.isPoseClear(player, this);
        }
    }

    ;

    static public class SwimmingPose implements IPlayerPose {
        @Override
        public float getPriority() {
            return 3;
        }

        @Override
        public float getHeight() {
            return 0.6F;
        }

        @Override
        public float getEyeHeight() {
            return 0.4F;
        }

        @Override
        public boolean canApply(EntityPlayer player) {
            return ((IPlayerSwimming) player).etfu$isSwimming();
        }
    }

    ;

    static public class FallFlyingPose implements IPlayerPose {
        @Override
        public float getPriority() {
            return 3;
        }

        @Override
        public float getHeight() {
            return 0.6F;
        }

        @Override
        public float getEyeHeight() {
            return 0.4F;
        }

        @Override
        public boolean canApply(EntityPlayer player) {
            return ConfigMixins.enableElytra && player instanceof IElytraPlayer && ((IElytraPlayer) player).etfu$isElytraFlying();
        }
    }

    ;
    static public final IPlayerPose STANDING = new StandingPose();
    static public final IPlayerPose CROUCHING = new CrouchingPose();
    static public final IPlayerPose CRAWLING = new CrawlingPose();
    static public final IPlayerPose SWIMMING = new SwimmingPose();
    static public final IPlayerPose FALL_FLYING = new FallFlyingPose();
}
