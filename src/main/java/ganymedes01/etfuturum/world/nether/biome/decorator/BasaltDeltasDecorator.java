package ganymedes01.etfuturum.world.nether.biome.decorator;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.world.generate.feature.WorldGenBasaltColumns;
import ganymedes01.etfuturum.world.generate.feature.WorldGenDeltas;
import ganymedes01.etfuturum.world.nether.biome.utils.NetherBiomeManager;

public class BasaltDeltasDecorator extends NetherBiomeDecorator {

    private final WorldGenerator deltaGenerator;
    private final WorldGenerator smallBasaltColumnGenerator;
    private final WorldGenerator largeBasaltColumnGenerator;

    public BasaltDeltasDecorator() {
        deltaGenerator = new WorldGenDeltas();
        smallBasaltColumnGenerator = new WorldGenBasaltColumns(1, 4, 1, 1);
        largeBasaltColumnGenerator = new WorldGenBasaltColumns(5, 10, 2, 3);
    }

    @Override
    public void populate(World world, Random rand, int chunkX, int chunkZ) {
        for (int attempt = 0; attempt < 15; attempt++) {
            int x = chunkX + rand.nextInt(16) + 8;
            int y = MathHelper.getRandomIntegerInRange(rand, 32, world.provider.getActualHeight() - 24);
            int z = chunkZ + rand.nextInt(16) + 8;

            while (y-- > 32) {
                if (!world.isAirBlock(x, y, z)) {
                    break;
                }
            }

            deltaGenerator.generate(world, rand, x, y, z);
        }

        for (int segment = 0; segment < 5; segment++) {
            for (int attempt = 0; attempt < 12; attempt++) {
                int x = chunkX + rand.nextInt(16) + 8;
                int y = MathHelper.getRandomIntegerInRange(rand, 32 + (segment * 18), 54 + (segment * 18));
                int z = chunkZ + rand.nextInt(16) + 8;

                if (world.getBiomeGenForCoords(x, z) != NetherBiomeManager.basaltDeltas) continue;

                while (y-- > 32) {
                    if (!world.isAirBlock(x, y - 1, z)) {
                        break;
                    }
                }

                boolean useSmallGen = rand.nextInt(4) == 0
                    || world.getBiomeGenForCoords(x + 16, z) != NetherBiomeManager.basaltDeltas
                    || world.getBiomeGenForCoords(x, z + 16) != NetherBiomeManager.basaltDeltas
                    || world.getBiomeGenForCoords(x + 16, z + 16) != NetherBiomeManager.basaltDeltas
                    || world.getBiomeGenForCoords(x - 16, z) != NetherBiomeManager.basaltDeltas
                    || world.getBiomeGenForCoords(x, z - 16) != NetherBiomeManager.basaltDeltas
                    || world.getBiomeGenForCoords(x - 16, z - 16) != NetherBiomeManager.basaltDeltas;

                if (world.getBlock(x, y - 1, z) != ModBlocks.BASALT.get()) {
                    (useSmallGen ? smallBasaltColumnGenerator : largeBasaltColumnGenerator)
                        .generate(world, rand, x, y, z);
                }
            }
        }
    }

    @Override
    public void decorate(World world, Random rand, int chunkX, int chunkZ) {}
}
