package ganymedes01.etfuturum.compat;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.ArrayUtils;

public final class CompatBiomesOPlenty {

	// Vanilla biome ID slots replaced by BOP: 21 Jungle, 22 JungleHills, 23 JungleEdge,
	// 149 Jungle M, 151 JungleEdge M. Vanilla 1.7.10 has no JungleHills mutation at ID 150.
	private static final int[] OVERRIDDEN_JUNGLE_IDS = {21, 22, 23, 149, 151};
	private static final String BOP_BIOMES_CLASS = "biomesoplenty.api.content.BOPCBiomes";
	private static final String BOP_BAMBOO_FOREST_CLASS = "biomesoplenty.common.biome.overworld.BiomeGenBambooForest";

	private static BiomeGenBase bambooForestBiome;
	private static boolean bambooForestResolved;

	private CompatBiomesOPlenty() {
	}

	public static void registerOverriddenJungleBiomeTypes() {
		if (!ModsList.BIOMES_O_PLENTY.isLoaded()) {
			return;
		}

		for (int biomeID : OVERRIDDEN_JUNGLE_IDS) {
			BiomeGenBase biome = BiomeGenBase.getBiome(biomeID);
			if (biome != null
					&& !ArrayUtils.contains(BiomeDictionary.getBiomesForType(BiomeDictionary.Type.JUNGLE), biome)) {
				BiomeDictionary.registerBiomeType(biome, BiomeDictionary.Type.JUNGLE);
			}
		}
	}

	public static BiomeGenBase getBambooForestBiome() {
		if (!ModsList.BIOMES_O_PLENTY.isLoaded()) {
			return null;
		}
		if (!bambooForestResolved) {
			bambooForestBiome = resolveBambooForestBiome();
			bambooForestResolved = true;
		}
		return bambooForestBiome;
	}

	public static boolean isBambooForestBiome(BiomeGenBase biome) {
		return biome != null && biome == getBambooForestBiome();
	}

	private static BiomeGenBase resolveBambooForestBiome() {
		try {
			Object biome = Class.forName(BOP_BIOMES_CLASS).getField("bambooForest").get(null);
			if (biome instanceof BiomeGenBase) {
				return (BiomeGenBase) biome;
			}
		} catch (ReflectiveOperationException ignored) {
		}

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			if (biome != null && BOP_BAMBOO_FOREST_CLASS.equals(biome.getClass().getName())) {
				return biome;
			}
		}

		for (BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
			if (biome != null
					&& biome.getClass().getName().startsWith("biomesoplenty.")
					&& "Bamboo Forest".equalsIgnoreCase(biome.biomeName)) {
				return biome;
			}
		}
		return null;
	}
}
