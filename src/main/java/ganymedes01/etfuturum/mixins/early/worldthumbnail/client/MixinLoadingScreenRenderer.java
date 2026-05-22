package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.integrated.IntegratedServer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

@Mixin(LoadingScreenRenderer.class)
public class MixinLoadingScreenRenderer {

    private static final int PROGRESS_BAR_WIDTH = 200;
    private static final int PROGRESS_BAR_HEIGHT = 2;

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

    @Unique
    private boolean etfu$isLoadingWorld() {
        String a = currentlyDisplayedText != null ? currentlyDisplayedText.toLowerCase() : "";
        String b = field_73727_a != null ? field_73727_a.toLowerCase() : "";
        String combined = a + " " + b;
        if (combined.contains("saving") || combined.contains("shutting")) return false;
        if (SpawnChunkProgress.hasData()) return true;
        return combined.contains("loading") || combined.contains("building") || combined.contains("terrain");
    }

    @WrapOperation(
            method = "setLoadingProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/client/FMLClientHandler;handleLoadingScreen(Lnet/minecraft/client/gui/ScaledResolution;)Z"
            )
    )
    private boolean etfu$modernLoadingScreen(FMLClientHandler handler, ScaledResolution sr,
                                              Operation<Boolean> original,
                                              @Share("progress") LocalIntRef progressRef) {
        if (original.call(handler, sr)) return true;
        if (!etfu$isLoadingWorld()) return false;

        int vanillaProgress = progressRef.get();

        int w = sr.getScaledWidth();
        int h = sr.getScaledHeight();
        int centerX = w / 2;
        int centerY = h / 2;

        mc.getTextureManager().bindTexture(Gui.optionsBackground);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(0x404040);
        float f = 32.0F;
        tessellator.addVertexWithUV(0, h, 0, 0, h / f);
        tessellator.addVertexWithUV(w, h, 0, w / f, h / f);
        tessellator.addVertexWithUV(w, 0, 0, w / f, 0);
        tessellator.addVertexWithUV(0, 0, 0, 0, 0);
        tessellator.draw();

        GL11.glEnable(GL11.GL_BLEND);

        boolean hasWorld = mc.theWorld != null;
        boolean hasSpawnData = SpawnChunkProgress.hasData();
        boolean showGrid = hasWorld || hasSpawnData;

        int gridSize;
        if (hasWorld) {
            int renderDist = mc.gameSettings.renderDistanceChunks;
            gridSize = renderDist * 2 + 1;
        } else {
            gridSize = 25;
        }
        int cellSize = Math.max(2, Math.min(4, (Math.min(w, h) - 80) / gridSize));
        int gridPixels = gridSize * cellSize;

        int contentTop;
        if (showGrid) {
            contentTop = centerY - gridPixels / 2 - 30;
        } else {
            contentTop = centerY - 20;
        }

        String title = currentlyDisplayedText;
        if (title == null || title.isEmpty()) title = I18n.format("menu.loadingLevel");
        mc.fontRenderer.drawStringWithShadow(title,
                centerX - mc.fontRenderer.getStringWidth(title) / 2,
                contentTop, 0xFFFFFF);

        float progress;
        if (hasWorld) {
            progress = ChunkLoadingProgress.getRawProgress();
        } else {
            IntegratedServer server = mc.getIntegratedServer();
            int serverPercent = server != null ? server.percentDone : 0;
            if (serverPercent > 0) {
                progress = serverPercent / 100.0F;
            } else if (vanillaProgress > 0) {
                progress = vanillaProgress / 100.0F;
            } else {
                progress = 0.0F;
            }
            progress = Math.max(progress, SpawnChunkProgress.getProgress());
            SpawnChunkProgress.setProgress(progress);
        }

        int barLeft = centerX - PROGRESS_BAR_WIDTH / 2;
        int barTop = contentTop + 15;
        Gui.drawRect(barLeft, barTop, barLeft + PROGRESS_BAR_WIDTH, barTop + PROGRESS_BAR_HEIGHT, 0xFF000000);
        int fillWidth = Math.round(progress * PROGRESS_BAR_WIDTH);
        if (fillWidth > 0) {
            Gui.drawRect(barLeft, barTop, barLeft + fillWidth, barTop + PROGRESS_BAR_HEIGHT, 0xFF00FF00);
        }

        if (showGrid) {
            int gridTop = barTop + 10;
            int gridLeft = centerX - gridPixels / 2;

            Gui.drawRect(gridLeft - 1, gridTop - 1,
                    gridLeft + gridPixels + 1, gridTop + gridPixels + 1,
                    0xFF000000);

            if (hasWorld) {
                int playerCX = ChunkLoadingProgress.getPlayerChunkX();
                int playerCZ = ChunkLoadingProgress.getPlayerChunkZ();
                int renderDist = mc.gameSettings.renderDistanceChunks;
                for (int dz = 0; dz < gridSize; dz++) {
                    for (int dx = 0; dx < gridSize; dx++) {
                        int chunkX = playerCX - renderDist + dx;
                        int chunkZ = playerCZ - renderDist + dz;
                        boolean loaded = ChunkLoadingProgress.isChunkLoaded(chunkX, chunkZ);
                        int color = loaded ? 0xFFFFFFFF : 0xFF545454;
                        int px = gridLeft + dx * cellSize;
                        int py = gridTop + dz * cellSize;
                        Gui.drawRect(px, py, px + cellSize, py + cellSize, color);
                    }
                }
            } else {
                int spawnCX = SpawnChunkProgress.getSpawnChunkX();
                int spawnCZ = SpawnChunkProgress.getSpawnChunkZ();
                int half = 12;
                for (int dz = 0; dz < gridSize; dz++) {
                    for (int dx = 0; dx < gridSize; dx++) {
                        int chunkX = spawnCX - half + dx;
                        int chunkZ = spawnCZ - half + dz;
                        int color = SpawnChunkProgress.getColor(chunkX, chunkZ);
                        int px = gridLeft + dx * cellSize;
                        int py = gridTop + dz * cellSize;
                        Gui.drawRect(px, py, px + cellSize, py + cellSize, color);
                    }
                }
            }
        }

        if (field_73727_a != null && !field_73727_a.isEmpty()) {
            int subtitleY = showGrid ? barTop + 10 + gridPixels + 8 : barTop + 15;
            mc.fontRenderer.drawStringWithShadow(field_73727_a,
                    centerX - mc.fontRenderer.getStringWidth(field_73727_a) / 2,
                    subtitleY, 0xFFFFFF);
        }

        return true;
    }
}
