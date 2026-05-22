package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.ChunkLoadingProgress;
import ganymedes01.etfuturum.client.SpawnChunkProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiDownloadTerrain.class)
public abstract class MixinGuiDownloadTerrain extends GuiScreen {

    private static final int PROGRESS_BAR_WIDTH = 200;
    private static final int PROGRESS_BAR_HEIGHT = 2;

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void etfu$modernLoadingScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();

        Minecraft mc = Minecraft.getMinecraft();
        int w = this.width;
        int h = this.height;
        int centerX = w / 2;
        int centerY = h / 2;

        this.drawBackground(0);

        boolean hasWorld = mc.theWorld != null;
        float clientProgress = hasWorld ? ChunkLoadingProgress.getRawProgress() : 0.0F;
        boolean hasSpawnData = SpawnChunkProgress.hasData();

        int gridSize = 25;
        int cellSize = Math.max(2, Math.min(4, (Math.min(w, h) - 80) / gridSize));
        int gridPixels = gridSize * cellSize;
        boolean showGrid = hasSpawnData || (hasWorld && clientProgress > 0);

        int contentTop;
        if (showGrid) {
            contentTop = centerY - gridPixels / 2 - 30;
        } else {
            contentTop = centerY - 20;
        }

        String title = I18n.format("menu.loadingLevel");
        mc.fontRenderer.drawStringWithShadow(title,
                centerX - mc.fontRenderer.getStringWidth(title) / 2,
                contentTop, 0xFFFFFF);

        float progress = Math.max(SpawnChunkProgress.getProgress(), clientProgress);
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

            int spawnCX = SpawnChunkProgress.getSpawnChunkX();
            int spawnCZ = SpawnChunkProgress.getSpawnChunkZ();
            int half = 12;

            for (int dz = 0; dz < gridSize; dz++) {
                for (int dx = 0; dx < gridSize; dx++) {
                    int chunkX = spawnCX - half + dx;
                    int chunkZ = spawnCZ - half + dz;
                    int color = SpawnChunkProgress.getColor(chunkX, chunkZ);
                    if (hasWorld && ChunkLoadingProgress.isChunkLoaded(chunkX, chunkZ)) {
                        color = 0xFFFFFFFF;
                    }
                    int px = gridLeft + dx * cellSize;
                    int py = gridTop + dz * cellSize;
                    Gui.drawRect(px, py, px + cellSize, py + cellSize, color);
                }
            }
        }

        String subtitle = I18n.format("multiplayer.downloadingTerrain");
        int subtitleY = showGrid ? barTop + 10 + gridPixels + 8 : barTop + 15;
        mc.fontRenderer.drawStringWithShadow(subtitle,
                centerX - mc.fontRenderer.getStringWidth(subtitle) / 2,
                subtitleY, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
