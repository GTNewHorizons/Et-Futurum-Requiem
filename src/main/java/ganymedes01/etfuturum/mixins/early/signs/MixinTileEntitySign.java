package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.ducks.IWaxableSign;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
