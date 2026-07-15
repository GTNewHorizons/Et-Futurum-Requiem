package ganymedes01.etfuturum.mixins.early.unicodepages.client;

import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GuiTextField.class)
public abstract class MixinGuiTextField {

	@Shadow
	private String text;

	@Shadow
	private int cursorPosition;

	@Shadow
	private int selectionEnd;

	private boolean etfr$midPair(int pos) {
		return pos > 0 && pos < this.text.length()
				&& Character.isHighSurrogate(this.text.charAt(pos - 1))
				&& Character.isLowSurrogate(this.text.charAt(pos));
	}

	private int etfr$snap(int pos, int from) {
		if (!etfr$midPair(pos)) {
			return pos;
		}
		return pos > from ? pos + 1 : pos - 1;
	}

	@ModifyVariable(method = "setCursorPosition", at = @At("HEAD"), argsOnly = true)
	private int etfr$snapCursor(int pos) {
		return etfr$snap(pos, this.selectionEnd);
	}

	@ModifyVariable(method = "setSelectionPos", at = @At("HEAD"), argsOnly = true)
	private int etfr$snapSelection(int pos) {
		return etfr$snap(pos, this.selectionEnd);
	}

	@ModifyVariable(method = "deleteFromCursor", at = @At("HEAD"), argsOnly = true)
	private int etfr$deleteWholeCodepoints(int num) {
		if (this.selectionEnd != this.cursorPosition) {
			return num;
		}
		final int boundary = this.cursorPosition + num;
		if (etfr$midPair(boundary)) {
			return num + (num < 0 ? -1 : 1);
		}
		return num;
	}
}
