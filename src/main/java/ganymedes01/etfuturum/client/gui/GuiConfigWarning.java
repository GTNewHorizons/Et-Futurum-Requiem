package ganymedes01.etfuturum.client.gui;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.config.GuiCheckBox;

/**
 * Adapted from Biomes o' Plenty by Glitchfiend
 * https://github.com/Glitchfiend/BiomesOPlenty/blob/BOP-1.7.10%2B2-2.0.x/src/main/java/biomesoplenty/client/gui/StartupWarningGUI.java
 */
public class GuiConfigWarning extends GuiScreen {

    private final GuiScreen parentGuiScreen;
    private final Configuration config;
    private boolean never;
    private int yLevel;

    public GuiConfigWarning(GuiScreen parentGuiScreen, Configuration config) {
        this.parentGuiScreen = parentGuiScreen;
        this.config = config;
    }

    @Override
    public void initGui() {

        // --- Buttons --- //

        buttonList.clear();

        buttonList.add(
            new GuiButton(
                0,
                width / 2 + 1,
                height / 4 + 120 + 12,
                128,
                20,
                "\u00a7c" + I18n.format("gui.warn.continue")));

        buttonList.add(new GuiButton(1, width / 2 - 129, height / 4 + 120 + 12, 128, 20, I18n.format("menu.quit")));

        String check = I18n.format("gui.warn.dismiss");
        buttonList.add(
            new GuiCheckBox(
                2,
                width / 2 - ((fontRendererObj.getStringWidth(check) + 13) / 2),
                height / 4 + 108,
                "\u00a7o" + check,
                never));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen(this.parentGuiScreen);
            } else if (button.id == 1) {
                this.mc.shutdown();
                onGuiClosed();
            } else if (button.id == 2) {
                never = !never;
                ((GuiCheckBox) button).setIsChecked(never);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        config.getCategory("warned")
            .get("configWarningShown")
            .set(never);
        config.save();
    }

    @Override
    public void drawScreen(int x, int y, float renderPartialTicks) {
        this.drawDefaultBackground();
        String header = "\u00a7l" + I18n.format("gui.warn.notice");
        drawCenteredString(fontRendererObj, header, width / 2, ((height / 4 - 60) + 60), 0xDDD605);

        String errMsg = I18n.format("gui.warn.configSplit1");
        fontRendererObj.resetStyles();
        fontRendererObj.textColor = 0xFFFFFF;
        this.renderCenteredSplitString(errMsg, width / 2, ((height / 4 - 60) + 60) + 12, width - 9, false);

        String errMsg2 = I18n.format("gui.warn.configSplit2");
        fontRendererObj.resetStyles();
        fontRendererObj.textColor = 0xFFFFFF;
        this.renderCenteredSplitString(errMsg2, width / 2, 9 + yLevel, width - 9, false);

        super.drawScreen(x, y, renderPartialTicks);
    }

    private void renderCenteredSplitString(String text, int x, int y, int width, boolean shadow) {
        List<String> list = fontRendererObj.listFormattedStringToWidth(text, width);

        for (Iterator<String> iterator = list.iterator(); iterator
            .hasNext(); yLevel = (y += fontRendererObj.FONT_HEIGHT)) {
            String s1 = iterator.next();
            fontRendererObj.renderStringAligned(
                s1,
                x - (fontRendererObj.getStringWidth(s1) / 2),
                y,
                width,
                fontRendererObj.textColor,
                shadow);
        }
    }
}
