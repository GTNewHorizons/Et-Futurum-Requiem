package ganymedes01.etfuturum.mixins.early.sounds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.Tags;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySkeleton.class)
public class MixinEntitySkeleton extends EntityMob {

	public MixinEntitySkeleton(World p_i1738_1_) {
		super(p_i1738_1_);
	}

	@Inject(method = "getLivingSound", at = @At(value = "HEAD"), cancellable = true)
	protected void getNewLivingSound(CallbackInfoReturnable<String> cir) {
		if (getSkeletonType() == 1) {
			cir.setReturnValue(Tags.MC_ASSET_VER + ":entity.wither_skeleton.ambient");
		}
	}

	@Inject(method = "getHurtSound", at = @At(value = "HEAD"), cancellable = true)
	protected void getNewHurtSound(CallbackInfoReturnable<String> cir) {
		if (getSkeletonType() == 1) {
			cir.setReturnValue(Tags.MC_ASSET_VER + ":entity.wither_skeleton.hurt");
		}
	}

	@Inject(method = "getDeathSound", at = @At(value = "HEAD"), cancellable = true)
	protected void getNewDeathSound(CallbackInfoReturnable<String> cir) {
		if (getSkeletonType() == 1) {
			cir.setReturnValue(Tags.MC_ASSET_VER + ":entity.wither_skeleton.death");
		}
	}

	@WrapOperation(method = "func_145780_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntitySkeleton;playSound(Ljava/lang/String;FF)V"))
	protected void playStepSound_inject(EntitySkeleton instance, String s, float volume, float pitch, Operation<Void> original) {
		original.call(instance, getSkeletonType() == 1 ? Tags.MC_ASSET_VER + ":entity.wither_skeleton.step" : s, volume, pitch);
	}

	@Shadow
	public int getSkeletonType() {
		return 0;
	}

}
