package ganymedes01.etfuturum.world.end;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenSpikes;

import ganymedes01.etfuturum.compat.ModsList;

public class BiomeDecoratorEFREnd extends BiomeEndDecorator {

    public BiomeDecoratorEFREnd() {
        this.spikeGen = new WorldGenSpikes(Blocks.end_stone);
    }

    @Override
    protected void genDecorations(BiomeGenBase p_150513_1_) {
        this.generateOres();

        if (!ModsList.HARDCORE_ENDER_EXPANSION.isLoaded()) {
            if (Math.abs(chunk_X) < 256 && Math.abs(chunk_Z) < 256 && this.randomGenerator.nextInt(5) == 0) {
                int i = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int j = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                int k = this.currentWorld.getTopSolidOrLiquidBlock(i, j);
                this.spikeGen.generate(this.currentWorld, this.randomGenerator, i, k, j);
            }

            if (this.chunk_X == 0 && this.chunk_Z == 0) {
                EntityDragon entitydragon = new EntityDragon(this.currentWorld);
                entitydragon.setLocationAndAngles(0.0D, 128.0D, 0.0D, this.randomGenerator.nextFloat() * 360.0F, 0.0F);
                this.currentWorld.spawnEntityInWorld(entitydragon);
            }
        }
    }
}
