package ganymedes01.etfuturum.world.nether.biome;

import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.world.nether.biome.decorator.BasaltDeltasDecorator;

public class BiomeBasaltDeltas extends NetherBiomeBase {

    public BiomeBasaltDeltas(int id) {
        super(id, new BasaltDeltasDecorator());
        fogSkyColor = 0x534868;
        topBlock = ModBlocks.BLACKSTONE.get();
        fillerBlock = ModBlocks.BASALT.get();

        spawnableMonsterList.clear();
        this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 40, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityMagmaCube.class, 100, 2, 5));
    }
}
