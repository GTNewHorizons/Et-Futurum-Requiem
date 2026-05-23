package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenHooks;
import ganymedes01.etfuturum.client.loading.LoadingScreenRenderer;
import ganymedes01.etfuturum.client.loading.LoadingScreenStateTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDownloadTerrain.class)
public abstract class MixinGuiDownloadTerrain extends GuiScreen {

    @Unique
    private static final LoadingScreenRenderer ETFU$RENDERER = new LoadingScreenRenderer();

    @Inject(method = "initGui", at = @At("HEAD"))
    private void etfu$beginDownloadTerrain(CallbackInfo ci) {
        LoadingScreenHooks.beginDownloadTerrain();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void etfu$modernLoadingScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();

        Minecraft mc = Minecraft.getMinecraft();
        float serverProgress = SpawnChunkProgress.getProgress();
        float clientProgress = ChunkLoadingProgress.getRawProgress();
        LoadingScreenStateTracker.updateTitle(I18n.format("multiplayer.downloadingTerrain"));
        LoadingScreenStateTracker.updateSubtitle("");
        LoadingScreenStateTracker.updateProgress(Math.max(serverProgress, clientProgress));

        if (serverProgress <= 0.0F) {
            LoadingScreenHooks.updateClientChunkMap(mc);
        }

        ETFU$RENDERER.render(mc, width, height, LoadingScreenStateTracker.snapshot());
    }
}
