package ganymedes01.etfuturum.mixins.early.worldheight;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow public WorldProvider provider;

    @ModifyConstant(method = { "setBlock",
                               "setBlockMetadataWithNotify",
                               "getBlock",
                               "getBlockMetadata",
                               "getTileEntity",
                               "checkChunksExist",
                               "blockExists",
                               "setLightValue",
                               "getSkyBlockTypeBrightness",
                               "getSavedLightValue",
                               "getFullBlockLightValue",
                               "getBlockLightValue_do"}, constant = @Constant(intValue = 256))
    private int getIncreasedWorldHeight(int original) {

        return this.provider.getHeight();
    }

    @ModifyConstant(method = { "getBlockLightOpacity",
                               "canBlockFreezeBody",
                               "canSnowAtBody" }, constant = @Constant(intValue = 256), remap = false)
    private int getIncreasedWorldHeightNoRemap(int original) {

        return this.provider.getHeight();
    }

    @ModifyConstant(method = { "getSavedLightValue",
                               "getFullBlockLightValue",
                               "getBlockLightValue_do" }, constant = @Constant(intValue = 255))
    private int getIncreasedWorldHeightMinusOne(int original) {

        return this.provider.getHeight() - 1;
    }
}
