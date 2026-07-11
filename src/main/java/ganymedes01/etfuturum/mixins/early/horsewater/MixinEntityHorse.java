package ganymedes01.etfuturum.mixins.early.horsewater;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHorse.class)
public abstract class MixinEntityHorse extends EntityAnimal {

	protected MixinEntityHorse(World world) {
		super(world);
	}

	@Inject(method = "onLivingUpdate", at = @At("TAIL"))
	private void floatWithRider(CallbackInfo ci) {
		if (this.riddenByEntity == null
				|| !this.worldObj.isAnyLiquid(this.boundingBox.contract(0.001D, 0.001D, 0.001D))) {
			return;
		}

		this.motionY += 0.03D;
	}
}
