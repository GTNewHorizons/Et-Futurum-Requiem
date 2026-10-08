package ganymedes01.etfuturum.mixins.early.swimming;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class MixinEntity {
	@Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSneaking()Z"))
	private boolean etfu$useActualSneakInputForEdgeSafety(Entity entity) {
		return entity instanceof IPlayerSwimming
				? ((IPlayerSwimming) entity).etfu$isActuallySneaking() : entity.isSneaking();
	}
}
