package ganymedes01.etfuturum.client.sound;

import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class ElytraSound extends MovingSound {
	private final EntityPlayerSP player;
	private int time;

	public ElytraSound(EntityPlayerSP p_i47113_1_) {
		super(new ResourceLocation(Tags.MC_ASSET_VER + ":item.elytra.flying"));
		this.player = p_i47113_1_;
		this.repeat = true;
		this.volume = 0.1F;
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	@Override
	public void update() {
		++this.time;

		if (!this.player.isDead && (this.time <= 20 || ((IElytraPlayer) player).etfu$isElytraFlying())) {
			this.xPosF = (float) this.player.posX;
			this.yPosF = (float) this.player.posY;
			this.zPosF = (float) this.player.posZ;
			float f = MathHelper.sqrt_double(this.player.motionX * this.player.motionX + this.player.motionZ * this.player.motionZ + this.player.motionY * this.player.motionY);
			float f1 = f / 2.0F;

			if (f >= 0.01D) {
				this.volume = MathHelper.clamp_float(f1 * f1, 0.0F, 1.0F);
			} else {
				this.volume = 0.0F;
			}

			if (this.time < 20) {
				this.volume = 0.0F;
			} else if (this.time < 40) {
				this.volume = (float) (this.volume * ((this.time - 20) / 20.0D));
			}

			if (this.volume > 0.8F) {
				this.field_147663_c = 1.0F + (this.volume - 0.8F); // pitch
			} else {
				this.field_147663_c = 1.0F; // pitch
			}
		} else {
			this.donePlaying = true;
		}
	}
}