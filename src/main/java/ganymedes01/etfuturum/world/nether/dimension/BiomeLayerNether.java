package ganymedes01.etfuturum.world.nether.dimension;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

import ganymedes01.etfuturum.world.nether.biome.utils.BiomeLayerNetherBiomes;

public abstract class BiomeLayerNether extends GenLayer {

    public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldtype, int dim) {
        int biomesize = 4; // Biome size variable

        BiomeLayerNether obj = new BiomeLayerNetherCreate(1L, false);
        obj = new BiomeLayerNetherFuzzyZoom(2000L, (obj));
        for (int i = 1; i < 3; i++) {
            obj = new BiomeLayerNetherlZoom(2000L + i, (obj));
        }
        obj = BiomeLayerNetherlZoom.magnify(1000L, ((obj)), 0);
        obj = new BiomeLayerNetherBiomes(200L, ((obj)));
        obj = BiomeLayerNetherlZoom.magnify(1000L, ((obj)), 2);
        for (int j = 0; j < biomesize; j++) {
            obj = new BiomeLayerNetherlZoom(1000L + j, (obj));
        }
        BiomeLayerNetherVoronoiZoom genlayervoronoizoom = new BiomeLayerNetherVoronoiZoom(10L, ((obj)));
        (obj).initWorldGenSeed(seed);
        genlayervoronoizoom.initWorldGenSeed(seed);
        return (new GenLayer[] { obj, genlayervoronoizoom });
    }

    public BiomeLayerNether(long seed) {
        super(seed);
    }

    public static byte getModdedBiomeSize(WorldType worldType, byte original) {
        WorldTypeEvent.BiomeSize event = new WorldTypeEvent.BiomeSize(worldType, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.newSize;
    }
}
