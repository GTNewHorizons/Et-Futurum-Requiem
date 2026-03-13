package ganymedes01.etfuturum.world.nether.biome;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import ganymedes01.etfuturum.world.nether.biome.decorator.NetherBiomeDecorator;

public class NetherBiomeBase extends BiomeGenBase {

    public int fogSkyColor;
    private final NetherBiomeDecorator decorator;

    public NetherBiomeBase(int id, NetherBiomeDecorator decorator) {
        super(id);
        this.decorator = decorator;
        this.setDisableRain();
        this.setTemperatureRainfall(2.0F, 0.0F);

        field_150604_aj/* topBlockMetadata */ = field_76754_C/* fillerBlockMetadata */ = 0;

        BiomeDictionary.registerBiomeType(this, BiomeDictionary.Type.NETHER);

        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.addAll(BiomeGenBase.hell.getSpawnableList(EnumCreatureType.monster));
    }

    public void populate(World world, Random rand, int x, int z) {
        decorator.populate(world, rand, x, z);
    }

    @Override
    public void decorate(World world, Random rand, int x, int z) {
        decorator.decorate(world, rand, x, z);
    }

    @Override
    public int getSkyColorByTemp(float par1) {
        return this.fogSkyColor;
    }

    protected void removeMonster(Class<? extends Entity> entity) {
        spawnableMonsterList.removeIf(entry -> entry.entityClass == entity);
    }
}
