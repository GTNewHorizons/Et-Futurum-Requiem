package ganymedes01.etfuturum.client.sound;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.EntityBee;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class BeeFlySound extends MovingSound {
	protected final EntityBee beeInstance;
	private boolean hasSwitchedSound;
	private final SoundHandler soundHandler;
	private final boolean wasAngry;

	public BeeFlySound(EntityBee beeIn) {
		super(new ResourceLocation(Tags.MC_ASSET_VER + ":entity.bee.loop" + (beeIn.isAngry() ? "_aggressive" : "")));
		this.beeInstance = beeIn;
		xPosF = (float) beeIn.posX;
		yPosF = (float) beeIn.posY;
		zPosF = (float) beeIn.posZ;
		this.repeat = true;
		//It seems in 1.7.10 if the volume is 0 for too long or initialized at 0, the sound handler stops the sound.
		//Not sure why minecarts don't get this issue.
		this.volume = 0.001F;
		wasAngry = beeInstance.isAngry();

		soundHandler = FMLClientHandler.instance().getClient().getSoundHandler();
	}

	@Override
	public void update() {
		boolean flag = this.shouldSwitchSound();
		if (flag && !this.donePlaying) {
			soundHandler.playDelayedSound(this.getNextSound(), 1);
			this.hasSwitchedSound = true;
		}

		if (!this.beeInstance.isDead && !this.hasSwitchedSound) {
			xPosF = (float) beeInstance.posX;
			yPosF = (float) beeInstance.posY;
			zPosF = (float) beeInstance.posZ;
			float f = MathHelper.sqrt_double(beeInstance.motionX * beeInstance.motionX + beeInstance.motionZ * beeInstance.motionZ);
			if (!beeInstance.onGround && (double) f >= 0.01D) {
				this.field_147663_c /*pitch*/ = Utils.lerp(MathHelper.clamp_float(f, this.getMinPitch(), this.getMaxPitch()), this.getMinPitch(), this.getMaxPitch());
				this.volume = Utils.lerp(MathHelper.clamp_float(f, 0.0F, 0.5F), 0.0F, 1.2F);
			} else {
				this.field_147663_c = 0.0F; // pitch
				this.volume = 0.001F;
			}

		} else {
			this.donePlaying = true;
		}
	}

	private float getMinPitch() {
		return this.beeInstance.isChild() ? 1.1F : 0.7F;
	}

	private float getMaxPitch() {
		return this.beeInstance.isChild() ? 1.5F : 1.1F;
	}

	private MovingSound getNextSound() {
		return new BeeFlySound(beeInstance);
	}

	private boolean shouldSwitchSound() {
		return beeInstance.isAngry() != wasAngry;
	}
}
