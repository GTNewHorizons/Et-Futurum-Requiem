package ganymedes01.etfuturum.client;

import ganymedes01.etfuturum.client.loading.LoadingScreenRenderer;
import ganymedes01.etfuturum.client.loading.LoadingScreenRenderManager;
import ganymedes01.etfuturum.client.loading.LoadingScreenStateTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiLoadingBridge extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        LoadingScreenRenderManager.getRenderer().render(Minecraft.getMinecraft(), width, height, LoadingScreenStateTracker.snapshot());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
