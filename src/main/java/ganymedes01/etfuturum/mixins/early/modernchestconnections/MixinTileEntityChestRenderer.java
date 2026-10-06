package ganymedes01.etfuturum.mixins.early.modernchestconnections;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityChestRenderer.class)
public class MixinTileEntityChestRenderer {
    @ModifyExpressionValue(method = "renderTileEntityAt(Lnet/minecraft/tileentity/TileEntityChest;DDDF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityChest;getBlockMetadata()I"))
    private int onlyUseVanillaBitsForRendering(int original) {
        return original & 0b111;
    }
}
