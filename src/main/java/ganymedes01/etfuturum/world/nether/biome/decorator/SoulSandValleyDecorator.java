package ganymedes01.etfuturum.world.nether.biome.decorator;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import ganymedes01.etfuturum.world.generate.feature.WorldGenBasaltPillars;
import ganymedes01.etfuturum.world.generate.feature.WorldGenNetherFossil;

public class SoulSandValleyDecorator extends NetherBiomeDecorator {

    private final WorldGenerator crimsonGrassGenerator;
    private WorldGenerator netherFossilGenerator;
    private final WorldGenerator pillarGenerator;

    public SoulSandValleyDecorator() {
        crimsonGrassGenerator = new WorldGenFlowers(ModBlocks.NETHER_ROOTS.get());
        pillarGenerator = new WorldGenBasaltPillars();
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        if (ModBlocks.NETHER_ROOTS.isEnabled() && rand.nextInt(3) == 0) {
            int x = chunkX + rand.nextInt(16) + 8;
            int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 8);
            int z = chunkZ + rand.nextInt(16) + 8;

            crimsonGrassGenerator.generate(world, rand, x, y, z);
        }
    }

    @Override
    public void decorate(World world, Random rand, int chunkX, int chunkZ) {
        if (netherFossilGenerator == null && ConfigWorld.fossilBlock != null) {
            netherFossilGenerator = new WorldGenNetherFossil();
        }

        if (ConfigWorld.fossilBlock != null) {
            for (int attempt = 0; attempt < 3; attempt++) {
                int x = chunkX + rand.nextInt(16) + 8;
                int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 8);
                int z = chunkZ + rand.nextInt(16) + 8;

                netherFossilGenerator.generate(world, rand, x, y, z);
            }
        }

        if (ModBlocks.BASALT.isEnabled() && rand.nextInt(6) == 0) {
            int x = chunkX + rand.nextInt(16) + 8;
            int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 8);
            int z = chunkZ + rand.nextInt(16) + 8;

            while (y++ <= world.provider.getActualHeight() - 8) {
                if (!world.isAirBlock(x, y, z)) {
                    break;
                }
            }

            pillarGenerator.generate(world, rand, x, y - 1, z);
        }
    }
}
