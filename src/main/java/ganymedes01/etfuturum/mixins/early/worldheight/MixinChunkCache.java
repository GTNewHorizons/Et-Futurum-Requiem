package ganymedes01.etfuturum.mixins.early.worldheight;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChunkCache.class)
public class MixinChunkCache {

    @Shadow private World worldObj;

    @ModifyConstant(method = { "getBlock",
                               "getBlockMetadata",
                               "getHeight"}, constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return worldObj.provider.getHeight();
    }
}
