package ganymedes01.etfuturum.mixins.early.worldheight;

import com.llamalad7.mixinextras.sugar.Local;
import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.client.gui.GuiWorldHeightConfirmDisabling;
import ganymedes01.etfuturum.client.gui.GuiWorldHeightConfirmMigration;
import ganymedes01.etfuturum.core.utils.helpers.NBTHelper;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.world.WorldHeightHandler;
import ganymedes01.etfuturum.world.WorldHeightHandler.NBTTags;
import ganymedes01.etfuturum.world.WorldHeightHandler.WorldHeightMigrator;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(FMLClientHandler.class)
public abstract class MixinFMLClientHandler {

    @Shadow(remap = false) public abstract void showGuiScreen(Object clientGuiElement);

    @Inject(method = "tryLoadExistingWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;getCompoundTag(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;"), cancellable = true)
    private void checkIncreasedHeight(GuiSelectWorld gui, String dirName, String saveName, CallbackInfo ci, @Local File dir, @Local NBTTagCompound leveldatNBT) throws IOException {

        NBTTagCompound etfuturumNBT = leveldatNBT.getCompoundTag(Reference.MOD_CONTAINER_ID);

        int migrationFlag = WorldHeightMigrator.isMigrationNeeded(etfuturumNBT);

        switch (migrationFlag) {
            case 1:
                showGuiScreen(new GuiWorldHeightConfirmMigration(gui, StatCollector.translateToLocal("gui.worldheight.migration.title"),
                        StatCollector.translateToLocalFormatted("gui.worldheight.migration.confirm.description.upper", EnumChatFormatting.LIGHT_PURPLE + Reference.MOD_NAME + EnumChatFormatting.RESET, EnumChatFormatting.GREEN + String.valueOf(WorldHeightHandler.getWorldHeightOffset()) + EnumChatFormatting.RESET),
                        dir, saveName));
                ci.cancel();
                break;
            case 2:
                showGuiScreen(new GuiWorldHeightConfirmMigration(gui, StatCollector.translateToLocal("gui.worldheight.migration.title"),
                        StatCollector.translateToLocalFormatted("gui.worldheight.migration.confirm.description.lower", EnumChatFormatting.LIGHT_PURPLE + Reference.MOD_NAME + EnumChatFormatting.RESET, EnumChatFormatting.GREEN + String.valueOf(WorldHeightHandler.getWorldHeightOffset()) + EnumChatFormatting.RESET, EnumChatFormatting.RED + String.valueOf(Math.abs(WorldHeightHandler.getWorldHeightOffset())) + EnumChatFormatting.RESET),
                        dir, saveName));
                ci.cancel();
                break;
            case 3:
                showGuiScreen(new GuiWorldHeightConfirmDisabling(gui, StatCollector.translateToLocal("gui.worldheight.migration.title"),
                        StatCollector.translateToLocalFormatted("gui.worldheight.migration.description.smaller", EnumChatFormatting.LIGHT_PURPLE + Reference.MOD_NAME + EnumChatFormatting.RESET, EnumChatFormatting.GREEN + String.valueOf(WorldHeightHandler.getMaxWorldHeight()) + EnumChatFormatting.RESET, EnumChatFormatting.RED + String.valueOf(etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString())) + EnumChatFormatting.RESET, EnumChatFormatting.GREEN + String.valueOf(WorldHeightHandler.getMaxWorldHeight()) + EnumChatFormatting.RESET),
                        dir, saveName));
                break;
            case 4:
                showGuiScreen(new GuiWorldHeightConfirmDisabling(gui, StatCollector.translateToLocal("gui.worldheight.migration.title"),
                        StatCollector.translateToLocalFormatted("gui.worldheight.migration.confirm.description.disabling", EnumChatFormatting.RED + String.valueOf(etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString())) + EnumChatFormatting.RESET, EnumChatFormatting.RED + String.valueOf(etfuturumNBT.getInteger(NBTTags.MAX_HEIGHT.toString())) + EnumChatFormatting.RESET),
                        dir, saveName));
                ci.cancel();
                break;
            default:
                etfuturumNBT.setBoolean(NBTTags.HEIGHT_ENABLED.toString(), WorldHeightHandler.isIncreasedWorldHeightEnabled());
                etfuturumNBT.setInteger(NBTTags.MAX_HEIGHT.toString(), WorldHeightHandler.getMaxWorldHeight());
                NBTHelper.writeNBTToFile(leveldatNBT, new File(dir, "level.dat"));
                break;
        }
    }
}
