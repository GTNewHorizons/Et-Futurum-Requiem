package ganymedes01.etfuturum.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;

import java.lang.ref.WeakReference;

public final class ModernLightmap {

	public static final float BLOCK_FACTOR = 1.4F;

	public static final float FLICKER_FACTOR = 0.1F;

	public static final float BIAS = 0.96F;

	private static final float[] OVERWORLD_AMBIENT = {10.0F / 255.0F, 10.0F / 255.0F, 10.0F / 255.0F};

	private static final float[] NETHER_AMBIENT = {48.0F / 255.0F, 40.0F / 255.0F, 33.0F / 255.0F};

	private static final float[] END_AMBIENT = {63.0F / 255.0F, 71.0F / 255.0F, 63.0F / 255.0F};

	public static final float[] NIGHT_VISION = {153.0F / 255.0F, 153.0F / 255.0F, 153.0F / 255.0F};

	private static final float[] BLOCK_TINT = {1.0f, 216.0F / 255.0F, 140.0F / 255.0F};

	public static final float[][] BLOCK_TINT_CURVE = {
			generateTintCurve(BLOCK_TINT[0]), generateTintCurve(BLOCK_TINT[1]), generateTintCurve(BLOCK_TINT[2])
	};

	private static final float[] OVERWORLD_TABLE = generateTable(0.0F);

	private static final float[] NETHER_TABLE = generateTable(0.1F);

	private static final float NO_AMBIENT_LIGHT = -1.0F;

	private static WeakReference<WorldProvider> cachedProvider = new WeakReference<>(null);

	private static float cachedAmbientLight = NO_AMBIENT_LIGHT;

	private static float[] cachedAmbient;

	private static boolean cachedModernCounterpart;

	private ModernLightmap() {
	}

	public static boolean hasNightVision(EntityPlayer player) {
		return player != null && player.isPotionActive(Potion.nightVision);
	}

	public static float floor(float ambient, int channel, float nightVisionBrightness) {
		return Math.max(ambient, NIGHT_VISION[channel] * nightVisionBrightness);
	}

	public static float endAmbient(int channel) {
		return END_AMBIENT[channel];
	}

	/**
	 * False for a modded dimension whose brightness table matches nothing we know how to rebuild;
	 * every modern lightmap change stays off there so the dimension keeps the look its mod tuned.
	 */
	public static boolean hasModernCounterpart(WorldProvider provider) {
		resolve(provider);
		return cachedModernCounterpart;
	}

	public static boolean hasAmbientColor(WorldProvider provider) {
		resolve(provider);
		return cachedAmbient != null;
	}

	public static float ambientColor(WorldProvider provider, int channel) {
		resolve(provider);
		return cachedAmbient == null ? 0.0F : cachedAmbient[channel];
	}

	private static void resolve(WorldProvider provider) {
		float ambientLight = ambientLight(provider);
		if (cachedProvider.get() == provider && cachedAmbientLight == ambientLight) {
			return;
		}
		cachedProvider = new WeakReference<>(provider);
		cachedAmbientLight = ambientLight;
		cachedAmbient = computeAmbientColor(provider);
		cachedModernCounterpart = provider.dimensionId == 1 || cachedAmbient != null;
	}

	private static float ambientLight(WorldProvider provider) {
		float[] table = provider.lightBrightnessTable;
		return table == null || table.length == 0 ? NO_AMBIENT_LIGHT : table[0];
	}

	private static float[] computeAmbientColor(WorldProvider provider) {
		switch (provider.dimensionId) {
			case 0:
				return OVERWORLD_AMBIENT;
			case -1:
				return netherAmbient(provider);
			case 1:
				return null; // The End writes its own floor in the dimensionId == 1 branch.
			default:
				break;
		}
		if (provider instanceof WorldProviderHell) {
			return netherAmbient(provider);
		}
		if (provider instanceof WorldProviderEnd) {
			return END_AMBIENT;
		}
		if (matchesTable(provider, OVERWORLD_TABLE)) {
			return OVERWORLD_AMBIENT;
		}
		return matchesTable(provider, NETHER_TABLE) ? NETHER_AMBIENT : null;
	}

	private static float[] netherAmbient(WorldProvider provider) {
		float[] table = provider.lightBrightnessTable;
		return table == null || table.length == 0 || table[0] <= 0.0F ? null : NETHER_AMBIENT;
	}

	private static boolean matchesTable(WorldProvider provider, float[] reference) {
		float[] table = provider.lightBrightnessTable;
		if (table == null || table.length != reference.length) {
			return false;
		}
		for (int i = 0; i < reference.length; i++) {
			if (table[i] != reference[i]) {
				return false;
			}
		}
		return true;
	}

	private static float[] generateTable(float ambientLight) {
		float[] table = new float[16];
		for (int i = 0; i <= 15; i++) {
			float f1 = 1.0F - (float) i / 15.0F;
			table[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - ambientLight) + ambientLight;
		}
		return table;
	}

	private static float[] generateTintCurve(float tint) {
		float[] curve = new float[16];
		for (int i = 0; i <= 15; i++) {
			float level = i / 15.0F;
			float mix = 0.9F * (2.0F * level - 1.0F) * (2.0F * level - 1.0F);
			curve[i] = tint + (1.0F - tint) * mix;
		}
		return curve;
	}

	public static float ambientScale(float ambientLight) {
		return 1.0F / (1.0F - Math.min(ambientLight, 0.99F));
	}
}
