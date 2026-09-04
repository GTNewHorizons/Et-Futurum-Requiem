package ganymedes01.etfuturum.mixins.early.panda;

import ganymedes01.etfuturum.entities.EntityPanda;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTrackerEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityTrackerEntry.class)
public abstract class MixinEntityTrackerEntry {

	@Shadow
	public Entity myEntity;

	@ModifyConstant(method = "sendLocationToAllClients", constant = @Constant(intValue = 4, ordinal = 0))
	private int etfu$adjustPandaMovementThresholdX(int original) {
		return etfu$getMovementThreshold(original);
	}

	@ModifyConstant(method = "sendLocationToAllClients", constant = @Constant(intValue = 4, ordinal = 1))
	private int etfu$adjustPandaMovementThresholdY(int original) {
		// Panda movement and terrain transitions repeatedly cross the 1/32-block vertical grid. Keeping vanilla's
		// 4-unit deadband can leave the last client target up to 3/32 inside the ground, where client-side gravity may
		// pull the panda through the block.
		return myEntity instanceof EntityPanda ? 1 : original;
	}

	@ModifyConstant(method = "sendLocationToAllClients", constant = @Constant(intValue = 4, ordinal = 2))
	private int etfu$adjustPandaMovementThresholdZ(int original) {
		return etfu$getMovementThreshold(original);
	}

	@Unique
	private int etfu$getMovementThreshold(int original) {
		if (myEntity instanceof EntityPanda
				&& ((EntityPanda) myEntity).getVariant() == EntityPanda.Gene.LAZY) {
			return 1;
		}
		return original;
	}
}
