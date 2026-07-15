package ganymedes01.etfuturum.ducks;

import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public interface IWaxableSign {
	boolean isWaxed();
	void setWaxed(boolean waxed);

	String[] getSignText(boolean front);

	default boolean isWallSign(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block instanceof BlockWoodSign) {
			return !((BlockWoodSign) block).standing;
		} else {
			return block == Blocks.wall_sign;
		}
	}

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

			boolean isWall = isWallSign(world, x, y, z);
			int totalParticles = isWall ? 5 : 10;
			for (int i = 0; i < totalParticles; ++i) {
				double px = x + random.nextFloat();
				double py = y + random.nextFloat();
				double pz = z + random.nextFloat();
				// Offset so particles spawn on the face of the sign
				if (isWall) {
					double randWallDistance = random.nextFloat() * 0.125D;
					switch (world.getBlockMetadata(x, y, z)) {
						case 2: pz = z + 1.0D - randWallDistance; break;  // south
						case 3: pz = z + randWallDistance; break;         // north
						case 4: px = x + 1.0D - randWallDistance; break;  // east
						case 5: px = x + randWallDistance; break;         // west
					}
				}
				else {
					py = y + 0.5D + random.nextFloat() / 2D;
				}

				CustomParticles.spawnCopperWaxOnParticle(world, px, py, pz);
			}
		}
	}
}
