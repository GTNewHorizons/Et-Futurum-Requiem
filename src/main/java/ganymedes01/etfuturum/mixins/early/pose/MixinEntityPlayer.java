package ganymedes01.etfuturum.mixins.early.pose;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ganymedes01.etfuturum.pose.IPlayerPose;
import ganymedes01.etfuturum.pose.IPoseablePlayer;
import ganymedes01.etfuturum.pose.PlayerPose;
import ganymedes01.etfuturum.pose.PlayerPoseManager;
import ganymedes01.etfuturum.pose.PlayerScaleEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements IPoseablePlayer {
    @Unique
    private IPlayerPose etfu$pose = PlayerPose.STANDING;

    @Unique
    protected float etfu$scale = 1.0f;

    protected MixinEntityPlayer(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void etfu$updateg(CallbackInfo ci) {
        this.etfu$updateScale();
        this.etfu$updatePose();
    }

    @Unique
    private void etfu$updateScale() {
        PlayerScaleEvent event = new PlayerScaleEvent((EntityPlayer) (Object) this, 1.0f);
        MinecraftForge.EVENT_BUS.post(event);
        etfu$scale = event.scale;
    }

    @Override
    public float etfu$getScale() {
        return etfu$scale;
    }

    @Unique
    private void etfu$updatePose() {
        IPlayerPose desiredPose = PlayerPoseManager.getPose((EntityPlayer) (Object) this);
        this.etfu$setPose(desiredPose);
        this.etfu$applyPoseSize(desiredPose);
    }

    @Unique
    private void etfu$applyPoseSize(IPlayerPose pose) {
        float width = pose.getWidth() * etfu$getScale();
        float height = pose.getHeight() * etfu$getScale();
        if (Math.abs(this.width - width) > 0.001F || Math.abs(this.height - height) > 0.001F) {
            this.setSize(width, height);
        }
    }

    @ModifyReturnValue(method = "getEyeHeight", at = @At("RETURN"))
    private float etfu$getPoseEyeHeight(float origin) {
        if (this.worldObj.isRemote) {
            return origin * etfu$getScale();
        }
        return this.etfu$pose.getEyeHeight() * etfu$getScale();
    }

    @ModifyReturnValue(method = "getDefaultEyeHeight", at = @At("RETURN"), remap = false)
    private float etfu$getDefaultEyeHeight(float origin) {
        if (this.worldObj.isRemote) {
            return origin * etfu$getScale();
        }
        return this.etfu$pose.getEyeHeight() * etfu$getScale();
    }

    @Override
    public boolean isSneaking() {
        return super.isSneaking() || etfu$getPose() == PlayerPose.CROUCHING;
    }

    @Unique
    float etfu$CurrentYOffset = 0f;

    @Override
    public float etfu$getCurrentYOffset() {
        return etfu$CurrentYOffset;
    }

    @Override
    public void etfu$setCurrentYOffset(float offset) {
        etfu$CurrentYOffset = offset;
    }

    @Override
    public IPlayerPose etfu$getPose() {
        return this.etfu$pose;
    }

    @Override
    public void etfu$setPose(IPlayerPose pose) {
        this.etfu$pose = pose;
    }
}
