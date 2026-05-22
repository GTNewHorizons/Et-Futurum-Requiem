package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.WorldIconManager;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.SaveFormatComparator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.GuiSelectWorld$List")
public abstract class MixinGuiSelectWorldList extends GuiSlot {

    @Shadow(aliases = "field_148207_k", remap = false)
    @Final
    GuiSelectWorld this$0;

    protected MixinGuiSelectWorldList() {
        super(null, 0, 0, 0, 0, 0);
    }

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void etfu$drawWorldIcon(int slotIndex, int x, int y, int slotHeight,
                                     Tessellator tess, int mouseX, int mouseY, CallbackInfo ci) {
        java.util.List<?> saves = this$0.field_146639_s;
        if (saves == null || slotIndex < 0 || slotIndex >= saves.size()) return;

        SaveFormatComparator save = (SaveFormatComparator) saves.get(slotIndex);
        String folderName = save.getFileName();
        ResourceLocation icon = WorldIconManager.getOrLoadIcon(folderName);
        WorldIconManager.drawIcon(icon, x, y);
    }

    @ModifyArg(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiSelectWorld;drawString(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V"
            ),
            index = 2
    )
    private int etfu$shiftTextForIcon(int x) {
        return x + WorldIconManager.ICON_OFFSET;
    }
}
