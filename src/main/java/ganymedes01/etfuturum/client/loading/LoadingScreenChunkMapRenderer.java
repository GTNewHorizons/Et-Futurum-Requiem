package ganymedes01.etfuturum.client.loading;

import net.minecraft.client.gui.Gui;

public class LoadingScreenChunkMapRenderer {

    private static final int CELL_SIZE = 2;
    private static final int CELL_MARGIN = 0;
    private static final int CELL_SPAN = CELL_SIZE + CELL_MARGIN;

    public int render(int centerX, int centerY, LoadingScreenChunkSnapshot snapshot) {
        int diameter = snapshot.getDiameter();
        int totalWidth = diameter * CELL_SPAN - CELL_MARGIN;
        int left = centerX - totalWidth / 2;
        int top = centerY - totalWidth / 2;

        for (int z = 0; z < diameter; z++) {
            for (int x = 0; x < diameter; x++) {
                int color = snapshot.getColor(x, z);
                int cellLeft = left + x * CELL_SPAN;
                int cellTop = top + z * CELL_SPAN;
                Gui.drawRect(cellLeft, cellTop, cellLeft + CELL_SIZE, cellTop + CELL_SIZE, color);
            }
        }

        return totalWidth;
    }

    public int getTextTop(int centerY, LoadingScreenChunkSnapshot snapshot) {
        return centerY - snapshot.getRadius() * CELL_SPAN - 27;
    }
}
