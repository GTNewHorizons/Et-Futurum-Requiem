package ganymedes01.etfuturum.mixins.early.swimming;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class MixinEntity {
	@Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
	private boolean etfu$useActualSneakInputForEdgeSafety(Entity entity) {
		return entity instanceof IPlayerSwimming
				? ((IPlayerSwimming) entity).etfu$isActuallySneaking() : entity.isSneaking();
	}

	@ModifyConstant(method = "handleWaterMovement", constant = @Constant(doubleValue = -0.4000000059604645D))
	private double etfu$keepSwimmingPlayersInWater(double original) {
		if (this instanceof IPlayerSwimming && ((IPlayerSwimming) this).etfu$isActuallySwimming()) {
			return -0.2500000059604645D;
		}
		return original;
	}
}
