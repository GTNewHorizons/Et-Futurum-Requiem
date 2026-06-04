package ganymedes01.etfuturum.world.generate.feature;

import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class WorldGenDeepslateLayer extends WorldGenerator {
	private final Block block;
	private final int meta;

	public WorldGenDeepslateLayer(Block block, int meta) {
		this.block = block;
		this.meta = meta;
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z) {
		return generate(world, random, x, y, z, ConfigWorld.deepslateMaxY);
	}

	public boolean generate(World world, Random random, int x, int y, int z, int height) {
		if(DeepslateOreRegistry.doesChunkSupportLayerDeepslate(world.getChunkProvider(), world.provider.dimensionId)) {
			Chunk chunk = world.getChunkFromBlockCoords(x, z);
			for (int yPos = 0; yPos <= Math.min(height, world.provider.getHeight()); yPos++) {
				for (int xPos = 0; xPos < 16; xPos++) {
					for (int zPos = 0; zPos < 16; zPos++) {
						Block currentBlock = chunk.getBlock(xPos, yPos, zPos);
						if ((height >= world.provider.getHeight() || yPos < height - 4 || yPos <= height - chunk.worldObj.rand.nextInt(4))) {
							DeepslateOreRegistry.setBlockDeepslate(chunk, xPos, yPos, zPos, block, meta);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

}
