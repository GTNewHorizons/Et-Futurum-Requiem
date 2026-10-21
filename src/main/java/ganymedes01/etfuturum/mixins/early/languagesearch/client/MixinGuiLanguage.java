package ganymedes01.etfuturum.mixins.early.languagesearch.client;

import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(GuiLanguage.class)
public abstract class MixinGuiLanguage extends GuiScreen {

	@Shadow
	private GuiLanguage.List field_146450_f;

	@Unique
	private GuiTextField etfu$searchBox;

	@Unique
	private int etfu$scrollAmount;

	@Inject(method = "initGui", at = @At("HEAD"))
	private void etfu$saveScroll(CallbackInfo ci) {
		// Record list scroll amount when resizing or toggling Unicode fonts
		if (field_146450_f != null) {
			etfu$scrollAmount = field_146450_f.getAmountScrolled();
		}
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "initGui", at = @At("RETURN"))
	private void etfu$initSearch(CallbackInfo ci) {
		Keyboard.enableRepeatEvents(true);
		if (etfu$searchBox == null) {
			etfu$searchBox = new GuiTextField(fontRendererObj, width / 2 - 100, 19, 200, 14);
			etfu$searchBox.setMaxStringLength(48);
			etfu$searchBox.setFocused(true);
			// Scroll selected slot to middle of screen
			int index = new ArrayList<>(mc.getLanguageManager().getLanguages()).indexOf(mc.getLanguageManager().getCurrentLanguage());
            etfu$scrollAmount = index * field_146450_f.slotHeight + (field_146450_f.slotHeight + 4) / 2
                    - (field_146450_f.bottom - field_146450_f.top) / 2;
		} else {
			// Keep the query, cursor and selection when resizing or toggling Unicode fonts.
			etfu$searchBox.xPosition = width / 2 - 100;
			((Consumer<String>) field_146450_f).accept(etfu$searchBox.getText());
		}
		field_146450_f.scrollBy(etfu$scrollAmount);
	}

	@ModifyConstant(method = "drawScreen", constant = @Constant(intValue = 16))
	private int etfu$moveTitle(int original) {
		return 6;
	}

	@Inject(method = "drawScreen", at = @At("RETURN"))
	private void etfu$drawSearch(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		etfu$searchBox.drawTextBox();
		if (etfu$searchBox.getText().isEmpty() && !etfu$searchBox.isFocused()) {
			fontRendererObj.drawStringWithShadow(I18n.format("etfuturum.gui.language_search"),
					etfu$searchBox.xPosition + 4, etfu$searchBox.yPosition + 3, 0x808080);
		}
	}

	@Override
	public void updateScreen() {
		etfu$searchBox.updateCursorCounter();
		super.updateScreen();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void keyTyped(char typedChar, int keyCode) {
		if (etfu$searchBox.textboxKeyTyped(typedChar, keyCode)) {
			((Consumer<String>) field_146450_f).accept(etfu$searchBox.getText());
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		etfu$searchBox.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Mixin(GuiLanguage.List.class)
	public abstract static class MixinList extends GuiSlot implements Consumer<String> {

		@Shadow
		@Final
		private List<String> field_148176_l;

		@Shadow
		@Final
		private Map<String, Language> field_148177_m;

		@Unique
		private List<String> etfu$allLanguages;

		protected MixinList() {
			super(null, 0, 0, 0, 0, 0);
		}

		@Inject(method = "<init>", at = @At("RETURN"))
		private void etfu$initSearch(CallbackInfo ci) {
			top = 36;
			field_148163_i = false;
			etfu$allLanguages = new ArrayList<>(field_148176_l);
		}

		@Override
		public void accept(String filter) {
			String search = filter.toLowerCase(Locale.ROOT);
			field_148176_l.clear();
			for (String code : etfu$allLanguages) {
				if (code.toLowerCase(Locale.ROOT).contains(search)
						|| field_148177_m.get(code).toString().toLowerCase(Locale.ROOT).contains(search)) {
					field_148176_l.add(code);
				}
			}
		}
	}
}
