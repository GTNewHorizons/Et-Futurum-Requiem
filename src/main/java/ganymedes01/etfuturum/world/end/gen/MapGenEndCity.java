package ganymedes01.etfuturum.world.end.gen;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.world.end.dimension.ChunkProviderEFREnd;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;

import java.util.Random;

/**
 * End city structure generator for the outer End islands.
 * Uses vanilla MapGenStructure for chunk boundary safety, bounding box clipping,
 * and persistence.
 */
public class MapGenEndCity extends MapGenStructure {

	// Vanilla End city spacing parameters
	private static final int SPACING = 20;
	private static final int SEPARATION = 11;
	private static final int SALT = 10387313;

	private final ChunkProviderEFREnd chunkProvider;

	public MapGenEndCity(ChunkProviderEFREnd chunkProvider) {
		this.chunkProvider = chunkProvider;
	}

	@Override
	public String func_143025_a() {
		return "EndCity";
	}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		int cx = chunkX;
		int cz = chunkZ;

		if (chunkX < 0) chunkX -= SPACING - 1;
		if (chunkZ < 0) chunkZ -= SPACING - 1;

		int gridX = chunkX / SPACING;
		int gridZ = chunkZ / SPACING;

		Random random = this.worldObj.setRandomSeed(gridX, gridZ, SALT);

		gridX *= SPACING;
		gridZ *= SPACING;

		gridX += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;
		gridZ += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;

		if (cx != gridX || cz != gridZ) return false;

		// Check if this is on a valid outer island
		if (!chunkProvider.isIslandChunk(cx, cz)) return false;

		int x = cx * 16 + 8;
		int z = cz * 16 + 8;
		int groundHeight = getGroundHeight(this.worldObj, x, z);
		
		return groundHeight >= 60;
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ) {
		return new Start(this.worldObj, chunkProvider, this.rand, chunkX, chunkZ);
	}

	/**
	 * Approximate terrain height to avoid recursive chunk loading during generation.
	 * Outer End islands generate around Y=60.
	 */
	public static int getGroundHeight(World world, int x, int z) {
		return 60;
	}

	public static class Start extends StructureStart {
		
		private boolean isCreated;

		public Start() {}

		public Start(World world, ChunkProviderEFREnd provider, Random rand, int chunkX, int chunkZ) {
			super(chunkX, chunkZ);
			
			int x = chunkX * 16 + 8;
			int z = chunkZ * 16 + 8;

			int groundHeight = getGroundHeight(world, x, z);
			if (groundHeight < 60) return;

			// Determine rotation (based on world seed + position for determinism)
			Random structRand = new Random(
					(long) chunkX * 341873128712L + (long) chunkZ * 132897987541L + world.getSeed() + SALT);
			int rotation = structRand.nextInt(4);

			BlockPos startPos = new BlockPos(x, groundHeight, z);

			EndCityPieces.startHouseTower(startPos, rotation, this.components, structRand);

			this.updateBoundingBox();
			this.isCreated = true;
		}

		@Override
		public boolean isSizeableStructure() {
			return this.isCreated;
		}
	}
}
