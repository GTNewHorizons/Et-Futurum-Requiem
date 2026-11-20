package ganymedes01.etfuturum.mixins.early.panda;

import ganymedes01.etfuturum.entities.EntityPanda;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

	@ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.005D, ordinal = 0))
	private double etfu$usePandaMotionThresholdX(double original) {
		return etfu$getHorizontalMotionThreshold(original);
	}

	@ModifyConstant(method = "onLivingUpdate", constant = @Constant(doubleValue = 0.005D, ordinal = 2))
	private double etfu$usePandaMotionThresholdZ(double original) {
		return etfu$getHorizontalMotionThreshold(original);
	}

	@Unique
	private double etfu$getHorizontalMotionThreshold(double original) {
		if ((Object) this instanceof EntityPanda) {
			return 0.003D;
		}
		return original;
	}
}
