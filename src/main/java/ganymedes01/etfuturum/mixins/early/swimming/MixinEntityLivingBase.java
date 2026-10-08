package ganymedes01.etfuturum.mixins.early.swimming;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
	@Redirect(
			method = "moveEntityWithHeading",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isSneaking()Z"))
	private boolean etfu$useActualSneakInputOnLadders(EntityLivingBase entity) {
		return entity instanceof IPlayerSwimming
				? ((IPlayerSwimming) entity).etfu$isActuallySneaking() : entity.isSneaking();
	}

	@ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = 0.800000011920929D))
	private double etfu$useSwimmingDrag(double original) {
		if (this instanceof IPlayerSwimming && ((IPlayerSwimming) this).etfu$isSwimming()) {
			return 0.9D;
		}
		return original;
	}

	@ModifyConstant(method = "moveEntityWithHeading", constant = @Constant(doubleValue = 0.02D, ordinal = 0))
	private double etfu$removeSwimmingGravity(double original) {
		if (this instanceof IPlayerSwimming) {
			return ((IPlayerSwimming) this).etfu$isSwimming() ? 0.0D : 0.005D;
		}
		return original;
	}
}
