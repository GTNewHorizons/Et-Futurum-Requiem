package ganymedes01.etfuturum.mixins.early.worldheight;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public class MixinWorldProvider {

    @Shadow public boolean hasNoSky;

    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void getIncreasedHeight(CallbackInfoReturnable<Integer> cir) {

        cir.setReturnValue(WorldHeightHandler.getMaxWorldHeight());
    }

    @ModifyConstant(method = "getActualHeight", constant = @Constant(intValue = 256), remap = false)
    private int getIncreasedActualHeight(int original) {

        return WorldHeightHandler.getMaxWorldHeight() > 128 && hasNoSky ? 128 : WorldHeightHandler.getMaxWorldHeight();
    }
}
