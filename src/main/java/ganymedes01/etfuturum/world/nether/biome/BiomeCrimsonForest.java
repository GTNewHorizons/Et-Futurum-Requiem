package ganymedes01.etfuturum.world.nether.biome;

import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Blocks;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.world.nether.biome.decorator.NetherForestDecorator;

public class BiomeCrimsonForest extends NetherBiomeBase {

    public BiomeCrimsonForest(int id) {
        super(id, NetherForestDecorator.newCrimsonForestDecorator());
        this.setColor(0xFA9418);
        fogSkyColor = 0xAD0808;
        this.topBlock = ModBlocks.NYLIUM.get();
        this.fillerBlock = Blocks.netherrack;

        spawnableMonsterList.clear();
        this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 1, 2, 4));
    }
}
