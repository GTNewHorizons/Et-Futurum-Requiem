package ganymedes01.etfuturum.world.nether.biome.decorator;

import java.util.Random;

import net.minecraft.world.World;

public abstract class NetherBiomeDecorator {

    public abstract void populate(World world, Random rand, int chunkX, int chunkZ);

    public abstract void decorate(World world, Random rand, int chunkX, int chunkZ);
}
