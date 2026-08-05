package ganymedes01.etfuturum.mixins.early.pose.client;

import ganymedes01.etfuturum.pose.IPoseablePlayer;
import ganymedes01.etfuturum.pose.PlayerPose;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityClientPlayerMP.class)
public abstract class MixinEntityClientPlayerMP {
    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void updateYOffset(CallbackInfo ci) {
        IPoseablePlayer p = (IPoseablePlayer) this;
        EntityClientPlayerMP player = ((EntityClientPlayerMP) (Object) this);
        float actualEyeHeight = p.etfu$getPose().getEyeHeight() * p.etfu$getScale();
        float standingEyeHeight = PlayerPose.STANDING.getEyeHeight();
        float targetYOffset = standingEyeHeight - actualEyeHeight;
        targetYOffset = Math.min(targetYOffset, standingEyeHeight);
        if (Math.abs(player.yOffset - actualEyeHeight) <= 0.001F) {
            p.etfu$setCurrentYOffset(targetYOffset);
            return;
        }
        float currentYOffset = p.etfu$getCurrentYOffset();
        if (Math.abs(targetYOffset - currentYOffset) <= 0.001F) {
            player.yOffset = standingEyeHeight - targetYOffset;
            return;
        }
        currentYOffset += (targetYOffset - currentYOffset) * 0.5F;
        p.etfu$setCurrentYOffset(currentYOffset);
        player.yOffset = standingEyeHeight - currentYOffset;
    }
}
