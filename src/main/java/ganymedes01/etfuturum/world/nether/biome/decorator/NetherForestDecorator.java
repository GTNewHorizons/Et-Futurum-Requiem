package ganymedes01.etfuturum.world.nether.biome.decorator;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.blocks.BlockNylium;

import ganymedes01.etfuturum.world.generate.decorate.WorldGenBlockSplatter;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenHugeFungus;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenNetherGrass;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenTwistingVines;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenWeepingVines;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class NetherForestDecorator extends NetherBiomeDecorator {

	private final WorldGenHugeFungus hugeFungusGenerator;
	private final WorldGenNetherGrass grassGenerator;
	private final WorldGenerator vineGenerator;
	private final WorldGenerator wartSplatterGenerator;
	private final WorldGenerator netherrackSplatterGenerator;

	private NetherForestDecorator(boolean crimson) {
		if (crimson) {
			hugeFungusGenerator = new WorldGenHugeFungus(false, 0, 0, ModBlocks.CRIMSON_STEM.get(), ModBlocks.NETHER_WART.get());
			grassGenerator = new WorldGenNetherGrass(true);
			vineGenerator = new WorldGenWeepingVines();
			wartSplatterGenerator = new WorldGenBlockSplatter(ModBlocks.NETHER_WART.get(), 0, ModBlocks.NYLIUM.get(), 0, 4);
			netherrackSplatterGenerator = new WorldGenBlockSplatter(Blocks.netherrack, 0, ModBlocks.NYLIUM.get(), 0, 4);
		} else {
			hugeFungusGenerator = new WorldGenHugeFungus(false, 0, 1, ModBlocks.WARPED_STEM.get(), ModBlocks.NETHER_WART.get());
			grassGenerator = new WorldGenNetherGrass(false);
			vineGenerator = new WorldGenTwistingVines();
			wartSplatterGenerator = new WorldGenBlockSplatter(ModBlocks.NETHER_WART.get(), 1, ModBlocks.NYLIUM.get(), 1, 6);
			netherrackSplatterGenerator = new WorldGenBlockSplatter(Blocks.netherrack, 0, ModBlocks.NYLIUM.get(), 1, 6);
		}
	}

	public static NetherForestDecorator newWarpedForestDecorator() {
		return new NetherForestDecorator(false);
	}

	public static NetherForestDecorator newCrimsonForestDecorator() {
		return new NetherForestDecorator(true);
	}

	@Override
	public void populate(World world, Random rand, int chunkX, int chunkZ) {
	}

	@Override
	public void decorate(World world, Random rand, int chunkX, int chunkZ) {
		{
			int x = chunkX + rand.nextInt(16) + 8;
			int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 8);
			int z = chunkZ + rand.nextInt(16) + 8;

			if (wartSplatterGenerator != null && rand.nextBoolean()) {
				wartSplatterGenerator.generate(world, rand, x, y, z);
			} else {
				netherrackSplatterGenerator.generate(world, rand, x, y, z);
			}
		}

		for (int attempt = 0; attempt < 12; attempt++) {
			int x = chunkX + rand.nextInt(16) + 8;
			int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 24);
			int z = chunkZ + rand.nextInt(16) + 8;

			while (y-- > 32) {
				if (!world.isAirBlock(x, y, z)) {
					break;
				}
			}

			if (world.getBlock(x, y, z) instanceof BlockNylium && world.getBlockMetadata(x, y, z) == (hugeFungusGenerator.log == ModBlocks.CRIMSON_STEM.get() ? 0 : 1)
					&& world.isAirBlock(x, y + 1, z)) {
				if (hugeFungusGenerator.generate(world, rand, x, y + 1, z)) {
					hugeFungusGenerator.func_150524_b(world, rand, x, y + 1, z);
				}
			}
		}

		for (int attempt = 0; attempt < 48; attempt++) {
			int x = chunkX + rand.nextInt(16) + 8;
			int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 8);
			int z = chunkZ + rand.nextInt(16) + 8;

			vineGenerator.generate(world, rand, x, y, z);
		}

		for (int attempt = 0; attempt < 12; attempt++) {
			int x = chunkX + rand.nextInt(16) + 8;
			int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 8);
			int z = chunkZ + rand.nextInt(16) + 8;

			grassGenerator.generate(world, rand, x, y, z);
		}
	}
}