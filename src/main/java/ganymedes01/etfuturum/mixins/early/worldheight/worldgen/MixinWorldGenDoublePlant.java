package ganymedes01.etfuturum.mixins.early.worldheight.worldgen;

import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Random;

@Mixin(WorldGenDoublePlant.class)
public class MixinWorldGenDoublePlant {

    @ModifyConstant(method = "generate", constant = @Constant(intValue = 254))
    private int getIncreasedWorldHeightMinusTwo(int original, World world, Random random, int x, int y, int z) {

        return world.provider.getHeight() - 2;
    }
}
