package ganymedes01.etfuturum.mixins.early.pose.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.client.entity.EntityPlayerSP;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {
    @ModifyExpressionValue(method = "onLivingUpdate", at = @At(value = "FIELD", target = "Lnet/minecraft/util/MovementInput;sneak:Z", opcode = Opcodes.GETFIELD, ordinal = 0))
    boolean update(boolean original)
    {
        if (ConfigMixins.enableModernSneaking) return false;
        return original;
    }
}