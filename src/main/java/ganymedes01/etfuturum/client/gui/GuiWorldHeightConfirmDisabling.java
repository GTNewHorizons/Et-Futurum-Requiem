package ganymedes01.etfuturum.client.gui;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.GuiBackupFailed;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.ZipperUtil;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.helpers.NBTHelper;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.world.WorldHeightHandler;
import ganymedes01.etfuturum.world.WorldHeightHandler.NBTTags;
import net.minecraft.client.gui.*;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class GuiWorldHeightConfirmDisabling extends GuiYesNo implements GuiYesNoCallback {

    private GuiSelectWorld parent;
    private String description;
    private File worldDir;
    private String saveName;
    private File backupZip;

    public GuiWorldHeightConfirmDisabling(GuiYesNoCallback parent, String title, String description, File worldDir, String saveName) {

        super(parent, title, description, StatCollector.translateToLocal("gui.load_world"), StatCollector.translateToLocal("gui.toMenu"),0);

        this.parent = (GuiSelectWorld) parent;
        this.description = description;
        this.worldDir = worldDir;
        this.saveName = saveName;
        this.backupZip = new File(FMLClientHandler.instance().getSavesDirectory(),String.format("%s-%2$td%2$tm%2$ty%2$tH%2$tM%2$tS.zip", worldDir.getName(), System.currentTimeMillis()));
    }

    @Override
    public void initGui() {

        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 * 5 , this.confirmButtonText));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 * 5, this.cancelButtonText));
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

        for (GuiButton guiButton : this.buttonList) {

            ((GuiButton) guiButton).drawButton(this.mc, mouseX, mouseY);
        }

        for (GuiLabel guiLabel : this.labelList) {

            ((GuiLabel) guiLabel).func_146159_a(this.mc, mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {

        if (button.id == 0) {

            // Creating a backup

            FMLLog.info("Creating a backup of world %s into file %s", saveName, backupZip.getAbsolutePath());

            try {

                ZipperUtil.zip(worldDir, backupZip);

            } catch (IOException e) {

                FMLLog.log(Level.WARN, e, "There was a problem saving the backup %s. Please fix and try again", backupZip.getName());
                FMLClientHandler.instance().showGuiScreen(new GuiBackupFailed(parent, backupZip));

                return;
            }

            FMLClientHandler.instance().showGuiScreen(null);

            // Resetting the nbt tags in level.dat
            NBTTagCompound leveldatNBT;

            leveldatNBT = NBTHelper.readNBTFromFile(new File(worldDir, "level.dat"));

            NBTTagCompound etfuturumNBT = leveldatNBT.getCompoundTag(Reference.MOD_ID + ".container");

            if(WorldHeightHandler.isIncreasedWorldHeightEnabled()) {

                etfuturumNBT.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), true);
                etfuturumNBT.setInteger(NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());

            } else {

                etfuturumNBT.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), false);
                etfuturumNBT.setInteger(NBTTags.MAX_HEIGHT.toString(), 256);
                etfuturumNBT.setBoolean(NBTTags.HEIGHT_MIGRATED.toString(), false);
                etfuturumNBT.setInteger(NBTTags.HEIGHT_OFFSET.toString(), 0);
            }

            NBTHelper.writeNBTToFile(leveldatNBT, new File(worldDir, "level.dat"));

            // Load world again
            FMLClientHandler.instance().tryLoadExistingWorld(parent, worldDir.getName(), saveName);

        } else if (button.id == 1) {

            ObfuscationReflectionHelper.setPrivateValue(GuiSelectWorld.class, (GuiSelectWorld)parent, false, "field_"+"146634_i");
            FMLClientHandler.instance().showGuiScreen(parent);
        }

        super.actionPerformed(button);
    }
}
