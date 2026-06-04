package ganymedes01.etfuturum.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import roadhog360.hogutils.api.utils.FastRandom;

import java.util.Random;

public class CustomParticles {

	protected static Random rand = new FastRandom();

	public static EntityFX spawnInvisibleBlockParticle(World world, double x, double y, double z) {
		EntityFX particle = new InnerBlockParticleFX(world, x, y, z);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnCopperWaxOnParticle(World world, double x, double y, double z) {
		EntityFX particle = new CopperGlowFX(world, x, y, z, 0xFFFFA32C);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnCopperWaxOffParticle(World world, double x, double y, double z) {
		EntityFX particle = new CopperGlowFX(world, x, y, z, 0xFFFFFFFF);
		return spawnParticle(world, particle);

	}

	public static EntityFX spawnCopperScrapeParticle(World world, double x, double y, double z) {
		EntityFX particle = new CopperGlowFX(world, x, y, z, 0xFF78DAC1);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnBlackHeartParticle(World world, double x, double y, double z) {
		EntityFX particle = new BlackHeartFX(world, x, y, z);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnEndRodParticle(World world, double x, double y, double z) {
		EntityFX particle = new EndRodFX(world, x, y, z);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnFallingDustParticle(World world, double x, double y, double z, int color) {
		EntityFX particle = new FallingDustFX(world, x, y, z, color);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnDrippingParticle(World world, double x, double y, double z, int color) {
		return spawnDrippingParticleWithSound(world, x, y, z, null, color, false);
	}

	public static EntityFX spawnDrippingParticle(World world, double x, double y, double z, int color, boolean splashes) {
		return spawnDrippingParticleWithSound(world, x, y, z, null, color, splashes);
	}

	public static EntityFX spawnDrippingParticleWithSound(World world, double x, double y, double z, String sound, int color) {
		return spawnDrippingParticleWithSound(world, x, y, z, sound, color, false);
	}

	public static EntityFX spawnDrippingParticleWithSound(World world, double x, double y, double z, String sound, int color, boolean splashes) {
		EntityFX particle = new CustomDripFX(world, x, y, z, sound, color, splashes);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnBeeNectarParticle(World world, double x, double y, double z) {
		EntityFX particle = new BeeNectarFX(world, x, y, z);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnCrimsonSpore(World world, double x, double y, double z) {
		double d0 = rand.nextFloat() * 0.5D * rand.nextFloat() * 0.1D * 5.0D;
		EntityFX particle = new BiomeFX(world, x, y, z, rand.nextGaussian() * 0.05D, d0, rand.nextGaussian() * 0.05D, 0.02F);
		particle.setRBGColorF(0.9F, 0.4F, 0.5F);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnWarpedSpore(World world, double x, double y, double z) {
		double d0 = rand.nextFloat() * 0.5D * rand.nextFloat() * 0.1D * 5.0D;
		EntityFX particle = new BiomeFX(world, x, y, z, rand.nextGaussian() * 0.05D, d0, rand.nextGaussian() * 0.05D, 0.001F);
		particle.setRBGColorF(0.1F, 0.1F, 0.3F);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnAshParticle(World world, double x, double y, double z) {
		double d0 = rand.nextFloat() * -1.9D * rand.nextFloat() * 0.1D;
		double d1 = rand.nextFloat() * -0.5D * rand.nextFloat() * 0.1D * 5.0D;
		double d2 = rand.nextFloat() * -1.9D * rand.nextFloat() * 0.1D;
		EntityFX particle = new BiomeFX(world, x, y, z, d0, d1, d2, (float) MathHelper.getRandomDoubleInRange(rand, 0.001D, 0.02D));
		particle.setRBGColorF(0.7294118F, 0.69411767F, 0.7607843F);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnCherryLeaf(World world, double x, double y, double z) {
		EntityFX particle = new CherryLeafFX(world, x, y, z);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnSoulFlame(World world, double x, double y, double z, double mX, double mY, double mZ) {
		EntityFX particle = new SoulFlameFX(world, x, y, z, mX, mY, mZ);
		return spawnParticle(world, particle);
	}

	public static EntityFX spawnSoulFlame(World world, double x, double y, double z) {
		EntityFX particle = new SoulFlameFX(world, x, y, z);
		return spawnParticle(world, particle);
	}

	protected static EntityFX spawnParticle(World world, EntityFX entityFX) {
		if (world.isRemote) {
			Minecraft.getMinecraft().effectRenderer.addEffect(entityFX);
			return entityFX;
		}
		return null;
	}
}
