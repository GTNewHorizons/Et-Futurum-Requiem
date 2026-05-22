package ganymedes01.etfuturum.world.end.gen;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.Logger;
import ganymedes01.etfuturum.world.end.dimension.ChunkProviderEFREnd;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import java.util.*;

/**
 * End city structure generator for the outer End islands.
 * Uses vanilla spacing/separation algorithm to determine valid generation positions,
 * then builds the full End city during chunk population.
 * <p>
 * This does NOT extend MapGenStructure for simplicity — instead it uses a direct
 * spacing check similar to how OceanMonument works in EFR. Structures are placed
 * entirely during populate() with no cross-chunk persistence.
 */
public class MapGenEndCity {

	// Vanilla End city spacing parameters
	private static final int SPACING = 20;
	private static final int SEPARATION = 11;
	private static final int SALT = 10387313;

	private final ChunkProviderEFREnd chunkProvider;

	public MapGenEndCity(ChunkProviderEFREnd chunkProvider) {
		this.chunkProvider = chunkProvider;
	}

	/**
	 * Check if an End city should generate at the given chunk coordinates and generate it if so.
	 * Called during populate().
	 */
	public void generateIfValid(World world, Random populateRand, int chunkX, int chunkZ) {
		if (!canSpawnStructureAtCoords(world, chunkX, chunkZ)) return;

		int x = chunkX * 16 + 8;
		int z = chunkZ * 16 + 8;

		// Check terrain height — need solid ground at reasonable height
		int groundHeight = getGroundHeight(world, x, z);
		if (groundHeight < 60) return;

		// Determine rotation (based on world seed + position for determinism)
		Random structRand = new Random(
				(long) chunkX * 341873128712L + (long) chunkZ * 132897987541L + world.getSeed() + SALT);
		int rotation = structRand.nextInt(4);

		BlockPos startPos = new BlockPos(x, groundHeight, z);

		try {
			// Generate the piece tree
			List<EndCityPieces.CityTemplate> pieces = new ArrayList<>();
			EndCityPieces.startHouseTower(startPos, rotation, pieces, structRand);

			// Place all pieces in the world
			for (EndCityPieces.CityTemplate piece : pieces) {
				piece.placeInWorld(world, structRand, null);
			}

			Logger.info("Generated End city at " + x + ", " + groundHeight + ", " + z
					+ " with " + pieces.size() + " pieces");
		} catch (Exception e) {
			Logger.error("Failed to generate End city at " + x + ", " + groundHeight + ", " + z);
			e.printStackTrace();
		}
	}

	/**
	 * Vanilla spacing/separation algorithm for End city placement.
	 * Matches vanilla 1.10 MapGenEndCity.canSpawnStructureAtCoords().
	 */
	private boolean canSpawnStructureAtCoords(World world, int chunkX, int chunkZ) {
		int cx = chunkX;
		int cz = chunkZ;

		if (chunkX < 0) chunkX -= SPACING - 1;
		if (chunkZ < 0) chunkZ -= SPACING - 1;

		int gridX = chunkX / SPACING;
		int gridZ = chunkZ / SPACING;

		Random random = world.setRandomSeed(gridX, gridZ, SALT);

		gridX *= SPACING;
		gridZ *= SPACING;

		gridX += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;
		gridZ += (random.nextInt(SPACING - SEPARATION) + random.nextInt(SPACING - SEPARATION)) / 2;

		if (cx != gridX || cz != gridZ) return false;

		// Check if this is on a valid outer island
		return chunkProvider.isIslandChunk(cx, cz);
	}

	/**
	 * Sample terrain height by checking the highest solid block.
	 * Uses a 4-point sample similar to vanilla.
	 */
	private int getGroundHeight(World world, int x, int z) {
		int h1 = world.getTopSolidOrLiquidBlock(x, z);
		int h2 = world.getTopSolidOrLiquidBlock(x + 1, z);
		int h3 = world.getTopSolidOrLiquidBlock(x, z + 1);
		int h4 = world.getTopSolidOrLiquidBlock(x + 1, z + 1);

		// Take the minimum to ensure there's ground for the structure
		return Math.min(Math.min(h1, h2), Math.min(h3, h4));
	}
}
