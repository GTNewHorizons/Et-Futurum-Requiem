package ganymedes01.etfuturum.mixins.early.worldheight.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Random;

@Mixin(WorldGenTaiga2.class)
public class MixinWorldGenTaiga2 {

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original, World world, Random random, int x, int y, int z) {

        return world.provider.getHeight();
    }
}
