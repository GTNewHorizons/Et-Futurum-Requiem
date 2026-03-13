package ganymedes01.etfuturum.world.end.dimension;

import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

import ganymedes01.etfuturum.world.end.BiomeDecoratorEFREnd;

public class WorldProviderEFREnd extends WorldProviderEnd {

    @Override
    public void registerWorldChunkManager() {
        BiomeGenBase.sky.theBiomeDecorator = new BiomeDecoratorEFREnd();
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.sky, 0.0F);
        this.dimensionId = 1;
        this.hasNoSky = true;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        // if (WorldgenConfiguration.BigNether) {
        // return new MaxHeightNetherChunkProvider(this.worldObj, this.worldObj.getSeed());
        // } else {
        return new ChunkProviderEFREnd(this.worldObj, this.worldObj.getSeed());
        // }
    }

}
