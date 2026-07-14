package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.ducks.IWaxableSign;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntitySign.class)
public abstract class MixinTileEntitySign implements IWaxableSign {

	@Unique
	private boolean waxed;

	@Override
	public boolean isWaxed() {
		return waxed;
	}

	@Override
	public void setWaxed(boolean waxed) {
		this.waxed = waxed;
	}

	@Inject(method = "writeToNBT", at = @At("TAIL"))
	private void onWriteToNBT(NBTTagCompound nbt, CallbackInfo ci) {
		nbt.setBoolean("Waxed", waxed);
	}

	@Inject(method = "readFromNBT", at = @At("TAIL"))
	private void onReadFromNBT(NBTTagCompound nbt, CallbackInfo ci) {
		waxed = nbt.getBoolean("Waxed");
	}

	/**
	 * Vanilla's NetHandlerPlayServer.processUpdateSign checks func_145914_a()
	 * (is an editing player set?) and logs a warning if not found.
	 * MixinBlockSign messes with that, so always return true here.
     * Probably OK to surpress warning? It didn't stop anything before anyways.
	 */
	@Inject(method = "func_145914_a", at = @At("HEAD"), cancellable = true)
	private void onGetIsEditable(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
