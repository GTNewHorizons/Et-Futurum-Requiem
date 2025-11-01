package ganymedes01.etfuturum.mixins.early.sounds;

import ganymedes01.etfuturum.Tags;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntitySquid.class)
public class MixinEntitySquid extends EntityWaterMob {

	public MixinEntitySquid(World p_i1695_1_) {
		super(p_i1695_1_);
	}

	@Inject(method = "getHurtSound", at = @At(value = "HEAD"), cancellable = true)
	protected void getNewHurtSound(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(Tags.MC_ASSET_VER + ":entity.squid.hurt");
	}

	@Inject(method = "getDeathSound", at = @At(value = "HEAD"), cancellable = true)
	protected void getNewDeathSound(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(Tags.MC_ASSET_VER + ":entity.squid.death");
	}

	@Inject(method = "getLivingSound", at = @At(value = "HEAD"), cancellable = true)
	protected void getNewLivingSound(CallbackInfoReturnable<String> cir) {
		cir.setReturnValue(Tags.MC_ASSET_VER + ":entity.squid.ambient");
	}

}
