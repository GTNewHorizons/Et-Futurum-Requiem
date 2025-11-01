package ganymedes01.etfuturum.client.particle;

import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.world.World;
import roadhog360.hogutils.api.utils.FastRandom;

public class BiomeFX extends EntityAuraFX {
	public BiomeFX(World world, double x, double y, double z, double vX, double vY, double vZ, float size) {
		super(world, x, y, z, 0, 0, 0);
		rand = new FastRandom();
		particleMaxAge = (int) (16D / (CustomParticles.rand.nextFloat() * 0.8D + 0.2D));
		particleGravity = 0.01F;
		motionX = vX;
		motionY = vY;
		motionZ = vZ;
		setSize(size, size);
	}
}
