package ganymedes01.etfuturum.ducks;

import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import java.util.Random;

public interface ISign {
	boolean isWaxed();
	void setWaxed(boolean waxed);

	/** Get the dye color ID (0-15), or -1 if not dyed */
	int getDyeId();
	void setDyeId(int dyeId);

	String[] getSignText(boolean front);

	/** Map dye metadata (0-15) to the closest § color code */
	String[] DYE_COLOR_CODES = {
		"\u00A7f", // 0: white
		"\u00A76", // 1: orange -> gold
		"\u00A7d", // 2: magenta -> light purple
		"\u00A7b", // 3: light_blue -> aqua
		"\u00A7e", // 4: yellow
		"\u00A7a", // 5: lime -> green
		"\u00A7d", // 6: pink -> light purple
		"\u00A78", // 7: gray -> dark gray
		"\u00A77", // 8: light_gray -> gray
		"\u00A73", // 9: cyan -> dark aqua
		"\u00A75", // 10: purple -> dark purple
		"\u00A79", // 11: blue
		"\u00A74", // 12: brown -> dark red
		"\u00A72", // 13: green -> dark green
		"\u00A7c", // 14: red
		"\u00A70", // 15: black
	};

	// Adds dye color as formatting code to text, as well as handling §r (reset)
	default String applyDyeBaseColor(String text) {
		int dyeId = getDyeId();
		if (dyeId < 0 || dyeId > 15) return text;
		String code = DYE_COLOR_CODES[dyeId];
		return code + text.replace("\u00A7r", "\u00A7r" + code);
	}

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
