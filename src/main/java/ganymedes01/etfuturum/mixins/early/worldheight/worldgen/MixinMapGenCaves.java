package ganymedes01.etfuturum.mixins.early.worldheight.worldgen;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.world.gen.MapGenCaves;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MapGenCaves.class)
public class MixinMapGenCaves {

    @ModifyConstant(method = "func_151538_a", constant = @Constant(intValue = 120))
    private int higherCaveStartPoint(int original) {

        return original + WorldHeightHandler.getWorldHeightOffset();
    }
}
