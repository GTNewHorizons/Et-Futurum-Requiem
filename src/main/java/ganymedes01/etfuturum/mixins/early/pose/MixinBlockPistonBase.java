package ganymedes01.etfuturum.mixins.early.pose;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.pose.IPoseablePlayer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockPistonBase.class)
public class MixinBlockPistonBase {
    @ModifyExpressionValue(method = "determineOrientation", at = @At(value = "CONSTANT", args = "doubleValue=1.82D"))
    private static double applyOffset(double origin, @Local(ordinal = 0, argsOnly = true) EntityLivingBase entity) {
        if (entity instanceof IPoseablePlayer p)
        {
            return p.etfu$getPose().getEyeHeight() * p.etfu$getScale() + 0.2D;
        }
        return origin;
    }
}
