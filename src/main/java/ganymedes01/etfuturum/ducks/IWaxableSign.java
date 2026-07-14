package ganymedes01.etfuturum.ducks;

import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.world.World;

import java.util.Random;

public interface IWaxableSign {
	boolean isWaxed();
	void setWaxed(boolean waxed);

	default void playWaxOnSound(World world, int x, int y, int z) {
        if (world.isRemote) {
            int pitch = world.rand.nextInt(3);
            world.playSound(x + 0.5D, y + 0.5D, z + 0.5D,
                    Reference.MCAssetVer + ":item.honeycomb.wax_on", 1F,
                    (float) ((pitch == 0 ? 0 : ((double) pitch / 10D)) + 0.9D), false);
        }
	}

	default void spawnWaxOnEffects(World world, int x, int y, int z) {
		if (world.isRemote) {
            playWaxOnSound(world, x, y, z);
			Random random = world.rand;
			for (int i = 0; i < 10; ++i) {
				double px = x + random.nextFloat();
				double py = y + random.nextFloat();
				double pz = z + random.nextFloat();
				CustomParticles.spawnCopperWaxOnParticle(world, px, py, pz);
			}
		}
	}
}
