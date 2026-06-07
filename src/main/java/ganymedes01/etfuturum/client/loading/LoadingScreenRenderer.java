package ganymedes01.etfuturum.client.loading;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class LoadingScreenRenderer {

    private static final int PROGRESS_BAR_WIDTH = 200;
    private static final int PROGRESS_BAR_HEIGHT = 2;

    private final LoadingScreenBackgroundRenderer backgroundRenderer = new LoadingScreenBackgroundRenderer();
    private final LoadingScreenChunkMapRenderer chunkMapRenderer = new LoadingScreenChunkMapRenderer();
    private float smoothedProgress;

    public void resetProgress() {
        smoothedProgress = 0.0F;
    }

    public void render(Minecraft mc, int width, int height, LoadingScreenSnapshot snapshot) {
        backgroundRenderer.render(mc, width, height);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        int centerX = width / 2;
        int centerY = height / 2;
        int textTop = centerY - 50;

        if (snapshot.hasChunkSnapshot()) {
            chunkMapRenderer.render(centerX, centerY, snapshot.getChunkSnapshot());
            textTop = chunkMapRenderer.getTextTop(centerY, snapshot.getChunkSnapshot());
        }

        String title = snapshot.getTitle();
        if (title == null || title.isEmpty()) {
            title = LoadingScreenText.getLoadingWorldTitle();
        }

        smoothedProgress += (snapshot.getProgress() - smoothedProgress) * 0.2F;
        smoothedProgress = Math.max(0.0F, Math.min(1.0F, smoothedProgress));

        mc.fontRenderer.drawStringWithShadow(title, centerX - mc.fontRenderer.getStringWidth(title) / 2, textTop, 0xFFFFFF);

        int barLeft = centerX - PROGRESS_BAR_WIDTH / 2;
        int barTop = textTop + 12;
        Gui.drawRect(barLeft, barTop, barLeft + PROGRESS_BAR_WIDTH, barTop + PROGRESS_BAR_HEIGHT, 0xFF000000);
        Gui.drawRect(barLeft, barTop, barLeft + Math.round(smoothedProgress * PROGRESS_BAR_WIDTH),
                barTop + PROGRESS_BAR_HEIGHT, 0xFF00FF00);

        String subtitle = snapshot.getSubtitle();
        if (subtitle != null && !subtitle.isEmpty()) {
            int subtitleY = snapshot.hasChunkSnapshot() ? centerY + snapshot.getChunkSnapshot().getRadius() * 2 + 12 : barTop + 12;
            mc.fontRenderer.drawStringWithShadow(subtitle, centerX - mc.fontRenderer.getStringWidth(subtitle) / 2, subtitleY,
                    0xFFFFFF);
        }
    }
}
