package ganymedes01.etfuturum.world;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenMinable;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Maps;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.DeepslateOreRegistry;
import ganymedes01.etfuturum.api.mappings.RegistryMapping;
import ganymedes01.etfuturum.configuration.configs.ConfigTweaks;
import ganymedes01.etfuturum.configuration.configs.ConfigWorld;

public class EtFuturumLateWorldGenerator extends EtFuturumWorldGenerator {

    public static final EtFuturumLateWorldGenerator INSTANCE = new EtFuturumLateWorldGenerator();

    public static final Map<Integer, Map<Long, List<Integer>>> deepslateRedoCache = Maps.newConcurrentMap();

    protected EtFuturumLateWorldGenerator() {
        super();
        if (ModBlocks.DEEPSLATE.isEnabled()) {
            if (ConfigWorld.deepslateReplacesDirt) {
                DeepslateOreRegistry.addOre(Blocks.dirt, ModBlocks.DEEPSLATE.get());
            }
            if (ConfigTweaks.deepslateReplacesCobblestone) {
                DeepslateOreRegistry.addOre(Blocks.cobblestone, ModBlocks.COBBLED_DEEPSLATE.get());
            }
        }
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
        IChunkProvider chunkProvider) {
        Chunk chunk = null;
        // Used to check if we're generating in a flat world
        if (doesChunkSupportLayerDeepslate(chunkGenerator, world.provider.dimensionId)) {
            // Turn off recording here so the world gen isn't an infinite recursion loop of constantly checking the same
            // blocks
            stopRecording = true;
            Map<Long, List<Integer>> map = deepslateRedoCache.remove(world.provider.dimensionId);
            if (map != null) {
                for (Entry<Long, List<Integer>> set : map.entrySet()) {
                    List<Integer> posSet = set.getValue();

                    if (posSet != null) {

                        long packedChunkCoords = set.getKey();
                        int redoX = (int) packedChunkCoords;
                        int redoZ = (int) (packedChunkCoords >> 32);

                        Chunk cachedChunk = world.getChunkFromChunkCoords(redoX, redoZ);
                        for (int pos : posSet) {
                            byte posX = (byte) ((pos >> 12));
                            short posY = (short) ((pos >> 4 & 0xFF));
                            byte posZ = (byte) ((pos & 0xF));

                            ExtendedBlockStorage array = cachedChunk.getBlockStorageArray()[posY >> 4];

                            if (array == null) continue;

                            replaceBlockInChunk(
                                cachedChunk.worldObj,
                                array.getBlockByExtId(posX, posY & 15, posZ),
                                posX,
                                posZ,
                                (redoX << 4) + posX,
                                posY,
                                (redoZ << 4) + posZ,
                                array);
                        }
                    }
                }
            }

            chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
            doDeepslateGen(chunk);

            stopRecording = false;
        }

        if (ModBlocks.COARSE_DIRT.isEnabled() && ConfigWorld.enableCoarseDirtReplacement) {
            if (chunk == null) {
                chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
            }
            doCoarseDirtGen(chunk);
        }

        if (world.getWorldInfo()
            .getTerrainType() != WorldType.FLAT
            || world.getWorldInfo()
                .getGeneratorOptions()
                .contains("decoration") && world.provider instanceof WorldProviderSurface) {
            if (ModBlocks.DEEPSLATE.isEnabled() && ConfigWorld.deepslateGenerationMode == 1) {
                generateOre(deepslateBlobGen, world, rand, chunkX, chunkZ, 1, 6, ConfigWorld.deepslateMaxY);
            }
            if (ModBlocks.TUFF.isEnabled()) {
                generateOre(tuffGen, world, rand, chunkX, chunkZ, 1, 6, ConfigWorld.deepslateMaxY);
            }
            if (ModBlocks.STONE.isEnabled() && ConfigWorld.maxStonesPerCluster > 0) {
                for (WorldGenMinable stoneGenerator : stoneGen) {
                    for (int i = 0; i < 10; i++) {
                        generateOre(stoneGenerator, world, rand, chunkX, chunkZ, 1, 0, 80);
                    }
                }
            }
        }
    }

    private void doCoarseDirtGen(Chunk chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < chunk.getHeightValue(x, z); y++) {
                    ExtendedBlockStorage array = chunk.getBlockStorageArray()[y >> 4];
                    if (array != null && array.getExtBlockMetadata(x, y & 15, z) == 1
                        && array.getBlockByExtId(x, y & 15, z) == Blocks.dirt) {
                        array.func_150818_a/* setExtBlockID */(x, y & 15, z, ModBlocks.COARSE_DIRT.get());
                        array.setExtBlockMetadata(x, y & 15, z, 0);
                    }
                }
            }
        }
    }

    private void doDeepslateGen(Chunk chunk) {

        int worldX;
        int worldZ;
        final int chunkMultiplierX = chunk.xPosition << 4;
        final int chunkMultiplierZ = chunk.zPosition << 4;

        boolean shouldDimBeJustDeepslate = ArrayUtils
            .contains(ConfigWorld.replaceAllStoneWithDeepslateDimensionWhitelist, chunk.worldObj.provider.dimensionId);
        int replaceUnderY = (shouldDimBeJustDeepslate ? chunk.worldObj.getActualHeight() : ConfigWorld.deepslateMaxY);
        for (int y = 0; y <= Math.min(replaceUnderY, chunk.worldObj.getHeight()); y++) {
            ExtendedBlockStorage array = chunk.getBlockStorageArray()[y >> 4];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (ConfigWorld.deepslateMaxY >= 255 || shouldDimBeJustDeepslate
                        || y < ConfigWorld.deepslateMaxY - 4
                        || y <= ConfigWorld.deepslateMaxY - chunk.worldObj.rand.nextInt(4)) {
                        worldX = x + chunkMultiplierX;
                        worldZ = z + chunkMultiplierZ;

                        if (array == null) continue;

                        this.replaceBlockInChunk(
                            chunk.worldObj,
                            array.getBlockByExtId(x, y & 15, z),
                            x,
                            z,
                            worldX,
                            y,
                            worldZ,
                            array);
                    }
                }
            }
        }
    }

    private void replaceBlockInChunk(World world, Block block, int x, int z, int worldX, int worldY, int worldZ,
        ExtendedBlockStorage array) {
        if ((ConfigWorld.deepslateReplacesStones || block != ModBlocks.STONE.get())
            && block.getMaterial() != Material.air
            && block != ModBlocks.TUFF.get()) {
            if (block.isReplaceableOreGen(world, worldX, worldY, worldZ, Blocks.stone)) {
                array.func_150818_a/* setExtBlockID */(x, worldY & 15, z, ModBlocks.DEEPSLATE.get());
                array.setExtBlockMetadata(x, worldY & 15, z, 0);
                return;
            }
            RegistryMapping<Block> mapping = DeepslateOreRegistry
                .getOre(block, array.getExtBlockMetadata(x, worldY & 15, z));
            if (mapping != null) {
                array.func_150818_a/* setExtBlockID */(x, worldY & 15, z, mapping.getObject());
                array.setExtBlockMetadata(x, worldY & 15, z, mapping.getMeta());
            }
        }
    }

    public static boolean stopRecording;

    private boolean doesChunkSupportLayerDeepslate(IChunkProvider provider, int dimId) {
        if (ModBlocks.DEEPSLATE.isEnabled()) {
            if (ConfigWorld.deepslateGenerationMode == 0 && ConfigWorld.deepslateMaxY > 0) {
                return !isFlatWorld(provider)
                    && ArrayUtils.contains(ConfigWorld.deepslateLayerDimensionBlacklist, dimId)
                        == ConfigWorld.deepslateLayerDimensionBlacklistAsWhitelist;
            }
        }
        return false;
    }

}
