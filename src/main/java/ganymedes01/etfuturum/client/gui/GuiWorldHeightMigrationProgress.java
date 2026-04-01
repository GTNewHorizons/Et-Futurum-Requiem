package ganymedes01.etfuturum.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import ganymedes01.etfuturum.world.WorldHeightHandler.WorldHeightMigrator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GuiWorldHeightMigrationProgress extends GuiScreen implements IGUIProgress {

    private final Logger logger = LogManager.getLogger();

    private volatile GuiSelectWorld parent;
    private volatile int progress = 5;
    private volatile int maxProgress = 10;
    private volatile String progressText;
    private volatile File worldDir;
    private volatile String saveName;

    //Marks the result of the migration. 0 = not done; 1 = successful; 2 = failed
    private volatile byte successfulMigration = 0;

    public GuiWorldHeightMigrationProgress(GuiSelectWorld parent, File worldDir, String saveName) {

        this.parent = parent;
        this.worldDir = worldDir;
        this.saveName = saveName;
    }

    @Override
    public void initGui() {

        this.buttonList.add(new GuiOptionButton(0, (this.width - 150) / 2, this.height / 6 * 5 , "Load World"));
        this.buttonList.add(new GuiOptionButton(1, (this.width - 150) / 2, this.height / 6 * 5 , "Return to Home"));

        buttonList.get(0).enabled = false;
        buttonList.get(0).visible = false;
        buttonList.get(1).enabled = false;
        buttonList.get(1).visible = false;

        startMigration();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        drawCenteredString(fontRendererObj, StatCollector.translateToLocal("gui.worldheight.migration.inprogress"), centerX, centerY - 50, 0xFFFFFF);

        int progressBarWidth = this.width / 10 * 6;
        int progressBarHeight = 10;

        int filled = progress >= 0 ? (int)((progress / (float) maxProgress) * progressBarWidth) : maxProgress * progressBarWidth;

        drawRect(centerX - progressBarWidth / 2, centerY - progressBarHeight / 2, centerX + progressBarWidth / 2, centerY + progressBarHeight / 2, 0xFFFFFFFF);

        if(maxProgress > 0) {

            drawRect(centerX - progressBarWidth / 2, centerY - progressBarHeight / 2, centerX - progressBarWidth / 2 + filled, centerY + progressBarHeight / 2, 0xFF55FF55);
        }

        drawCenteredString(fontRendererObj, progressText + (progress >= 0 ? ": " + progress + " / " + maxProgress : ""), centerX, centerY + 15, 0x55FF55);

        if (successfulMigration == 1) {

            //((GuiButton) this.buttonList.get(0)).drawButton(this.mc, mouseX, mouseY);

        } else if (successfulMigration == 2) {

            List<String> failedTextLines = fontRendererObj.listFormattedStringToWidth(StatCollector.translateToLocal("gui.worldheight.migration.failed.text"), this.width / 10 * 5);

            int y = centerY + 40;

            for (String failedText : failedTextLines) {

                this.drawCenteredString(this.fontRendererObj, failedText, this.width / 2, y += 17, 0xFFFFFF);
            }

            //((GuiButton) this.buttonList.get(1)).drawButton(this.mc, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {

        if (successfulMigration == 1) {

            this.mc.displayGuiScreen((GuiScreen)null);

            // Load world again
            System.out.println("Welt neu laden!");
            FMLClientHandler.instance().tryLoadExistingWorld(parent, worldDir.getName(), saveName);

        } else if (successfulMigration == 2) {

            ObfuscationReflectionHelper.setPrivateValue(GuiSelectWorld.class, parent, false, "field_" + "146634_i");
            FMLClientHandler.instance().showGuiScreen(parent);

        } else {

            logger.info("Migration of world {} is not done yet!", worldDir.getName());
        }
    }

    private void startMigration() {

        new Thread(() -> {

            try {

                WorldHeightMigrator.migrateWorldHeightWithProgress(worldDir, this);

            } catch (IOException e) {

                logger.error("World Migration failed! Stopped loading world.", e);

                setSuccessful((byte) 2);
            }
        }, "World Height Migration Thread").start();
    }

    @Override
    public void setProgress(int progress) {

        this.progress = progress;
    }

    @Override
    public void setMaxProgress(int maxProgress) {

        this.maxProgress = maxProgress;
    }

    @Override
    public void setProgressText(String progressText) {

        this.progressText = progressText;
    }

    @Override
    public void setSuccessful(byte successful) {

        this.successfulMigration = successful;
        if (!buttonList.isEmpty()) {
            switch (successful) {
                case 1:
                    buttonList.get(0).enabled = true;
                    buttonList.get(0).visible = true;
                    break;
                case 2:
                    buttonList.get(1).enabled = true;
                    buttonList.get(1).visible = true;
                    break;
            }
        }
    }
}
