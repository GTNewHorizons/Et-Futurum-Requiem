package ganymedes01.etfuturum.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiLoadingBridge extends GuiScreen {

    private static final int PROGRESS_BAR_WIDTH = 200;
    private static final int PROGRESS_BAR_HEIGHT = 2;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int gridSize = 25;
        int cellSize = Math.max(2, Math.min(4, (Math.min(this.width, this.height) - 80) / gridSize));
        int gridPixels = gridSize * cellSize;
        boolean showGrid = SpawnChunkProgress.hasData();

        int contentTop = showGrid ? centerY - gridPixels / 2 - 30 : centerY - 20;

        String title = I18n.format("menu.loadingLevel");
        this.fontRendererObj.drawStringWithShadow(title,
                centerX - this.fontRendererObj.getStringWidth(title) / 2,
                contentTop, 0xFFFFFF);

        float progress = SpawnChunkProgress.getProgress();
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
                    gridLeft + gridPixels + 1, gridTop + gridPixels + 1, 0xFF000000);

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

    @Override
    public void drawWorldBackground(int tint) {
        drawBackground(tint);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
