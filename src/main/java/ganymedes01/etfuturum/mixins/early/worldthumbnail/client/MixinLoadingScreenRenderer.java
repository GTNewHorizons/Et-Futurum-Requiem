package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.client.GuiLoadingBridge;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenStateTracker;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingScreenRenderer.class)
public class MixinLoadingScreenRenderer {

    @Shadow
    private Minecraft mc;

    @Shadow
    private String currentlyDisplayedText;

    @Shadow
    private String field_73727_a;

    @Inject(method = "setLoadingProgress", at = @At("HEAD"))
    private void etfu$captureProgress(int progress, CallbackInfo ci, @Share("progress") LocalIntRef ref) {
        ref.set(progress);
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/client/FMLClientHandler;handleLoadingScreen(Lnet/minecraft/client/gui/ScaledResolution;)Z",
                    remap = false
            )
    )
    private boolean etfu$modernLoadingScreen(FMLClientHandler handler, ScaledResolution resolution,
                                             Operation<Boolean> original, @Share("progress") LocalIntRef progressRef) {
        if (original.call(handler, resolution)) {
            return true;
        }

        if (!LoadingScreenStateTracker.isActive()) {
            return false;
        }

        LoadingScreenStateTracker.updateTitle(I18n.format("multiplayer.downloadingTerrain"));
        if (field_73727_a != null && !field_73727_a.isEmpty()) {
            LoadingScreenStateTracker.updateSubtitle(field_73727_a);
        } else if (currentlyDisplayedText != null && !currentlyDisplayedText.isEmpty()) {
            LoadingScreenStateTracker.updateSubtitle(currentlyDisplayedText);
        } else {
            LoadingScreenStateTracker.updateSubtitle("");
        }

        IntegratedServer server = mc.getIntegratedServer();
        int serverPercent = server != null ? server.percentDone : 0;
        float progress = 0.0F;

        if (serverPercent > 0) {
            progress = serverPercent / 100.0F;
        } else if (progressRef.get() > 0) {
            progress = progressRef.get() / 100.0F;
        }

        progress = Math.max(progress, SpawnChunkProgress.getProgress());
        LoadingScreenStateTracker.updateProgress(progress);

        if (!(mc.currentScreen instanceof GuiLoadingBridge)) {
            mc.displayGuiScreen(new GuiLoadingBridge());
        }

        if (mc.currentScreen != null) {
            mc.currentScreen.drawScreen(0, 0, 0.0F);
        }

        return true;
    }
}
