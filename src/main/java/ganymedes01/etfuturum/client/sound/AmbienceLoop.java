package ganymedes01.etfuturum.client.sound;

import cpw.mods.fml.client.FMLClientHandler;
import ganymedes01.etfuturum.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import roadhog360.hogutils.api.utils.FastRandom;

import java.util.Random;

public class AmbienceLoop extends MovingSound {

	private final Random rand = new FastRandom();
	private final String name;

	public boolean isStopping;
	private final int minAmbienceDelay;
	private final int maxAmbienceDelay;
	private int ambienceDelay;

	private boolean muteMainTrack;
	private boolean hasAdditions;
	private boolean hasCaveSoundOverride;
	private boolean hasMusicOverride;

	private final Minecraft mc = FMLClientHandler.instance().getClient();

	public AmbienceLoop(String loc, int minAmbienceDelay, int maxAmbienceDelay) {
		super(new ResourceLocation(Tags.MC_ASSET_VER + ":ambient." + loc + ".loop"));
		repeat = true;
		field_147666_i = ISound.AttenuationType.NONE; // attenuationType
		this.field_147665_h = 0; // repeatDelay
		volume = 0.01F;
		this.minAmbienceDelay = minAmbienceDelay;
		this.maxAmbienceDelay = maxAmbienceDelay;
		ambienceDelay = maxAmbienceDelay;
		name = loc;
		setHasAdditions(true);
		setHasMusicOverride(true);
		setHasCaveSoundOverride(true);
	}

	public AmbienceLoop createNew() {
		AmbienceLoop loop = new AmbienceLoop(name, minAmbienceDelay, maxAmbienceDelay);
		loop.setHasAdditions(hasAdditions);
		loop.setHasMusicOverride(hasMusicOverride);
		loop.setHasCaveSoundOverride(hasCaveSoundOverride);
		return loop;
	}

	public void setMuteMainTrack(boolean mute) {
		muteMainTrack = mute;
		volume = 0.01F; //We can't set it to 0 or the sound ends.
	}

	public void setHasAdditions(boolean additions) {
		hasAdditions = additions;
	}

	public void setHasCaveSoundOverride(boolean caveSounds) {
		hasCaveSoundOverride = caveSounds;
	}

	public void setHasMusicOverride(boolean music) {
		hasMusicOverride = music;
	}

	public boolean hasAdditions() {
		return hasAdditions;
	}

	public boolean hasCaveSoundOverride() {
		return hasCaveSoundOverride;
	}

	public boolean hasMusicOverride() {
		return hasMusicOverride;
	}

	public String getName() {
		return name;
	}

	@Override
	public void update() {
		if (isStopping) {
			volume -= 0.0175F;
			if (volume < 0) {
				donePlaying = true;
				return;
			}
		} else if (!muteMainTrack && volume != 1 && (volume += 0.0175F) > 1) {
			volume = 1;
		}
		if (volume == 1) {
			if (hasAdditions && ambienceDelay-- <= 0) {
				ambienceDelay = MathHelper.getRandomIntegerInRange(rand, minAmbienceDelay, maxAmbienceDelay);
				mc.getSoundHandler().playSound(new AmbienceAdditionSound(Tags.MC_ASSET_VER + ":ambient." + name + ".additions"));
			}
		}
	}

	public void skipFadeIn() {
		volume = 1;
	}

	public void fadeOut() {
		isStopping = true;
	}
}
