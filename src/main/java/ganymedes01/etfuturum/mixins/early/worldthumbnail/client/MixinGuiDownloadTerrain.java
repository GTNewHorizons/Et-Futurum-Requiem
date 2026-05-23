package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import ganymedes01.etfuturum.client.loading.LoadingScreenHooks;
import ganymedes01.etfuturum.client.loading.LoadingScreenRenderManager;
import ganymedes01.etfuturum.client.loading.LoadingScreenStateTracker;
import ganymedes01.etfuturum.client.loading.LoadingScreenText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDownloadTerrain.class)
public abstract class MixinGuiDownloadTerrain extends GuiScreen {

    @Unique
    private static final long ETFU$PREPARING_TERRAIN_DELAY_MS = 1500L;

    @Unique
    private long etfu$openedAt;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void etfu$beginDownloadTerrain(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        etfu$openedAt = Minecraft.getSystemTime();
        LoadingScreenHooks.beginDownloadTerrain(mc.getIntegratedServer() != null);
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void etfu$modernLoadingScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();

        Minecraft mc = Minecraft.getMinecraft();
        float serverProgress = SpawnChunkProgress.getProgress();
        float clientProgress = ChunkLoadingProgress.getRawProgress();
        float progress = Math.max(serverProgress, clientProgress);

        if (mc.getIntegratedServer() == null) {
            LoadingScreenStateTracker.updateTitle(LoadingScreenText.getDownloadingTerrainTitle());
            LoadingScreenStateTracker.updateSubtitle("");
        } else {
            LoadingScreenStateTracker.updateTitle(LoadingScreenText.getLoadingWorldTitle());
            if (Minecraft.getSystemTime() - etfu$openedAt >= ETFU$PREPARING_TERRAIN_DELAY_MS
                    && (serverProgress > 0.0F || clientProgress > 0.0F)) {
                LoadingScreenStateTracker.updateSubtitle(LoadingScreenText.getPreparingTerrainSubtitle());
            }
        }

        LoadingScreenStateTracker.updateProgress(progress);

        if (serverProgress <= 0.0F) {
            LoadingScreenHooks.updateClientChunkMap(mc);
        }

        LoadingScreenRenderManager.getRenderer().render(mc, width, height, LoadingScreenStateTracker.snapshot());
    }
}
