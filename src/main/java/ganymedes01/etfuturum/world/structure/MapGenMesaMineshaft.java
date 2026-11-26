package ganymedes01.etfuturum.world.structure;

import net.minecraft.world.World;

import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.StructureMineshaftStart;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This still allows regular mineshafts to generate as well as Mesa ones.
 * We do this so mixins are not required for overriding normal Mineshafts
 *
 * @author roadhog360
 */
public class MapGenMesaMineshaft extends MapGenMineshaft {

	public boolean canLocateAt(World world, int chunkX, int chunkZ) {
		long seed = world.getSeed();
		this.rand.setSeed(seed);
		long xSeed = this.rand.nextLong();
		long zSeed = this.rand.nextLong();
		this.rand.setSeed(chunkX * xSeed ^ chunkZ * zSeed ^ seed);
		// MapGenStructure advances the chunk RNG before checking the spawn chance.
		this.rand.nextInt();
		return canSpawnStructureAtCoords(chunkX, chunkZ) && isMesa(world, chunkX, chunkZ);
	}

	private static boolean isMesa(World world, int chunkX, int chunkZ) {
		return ArrayUtils.contains(BiomeDictionary.getTypesForBiome(world.getBiomeGenForCoords((chunkX << 4) + 8, (chunkZ << 4) + 8)), Type.MESA);
	}

	@Override
	protected StructureStart getStructureStart(int p_75049_1_, int p_75049_2_) {
		if (isMesa(this.worldObj, p_75049_1_, p_75049_2_)) {
			return new StructureMesaMineshaftStart(this.worldObj, this.rand, p_75049_1_, p_75049_2_);
		}
		return new StructureMineshaftStart(this.worldObj, this.rand, p_75049_1_, p_75049_2_);
	}
}
