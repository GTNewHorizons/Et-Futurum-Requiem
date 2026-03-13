package ganymedes01.etfuturum.world.nether.biome;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;

import ganymedes01.etfuturum.world.nether.biome.decorator.SoulSandValleyDecorator;

public class BiomeSoulSandValley extends NetherBiomeBase {

    public BiomeSoulSandValley(int id) {
        super(id, new SoulSandValleyDecorator());
        fogSkyColor = 0x00BACD;
        this.topBlock = Blocks.soul_sand;
        this.fillerBlock = Blocks.soul_sand;
        removeMonster(EntityPigZombie.class);
        removeMonster(EntityGhast.class);
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 20, 1, 5));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 50, 1, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 1, 1, 4));
    }

    @Override
    public float getSpawningChance() {
        return 0.075F;
    }
}
