package ganymedes01.etfuturum.world.generate;

import ganymedes01.etfuturum.world.WorldHeightHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;

public class ChunkProviderIncreasedHeight extends ChunkProviderGenerate {

    private Random rand;

    private NoiseGeneratorOctaves lowerNoiseGen;
    private NoiseGeneratorOctaves upperNoiseGen;
    private NoiseGeneratorOctaves mixedNoiseGen;
    private NoiseGeneratorPerlin field_147430_m;
    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves noiseGen5;
    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves depthNoiseGen;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    /** Reference to the World object. */
    private World worldObj;
    /** are map structures going to be generated (e.g. strongholds) */
    private final boolean mapFeaturesEnabled;
    private WorldType worldType;
    private final double[] densityField;
    private final float[] parabolicField;
    private double[] stoneNoise = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();
    /** Holds Stronghold Generator */
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();
    /** Holds Village Generator */
    private MapGenVillage villageGenerator = new MapGenVillage();
    /** Holds Mineshaft Generator */
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
    /** Holds ravine generator */
    private MapGenBase ravineGenerator = new MapGenRavine();
    /** The biomes that are used to generate the chunk */
    private BiomeGenBase[] biomesForGeneration;
    /** This field determines how the transition between lower and upper noise is handled **/
    double[] blendNoiseField;
    double[] lowerNoiseField;
    double[] upperNoiseField;
    /** This 2D noise field influences the approximate elevation of the terrain per x/z sample **/
    double[] depthNoiseField;
    int[][] field_73219_j = new int[32][32];

    {
        caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
        strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, STRONGHOLD);
        villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, VILLAGE);
        mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator, SCATTERED_FEATURE);
        ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
    }

    public ChunkProviderIncreasedHeight(World world, long seed, boolean mapFeatures) {

        super(world, seed, mapFeatures);

        this.worldObj = world;
        this.mapFeaturesEnabled = mapFeatures;
        this.worldType = world.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.lowerNoiseGen = new NoiseGeneratorOctaves(this.rand, 16);
        this.upperNoiseGen = new NoiseGeneratorOctaves(this.rand, 16);
        this.mixedNoiseGen = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.depthNoiseGen = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        // 5 x 5 x 65 = 1625
        this.densityField = new double[1625];
        this.parabolicField = new float[25];

        for (int j = -2; j <= 2; ++j)
        {
            for (int k = -2; k <= 2; ++k)
            {
                float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }

        NoiseGenerator[] noiseGens = {lowerNoiseGen, upperNoiseGen, mixedNoiseGen, field_147430_m, noiseGen5, depthNoiseGen, mobSpawnerNoise};
        noiseGens = TerrainGen.getModdedNoiseGenerators(world, this.rand, noiseGens);
        this.lowerNoiseGen = (NoiseGeneratorOctaves)noiseGens[0];
        this.upperNoiseGen = (NoiseGeneratorOctaves)noiseGens[1];
        this.mixedNoiseGen = (NoiseGeneratorOctaves)noiseGens[2];
        this.field_147430_m = (NoiseGeneratorPerlin)noiseGens[3];
        this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
        this.depthNoiseGen = (NoiseGeneratorOctaves)noiseGens[5];
        this.mobSpawnerNoise = (NoiseGeneratorOctaves)noiseGens[6];
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {

        this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

        //Block[] blocks = new Block[16 * 16 * WorldHeightHandler.getMaxWorldGenHeight()];
        //byte[] abyte = new byte[16 * 16 * WorldHeightHandler.getMaxWorldGenHeight()];
        Block[] blocks = new Block[65536];
        byte[] abyte = new byte[65536];

        this.generateBaseTerrain(chunkX, chunkZ, blocks);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
        this.replaceBlocksForBiome(chunkX, chunkZ, blocks, abyte, this.biomesForGeneration);
        this.caveGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
        this.ravineGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);

        if (this.mapFeaturesEnabled)
        {
            this.mineshaftGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
            this.villageGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
            this.strongholdGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
            this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, chunkX, chunkZ, blocks);
        }

        Chunk chunk = new Chunk(this.worldObj, blocks, abyte, chunkX, chunkZ);
        byte[] abyte1 = chunk.getBiomeArray();

        for (int k = 0; k < abyte1.length; ++k)
        {
            abyte1[k] = (byte)this.biomesForGeneration[k].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /*
    Generates a base terrain of stone.
    And oceans for every empty (Air) block below the sealevel.
     */
    protected void generateBaseTerrain(int chunkX, int chunkZ, Block[] blocks) {

        byte seaLevel = (byte) (63 + WorldHeightHandler.getWorldHeightOffset());
        //byte verticalCells = (byte) (WorldHeightHandler.getMaxWorldHeight() >> 3);
        byte verticalCells = 32;

        // Gets biomes for an area of 10x10 in a 4x4 downsampled resolution
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);

        this.generateDensityField(chunkX * 4, 0, chunkZ * 4);

        // Divides the chunk in 4x4 horizontal "cells"
        for (int cellX = 0; cellX < 4; ++cellX) {

            // 5 because there are exactly 4 gaps between 5 points
            int noiseX = cellX * 5;
            int noiseX1 = (cellX + 1) * 5;

            for (int cellZ = 0; cellZ < 4; ++cellZ) {

                // Four corner points in the coarse density field for this horizontal cell
                int columnx0z0 = (noiseX + cellZ) * (verticalCells + 1);
                int columnx0z1 = (noiseX + cellZ + 1) * (verticalCells + 1);
                int columnx1z0 = (noiseX1 + cellZ) * (verticalCells + 1);
                int columnx1z1 = (noiseX1 + cellZ + 1) * (verticalCells + 1);

                // The density field has 33 vertical points with 32 gaps and and every vertical cell has 8 blocks (256 / 8)
                for (int cellY = 0; cellY < verticalCells; ++cellY) {

                    // Vertical interpolation between two Y points
                    // 1 / 8 = 0.125 because one vertical cell has 8 blocks
                    double verticalInterpolation = 0.125D;

                    // Density values at the 4 lower corners of the current 3D cell
                    double density00 = this.densityField[columnx0z0 + cellY];
                    double density01 = this.densityField[columnx0z1 + cellY];
                    double density10 = this.densityField[columnx1z0 + cellY];
                    double density11 = this.densityField[columnx1z1 + cellY];

                    // How much each parameter changes per vertical substep (a vertical substep is one block)
                    double densityYStep00 = (this.densityField[columnx0z0 + cellY + 1] - density00) * verticalInterpolation;
                    double densityYStep01 = (this.densityField[columnx0z1 + cellY + 1] - density01) * verticalInterpolation;
                    double densityYStep10 = (this.densityField[columnx1z0 + cellY + 1] - density10) * verticalInterpolation;
                    double densitiyYStep11 = (this.densityField[columnx1z1 + cellY + 1] - density11) * verticalInterpolation;

                    // 8 vertical substeps per vertical cell
                    for (int subStepY = 0; subStepY < 8; ++subStepY) {

                        // Horizontal interpolation in x direction
                        // 1 / 4 = 0.25 because one horizontal cell has 4 blocks in x direction
                        double horizontalInterpolationX = 0.25D;

                        // Start values left/right for the y cell
                        double densityLeft = density00;
                        double densityRight = density01;

                        // How much each parameter changes per horizontal substep
                        double densityXStepLeft = (density10 - density00) * horizontalInterpolationX;
                        double densityXStepRight = (density11 - density01) * horizontalInterpolationX;

                        // 4 horizontal substeps per horizontal x cell
                        for (int subStepX = 0; subStepX < 4; ++subStepX) {

                            // Blockarray index for the starting point of the current Z column
                            //int blockIndex = ((cellX * 4 + subStepX) * 16 + (cellZ * 4)) * WorldHeightHandler.getMaxWorldGenHeight() + (cellY * 8 + subStepY);
                            int blockIndex = ((subStepX + cellX * 4) << 12) | ((cellZ * 4) << 8) | (cellY * 8 + subStepY);

                            // Step size for Z in the array
                            // If you increment z by 1, it advances by 256.
                            //short zStride = (short) (WorldHeightHandler.getMaxWorldGenHeight());
                            short zStride = 256;

                            // -zStride because in the inner loop it will be added first
                            blockIndex -= zStride;

                            // Horizontal interpolation in z direction
                            // 1 / 4 = 0.25 because one horizontal cell has 4 blocks in z direction
                            double horizontalInterpolationZ = 0.25D;

                            double densityZStep = (densityRight - densityLeft) * horizontalInterpolationZ;

                            // Set the starting value so that in the first loop iteration,
                            // the first valid point is generated immediately after the += operator.
                            double density = densityLeft - densityZStep;

                            // 4 horizontal substeps per horizontal z cell
                            for (int subStepZ = 0; subStepZ < 4; ++subStepZ) {

                                // Block has density = stone
                                if ((density += densityZStep) > 0.0D) {

                                    blocks[blockIndex += zStride] = Blocks.stone;

                                // No density (air) below sealevel = water
                                } else if (cellY * 8 + subStepY < seaLevel) {

                                    blocks[blockIndex += zStride] = Blocks.water;

                                // No density = air
                                } else {

                                    blocks[blockIndex += zStride] = null;
                                }
                            }

                            // For the next x step
                            densityLeft += densityXStepLeft;
                            densityRight += densityXStepRight;
                        }

                        // For the next vertical substep
                        density00 += densityYStep00;
                        density01 += densityYStep01;
                        density10 += densityYStep10;
                        density11 += densitiyYStep11;
                    }
                }
            }
        }
    }

    /*
    Calculates a coarse 3D density field for this chunk area
     */
    protected void generateDensityField(int xStart, int yStart, int zStart) {

        // A rough approximation of the local elevation. Not the actual mountain shpae, but rather an additional modulation
        this.depthNoiseField = this.depthNoiseGen.generateNoiseOctaves(this.depthNoiseField, xStart, zStart, 5, 5, 200.0D, 200.0D, 0.5D);
        // How much mix the lower and upper noise
        this.blendNoiseField = this.mixedNoiseGen.generateNoiseOctaves(this.blendNoiseField, xStart, yStart, zStart, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.lowerNoiseField = this.lowerNoiseGen.generateNoiseOctaves(this.lowerNoiseField, xStart, yStart, zStart, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.upperNoiseField = this.upperNoiseGen.generateNoiseOctaves(this.upperNoiseField, xStart, yStart, zStart, 5, 33, 5, 684.412D, 684.412D, 684.412D);

        // Linear index into the final 3D densityField
        int densityIndex = 0;
        // Linear index into the final 2D-depth-noise parabolicField
        int depthNoiseIndex = 0;
        // Base terrain level in the approximate sample area
        double baseTerrainLevel = 8.5D;

        // The horizontal density field has a size of 5x5
        for (int sampleX = 0; sampleX < 5; ++sampleX) {

            for (int sampleZ = 0; sampleZ < 5; ++sampleZ) {

                // These three variables calculate biome-weighted averages:
                float accumulatedHeightVariation = 0.0F;
                float accumulatedRootHeight = 0.0F;
                float accumulatedWeight = 0.0F;

                // Radius for biome-blending (-2 .. +2 = 5x5 area)
                byte biomeBlendRadius = 2;

                // The central biome for this sample
                BiomeGenBase centralBiome = this.biomesForGeneration[sampleX + 2 + (sampleZ + 2) * 10];

                for (int biomeOffsetX = -biomeBlendRadius; biomeOffsetX <= biomeBlendRadius; ++biomeOffsetX) {

                    for (int biomeOffsetZ = -biomeBlendRadius; biomeOffsetZ <= biomeBlendRadius; ++biomeOffsetZ) {

                        BiomeGenBase nearbyBiomes = this.biomesForGeneration[sampleX + biomeOffsetX + 2 + (sampleZ + biomeOffsetZ + 2) * 10];

                        // Coarse terrain base layer
                        float nearbyRootHeight = nearbyBiomes.rootHeight;
                        // How hilly or steep the terrain is
                        float nearbyHeightVariation = nearbyBiomes.heightVariation;

                        // WorldType Amplified makes height variations way more extreme
                        if (this.worldType == WorldType.AMPLIFIED && nearbyRootHeight > 0.0F) {

                            nearbyRootHeight = 1.0F + nearbyRootHeight * 2.0F;
                            nearbyHeightVariation = 1.0F + nearbyHeightVariation * 4.0F;
                        }
                        // Weighted values from parabolicField
                        // The center has more influence than more distant biomes
                        // The expression /(nearbyRootHeight + 2) slightly reduces the significance of tall biomes
                        float biomeWeight = this.parabolicField[biomeOffsetX + 2 + (biomeOffsetZ + 2) * 5] / (nearbyRootHeight + 2.0F);

                        // If the adjacent biome is higher than the center, its influence is halved.
                        // This prevents high biomes from having too strong an influence on lower ones.
                        if (nearbyBiomes.rootHeight > centralBiome.rootHeight) {

                            biomeWeight /= 2.0F;
                        }

                        accumulatedHeightVariation += nearbyHeightVariation * biomeWeight;
                        accumulatedRootHeight += nearbyRootHeight * biomeWeight;
                        accumulatedWeight += biomeWeight;
                    }
                }

                // Normalize weighted averages
                accumulatedHeightVariation /= accumulatedWeight;
                accumulatedRootHeight /= accumulatedWeight;
                // Height variation gets a bit smoothed out
                accumulatedHeightVariation = accumulatedHeightVariation * 0.9F + 0.1F;
                // Root height gets translated into the vertical logic of the terrain generator
                accumulatedRootHeight = (accumulatedRootHeight * 4.0F - 1.0F) / 8.0F;

                // The 2D-depth-noise-field adjusts the base height again
                double depthNoise = this.depthNoiseField[depthNoiseIndex] / 8000.0D;

                if (depthNoise < 0.0D) {

                    depthNoise = -depthNoise * 0.3D;
                }

                depthNoise = depthNoise * 3.0D - 2.0D;

                if (depthNoise < 0.0D) {

                    depthNoise /= 2.0D;

                    if (depthNoise < -1.0D) {

                        depthNoise = -1.0D;
                    }

                    depthNoise /= 1.4D;
                    depthNoise /= 2.0D;
                }
                else
                {
                    if (depthNoise > 1.0D)
                    {
                        depthNoise = 1.0D;
                    }

                    depthNoise /= 8.0D;
                }

                ++depthNoiseIndex;
                double blendedRootHeight = (double)accumulatedRootHeight;
                double blendedHeightVariation = (double)accumulatedHeightVariation;
                // Depth noise slightly affects the effective base level
                blendedRootHeight += depthNoise * 0.2D;
                // Some additional translation into terrain generator logic
                blendedRootHeight = blendedRootHeight * 8.5D / 8.0D;

                // This is the central vertical “target height” of the terrain in the coarse sample
                // I use this to adjust the terrain height to get more space for the deep caves
                double terrainCenterY = 8.5D + blendedRootHeight * 4.0D + (double) (WorldHeightHandler.getWorldHeightOffset() / 8);

                for (int sampleY = 0; sampleY < 33; ++sampleY) {

                    // This is the vertical distance from sampleY to the center of the terrain, scaled by the Height Variation.
                    // The larger it is, the more the density is pulled downward, meaning there is more air and less rock.
                    double verticalDistanceToTerrainCenter = ((double)sampleY - terrainCenterY) * 12.0D * 128.0D / 256.0D / blendedHeightVariation;

                    // Below the terrain center (negative values), the curve is amplified to make the ground appear more solid.
                    if (verticalDistanceToTerrainCenter < 0.0D) {

                        verticalDistanceToTerrainCenter *= 4.0D;
                    }

                    // The big 3D noise fields
                    double lowerNoise = this.lowerNoiseField[densityIndex] / 512.0D;
                    double upperNoise = this.upperNoiseField[densityIndex] / 512.0D;

                    // Blending between the two noise fields
                    double blendNoise = (this.blendNoiseField[densityIndex] / 10.0D + 1.0D) / 2.0D;

                    // Blending between lower an dupper noise and subtract the vertical gradient
                    double density = MathHelper.denormalizeClamp(lowerNoise, upperNoise, blendNoise) - verticalDistanceToTerrainCenter;

                    // This causes the top samples to be pulled toward the air. As a result, mountains end up well below the world limit.
                    if (sampleY > 29) {

                        double topFade = (double)((float)(sampleY - 29) / 3.0F);
                        density = density * (1.0D - topFade) + -10.0D * topFade;
                    }

                    this.densityField[densityIndex] = density;
                    ++densityIndex;
                }
            }
        }
    }
}
