package ganymedes01.etfuturum.mixins.early.singlelevel;

import net.minecraft.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Enchantment.class)
public abstract class MixinEnchantment {

    @Shadow
    public abstract int getMaxLevel();

    @Inject(
        method = "getTranslatedName",
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/StatCollector;translateToLocal(Ljava/lang/String;)Ljava/lang/String;",
            shift = At.Shift.AFTER,
            ordinal = 1))
    private void hideLevel(int p_77316_1_, CallbackInfoReturnable<String> cir, String s) {
        if (getMaxLevel() == 1) {
            cir.setReturnValue(s);
        }
    }
}
