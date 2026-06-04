package ganymedes01.etfuturum.mixins.early.sounds;

import ganymedes01.etfuturum.Tags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBeacon.class)
public abstract class MixinTileEntityBeacon extends TileEntity {

	@Shadow
	private boolean field_146015_k;

	@Unique
	boolean etfuturum$wasOn; //Used to store the previous powered state of the beacon, so we can tell when to play the on/off sound

	@Unique
	private boolean etfuturum$isNetherliciousBeacon;

	@Inject(method = "updateEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntityBeacon;func_146000_x()V", shift = At.Shift.BEFORE))
	private void playBeaconSounds(CallbackInfo ci) {
		if (!etfuturum$isNetherliciousBeacon) {
			if (!worldObj.isRemote) {
				if (field_146015_k != etfuturum$wasOn) {
					String suffix = field_146015_k ? "activate" : "deactivate";
					worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Tags.MC_ASSET_VER + ":block.beacon." + suffix, 1, 1);
				}
				etfuturum$wasOn = field_146015_k;
			}
			if (field_146015_k) {
				worldObj.playSound(xCoord + .5D, yCoord + .5D, zCoord + .5D, Tags.MC_ASSET_VER + ":block.beacon.ambient", 1, 1, false);
			}
		}
	}

	@Inject(method = "setPrimaryEffect", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/TileEntityBeacon;primaryEffect:I", ordinal = 1))
	private void playPowerSelectNoise(CallbackInfo ci) {
		//Somehow worldObj can be null, probably due to the weird stuff the client is doing to its own TileEntityBeacon instance while in the GUI
		if (!etfuturum$isNetherliciousBeacon && worldObj != null && !worldObj.isRemote) {
			worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Tags.MC_ASSET_VER + ":block.beacon.power_select", 1, 1);
		}
	}

	@Override
	public void validate() {
		etfuturum$isNetherliciousBeacon = getClass().getName().toLowerCase().contains("netherlicious");
		super.validate();
	}

	/**
	 * The deactivation sound in vanilla plays when the beacon beam is severed (see above) or when the block is broken even if it's not powered (thus causing this function to run)
	 * We also check if the tile entity at the coordinate is null, because otherwise the deactivate sound will play when placing a vanilla beacon with tile swap mode on to change it to the EFR beacon.
	 * This is because when swapping the TE it invalidates the old TE. This runs after the new TE is created to we can simply check if it's null.
	 */
	@Override
	public void invalidate() {
		if (!etfuturum$isNetherliciousBeacon && !worldObj.isRemote && worldObj.getTileEntity(xCoord, yCoord, zCoord) == null) {
			worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, Tags.MC_ASSET_VER + ":block.beacon.deactivate", 1, 1);
		}
		super.invalidate();
	}
}
