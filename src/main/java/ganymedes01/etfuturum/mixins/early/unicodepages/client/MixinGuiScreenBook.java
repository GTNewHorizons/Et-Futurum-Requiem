package ganymedes01.etfuturum.mixins.early.unicodepages.client;

import net.minecraft.client.gui.GuiScreenBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiScreenBook.class)
public abstract class MixinGuiScreenBook {

	@Redirect(method = {"keyTypedInBook", "func_146460_c"},
			at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(II)Ljava/lang/String;"))
	private String etfr$backspaceWholeCodepoint(String text, int start, int end) {
		if (end > start && end < text.length()
				&& Character.isLowSurrogate(text.charAt(end))
				&& Character.isHighSurrogate(text.charAt(end - 1))) {
			end--;
		}
		return text.substring(start, end);
	}
}
