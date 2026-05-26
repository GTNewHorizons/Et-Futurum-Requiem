package ganymedes01.etfuturum.mixins.early.spectator;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import ganymedes01.etfuturum.api.spectator.SpectatorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
	@Shadow
	public boolean noClip;

	@ModifyReturnValue(method = "isEntityInsideOpaqueBlock", at = @At("RETURN"))
	private boolean ignoreBlockIfSpectator(boolean original) {
		if (this.noClip && original) {
			if(SpectatorUtils.isSpectator((Entity) (Object) this)) {
				return false;
			}
		}
		return original;
	}

	@Inject(method = "setAngles", at = @At("HEAD"))
	private void spectatorCameraLock(float yaw, float pitch, CallbackInfo ci,
									 @Local(argsOnly = true, ordinal = 0) LocalFloatRef yawAssignable,
									 @Local(argsOnly = true, ordinal = 1) LocalFloatRef pitchAssignable) {
		if((Entity) (Object) this instanceof EntityPlayer player) {
			if(SpectatorUtils.isSpectator(player)) {
				Entity entity = SpectatorUtils.getSpectatingEntity(player);
				if(entity != null) {
					yawAssignable.set(0);
					pitchAssignable.set(0);
				}
			}
		}
	}

	@ModifyReturnValue(method = "canAttackWithItem", at = @At(value = "RETURN"))
	public boolean canAttackWithItem(boolean original)
	{
		if(original) {
			return !SpectatorUtils.isSpectator((Entity) (Object) this);
		}
		return false;
	}

	@ModifyReturnValue(method = "handleWaterMovement", at = @At(value = "RETURN"))
	public boolean handleWaterMovement(boolean original) {
		if(original) {
			return !SpectatorUtils.isSpectator((Entity) (Object) this);
		}
		return false;
	}
}
