package ganymedes01.etfuturum.mixins.early.pose;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import ganymedes01.etfuturum.pose.IPoseablePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemBoat.class)
public class MixinItemBoat {
    @ModifyExpressionValue(method = "onItemRightClick", at = @At(value = "CONSTANT", args = "doubleValue=1.62D"))
    private static double applyOffset(double origin, @Local(ordinal = 0, argsOnly = true) EntityPlayer player) {
        if (player instanceof IPoseablePlayer p)
        {
            return p.etfu$getPose().getEyeHeight() * p.etfu$getScale();
        }
        return origin;
    }
}
