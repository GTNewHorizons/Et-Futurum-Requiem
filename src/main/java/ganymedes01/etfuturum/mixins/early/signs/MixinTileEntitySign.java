package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.ducks.ISign;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Enable sign waxing, back text, and dye color update logic
 * Also applies to modded signs since TileEntityWoodSign extends TileEntitySign
 *
 * @author mosesyu1028
 */
@Mixin(TileEntitySign.class)
public abstract class MixinTileEntitySign implements ISign {

	@Unique
	private boolean waxed;
	@Unique
	private int dyeId = -1;
	@Unique
	private String[] signBackText = new String[]{"", "", "", ""};

	@Override
	public boolean isWaxed() {
		return waxed;
	}

	@Override
	public void setWaxed(boolean waxed) {
		this.waxed = waxed;
	}

	@Override
	public int getDyeId() {
		return dyeId;
	}

	@Override
	public void setDyeId(int dyeId) {
		this.dyeId = dyeId;
	}

	@Override
	public String[] getSignText(boolean front) {
		TileEntitySign self = (TileEntitySign) (Object) this;
		if (front) {
			return self.signText;
		}
		if (signBackText == null) {
			signBackText = new String[]{"", "", "", ""};
		}
		return signBackText;
	}

	@Inject(method = "writeToNBT", at = @At("TAIL"))
	private void onWriteToNBT(NBTTagCompound nbt, CallbackInfo ci) {
		nbt.setBoolean("Waxed", waxed);
		nbt.setInteger("DyeId", dyeId);
		if (signBackText != null) {
			for (int i = 0; i < 4; i++) {
				nbt.setString("BackText" + (i + 1), signBackText[i] != null ? signBackText[i] : "");
			}
		}
	}

	@Inject(method = "readFromNBT", at = @At("TAIL"))
	private void onReadFromNBT(NBTTagCompound nbt, CallbackInfo ci) {
		TileEntitySign self = (TileEntitySign) (Object) this;

		// Read custom fields
		waxed = nbt.getBoolean("Waxed");
		dyeId = nbt.hasKey("DyeId") ? nbt.getInteger("DyeId") : -1;
		if (signBackText == null) {
			signBackText = new String[]{"", "", "", ""};
		}
		for (int i = 0; i < 4; i++) {
			String key = "BackText" + (i + 1);
			signBackText[i] = nbt.hasKey(key) ? nbt.getString(key) : "";
		}

		// Trigger client re-render so waxed/dyed signs show correctly after chunk load
		if (self.getWorldObj() != null && self.getWorldObj().isRemote) {
			self.getWorldObj().func_147479_m(self.xCoord, self.yCoord, self.zCoord);
		}
	}

	/**
	 * Vanilla's NetHandlerPlayServer.processUpdateSign checks func_145914_a()
	 * (is an editing player set?) and logs a warning if not found.
	 * MixinBlockSign messes with that, so always return true here.
	 * Probably OK to suppress warning? It didn't stop anything before anyways.
	 */
	@Inject(method = "func_145914_a", at = @At("HEAD"), cancellable = true)
	private void onGetIsEditable(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
