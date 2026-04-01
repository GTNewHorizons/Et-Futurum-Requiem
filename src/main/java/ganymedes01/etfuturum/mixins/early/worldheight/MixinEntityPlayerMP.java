package ganymedes01.etfuturum.mixins.early.worldheight;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {

    @ModifyConstant(method = "onUpdate", constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return ((Entity)(Object)this).worldObj.provider.getHeight();
    }
}
