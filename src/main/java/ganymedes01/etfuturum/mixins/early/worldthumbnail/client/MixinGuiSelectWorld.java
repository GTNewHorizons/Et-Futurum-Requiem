package ganymedes01.etfuturum.mixins.early.worldthumbnail.client;

import ganymedes01.etfuturum.client.WorldIconManager;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.world.storage.SaveFormatComparator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(GuiSelectWorld.class)
public abstract class MixinGuiSelectWorld extends GuiScreen {

    @Shadow
    public List field_146639_s;

    @Shadow
    private int field_146640_r;

    @Unique
    private GuiTextField etfu$searchBox;

    @Unique
    private List etfu$allWorlds;

    @Unique
    private static Field etfu$slotField;

    @Inject(method = "initGui", at = @At("RETURN"))
    private void etfu$initSearch(CallbackInfo ci) {
        if (ConfigMixins.worldSaveThumbnails && field_146639_s != null) {
            WorldIconManager.clearCache();
            for (Object obj : field_146639_s) {
                WorldIconManager.getOrLoadIcon(((SaveFormatComparator) obj).getFileName());
            }
        }

        if (field_146639_s != null) {
            etfu$allWorlds = new ArrayList(field_146639_s);
        }

        etfu$searchBox = new GuiTextField(this.fontRendererObj,
                this.width / 2 - 100, 33, 200, 14);
        etfu$searchBox.setMaxStringLength(50);

        try {
            if (etfu$slotField == null) {
                for (Field f : GuiSelectWorld.class.getDeclaredFields()) {
                    if (GuiSlot.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        etfu$slotField = f;
                        break;
                    }
                }
            }
            if (etfu$slotField != null) {
                GuiSlot slot = (GuiSlot) etfu$slotField.get(this);
                if (slot != null) slot.top = 52;
            }
        } catch (Exception ignored) {}
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void etfu$drawSearch(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (etfu$searchBox != null) {
            etfu$searchBox.updateCursorCounter();
            etfu$searchBox.drawTextBox();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (etfu$searchBox != null && etfu$searchBox.isFocused()) {
            if (keyCode == 1) {
                etfu$searchBox.setFocused(false);
                return;
            }
            String before = etfu$searchBox.getText();
            etfu$searchBox.textboxKeyTyped(typedChar, keyCode);
            String after = etfu$searchBox.getText();
            if (!before.equals(after)) {
                etfu$updateFilter(after);
            }
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (etfu$searchBox != null) {
            etfu$searchBox.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Unique
    @SuppressWarnings("unchecked")
    private void etfu$updateFilter(String filter) {
        if (etfu$allWorlds == null) return;
        String lower = filter.toLowerCase(Locale.ROOT);
        if (lower.isEmpty()) {
            field_146639_s = new ArrayList(etfu$allWorlds);
        } else {
            List filtered = new ArrayList();
            for (Object obj : etfu$allWorlds) {
                SaveFormatComparator save = (SaveFormatComparator) obj;
                String name = save.getDisplayName();
                if (name == null) name = "";
                String id = save.getFileName();
                if (id == null) id = "";
                if (name.toLowerCase(Locale.ROOT).contains(lower)
                        || id.toLowerCase(Locale.ROOT).contains(lower)) {
                    filtered.add(save);
                }
            }
            field_146639_s = filtered;
        }
        field_146640_r = -1;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (ConfigMixins.worldSaveThumbnails) {
            WorldIconManager.clearCache();
        }
    }
}
