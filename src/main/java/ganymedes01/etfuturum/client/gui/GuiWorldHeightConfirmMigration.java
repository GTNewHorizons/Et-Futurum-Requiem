package ganymedes01.etfuturum.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.client.gui.*;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.List;

public class GuiWorldHeightConfirmMigration extends GuiYesNo implements GuiYesNoCallback{

    private int flag;
    private GuiSelectWorld parent;
    private String description;
    private String confirmation;
    private File worldDir;
    private String saveName;

    private int linkWidth, linkX, linkY;

    public GuiWorldHeightConfirmMigration(GuiSelectWorld parent, String title, String description, File worldDir, String saveName) {

        super(null, title, description, StatCollector.translateToLocal("gui.worldheight.button.migrate"), StatCollector.translateToLocal("gui.toMenu"), 0);

        this.description = description;

        this.worldDir = worldDir;
        this.saveName = saveName;
        this.parent = parent;
        this.confirmation = StatCollector.translateToLocal("gui.worldheight.migration.confirmation");
    }

    @Override
    public void initGui() {

        linkWidth = fontRendererObj.getStringWidth(EtFuturum.githubURL);

        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 * 5 , this.confirmButtonText));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 * 5, this.cancelButtonText));
    }

    @Override
    protected void actionPerformed(GuiButton button) {

        if(button.id == 0) {

            this.mc.displayGuiScreen((GuiScreen) null);

            FMLClientHandler.instance().showGuiScreen(new GuiWorldHeightMigrationProgress(parent, worldDir, saveName));

        } else {

            ObfuscationReflectionHelper.setPrivateValue(GuiSelectWorld.class, (GuiSelectWorld)parent, false, "field_"+"146634_i");
            FMLClientHandler.instance().showGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();

        int y = this.height / 6;

        this.drawCenteredString(this.fontRendererObj, this.field_146351_f, this.width / 2, y - 20, 16777215);

        y += 20;

        List<String> lines = fontRendererObj.listFormattedStringToWidth(description, this.width - 50);

        for (String descriptionLine : lines) {

            this.drawCenteredString(this.fontRendererObj, descriptionLine, this.width / 2, y, 16777215);

            y += 17;
        }

        this.drawCenteredString(this.fontRendererObj, StatCollector.translateToLocal("gui.worldheight.migration.github"), this.width / 2, y += 40, 16777215);

        linkX = this.width / 2 - linkWidth / 2;
        linkY = y += 20;

        if (mouseX >= linkX && mouseX <= linkX + linkWidth && mouseY >= linkY && mouseY <= linkY + 12) {

            this.drawString(this.fontRendererObj, EtFuturum.githubURL, linkX, linkY, 0x55AAFF);

        } else {

            this.drawString(this.fontRendererObj, EtFuturum.githubURL, linkX, linkY, 0xAAFFAA);
        }

        this.drawCenteredString(this.fontRendererObj, confirmation, this.width / 2, this.height / 6 * 5 - 40, 0xFFAAAA);

        for (GuiButton guiButton : this.buttonList) {

            ((GuiButton) guiButton).drawButton(this.mc, mouseX, mouseY);
        }

        for (GuiLabel guiLabel : this.labelList) {

            ((GuiLabel) guiLabel).func_146159_a(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {

        if (mouseX >= linkX && mouseX <= linkX + linkWidth && mouseY >= linkY && mouseY <= linkY + 12) {

            openLink(EtFuturum.githubURL);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void openLink(String url) {

        try {

            Desktop.getDesktop().browse(new URI(url));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
