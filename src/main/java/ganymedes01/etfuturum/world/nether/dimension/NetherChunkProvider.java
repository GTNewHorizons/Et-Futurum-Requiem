package ganymedes01.etfuturum.world.nether.dimension;

import cpw.mods.fml.common.eventhandler.Event;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.compat.ExternalContent;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.world.generate.decorate.WorldGenSoulFire;
import ganymedes01.etfuturum.world.generate.feature.WorldGenBasaltGlowstone;
import ganymedes01.etfuturum.world.nether.biome.NetherBiomeBase;
import ganymedes01.etfuturum.world.nether.biome.utils.NetherBiomeManager;
import ganymedes01.etfuturum.world.nether.dimension.gen.MapGenCavesHellModified;
import ganymedes01.etfuturum.world.nether.dimension.gen.MapGenRavineHell;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.List;
import java.util.Random;

import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.SHROOM;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_BRIDGE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.NETHER_CAVE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.*;

public class NetherChunkProvider implements IChunkProvider {
	private final Random hellRNG;
	private final NoiseGeneratorOctaves netherNoiseGen1;
	private final NoiseGeneratorOctaves netherNoiseGen2;
	private final NoiseGeneratorOctaves netherNoiseGen3;
	private final NoiseGeneratorOctaves slowsandGravelNoiseGen;
	private final NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
	public NoiseGeneratorOctaves netherNoiseGen6;
	public NoiseGeneratorOctaves netherNoiseGen7;
	private final World worldObj;
	private double[] noiseField;
	public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
	private double[] slowsandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private BiomeGenBase[] biomesForGeneration;
	private double[] netherrackExclusivityNoise = new double[256];
	private MapGenBase netherCaveGenerator = new MapGenCavesHellModified();
	private final MapGenBase netherRavineGenerator = new MapGenRavineHell();
	double[] noiseData1;
	double[] noiseData2;
	double[] noiseData3;
	double[] noiseData4;
	double[] noiseData5;

	{
		genNetherBridge = (MapGenNetherBridge) TerrainGen.getModdedMapGen(genNetherBridge, NETHER_BRIDGE);
		netherCaveGenerator = TerrainGen.getModdedMapGen(netherCaveGenerator, NETHER_CAVE);
	}

	private double[] soulSoilNoise = new double[256];
	private final NoiseGeneratorOctaves soulSoilNoiseGen;

	private final WorldGenerator quartzOreGen = new WorldGenMinable(Blocks.quartz_ore, 13, Blocks.netherrack);
	private final WorldGenerator redMushroomGen = new WorldGenFlowers(Blocks.red_mushroom);
	private final WorldGenerator brownMushroomGen = new WorldGenFlowers(Blocks.brown_mushroom);
	private final WorldGenerator glowstone1Gen = new WorldGenGlowStone1();
	private final WorldGenerator glowstone2Gen = new WorldGenGlowStone2();
	private final WorldGenerator basaltGlowstoneGen = new WorldGenBasaltGlowstone();
	private final WorldGenerator fireGen = new WorldGenFire();
	private final WorldGenerator soulFireGen = new WorldGenSoulFire();
	private final WorldGenerator lavaGen = new WorldGenHellLava(Blocks.flowing_lava, false);

	public NetherChunkProvider(World par1World, long par2) {
		worldObj = par1World;
		hellRNG = new Random(par2);
		netherNoiseGen1 = new NoiseGeneratorOctaves(hellRNG, 16);
		netherNoiseGen2 = new NoiseGeneratorOctaves(hellRNG, 16);
		netherNoiseGen3 = new NoiseGeneratorOctaves(hellRNG, 8);
		slowsandGravelNoiseGen = new NoiseGeneratorOctaves(hellRNG, 4);
		netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(hellRNG, 4);
		netherNoiseGen6 = new NoiseGeneratorOctaves(hellRNG, 10);
		netherNoiseGen7 = new NoiseGeneratorOctaves(hellRNG, 16);

		soulSoilNoiseGen = new NoiseGeneratorOctaves(hellRNG, 4);
		/*
		 * NoiseGeneratorOctaves[] noiseGens = {netherNoiseGen1, netherNoiseGen2,
		 * netherNoiseGen3, slowsandGravelNoiseGen, netherrackExculsivityNoiseGen,
		 * netherNoiseGen6, netherNoiseGen7}; noiseGens =
		 * TerrainGen.getModdedNoiseGenerators(par1World, hellRNG, noiseGens);
		 * netherNoiseGen1 = noiseGens[0]; netherNoiseGen2 = noiseGens[1];
		 * netherNoiseGen3 = noiseGens[2]; slowsandGravelNoiseGen = noiseGens[3];
		 * netherrackExculsivityNoiseGen = noiseGens[4]; netherNoiseGen6 = noiseGens[5];
		 * netherNoiseGen7 = noiseGens[6];
		 */
	}

	/**
	 * Generates the shape of the terrain in the nether.
	 */
	public void generateNetherTerrain(int par1, int par2, Block[] blocks, byte[] abyte, BiomeGenBase[] biomesForGeneration) {
		byte b0 = 4;
		byte b1 = 32;
		int k = b0 + 1;
		byte b2 = 17;
		int l = b0 + 1;
		BiomeGenBase biomegenbase = biomesForGeneration[l + k * 16];
		noiseField = this.initializeNoiseField(noiseField, par1 * b0, 0, par2 * b0, k, b2, l);

		for (int i1 = 0; i1 < b0; ++i1) {
			for (int j1 = 0; j1 < b0; ++j1) {
				for (int k1 = 0; k1 < 16; ++k1) {
					double d0 = 0.125D;
					double d1 = noiseField[((i1) * l + j1) * b2 + k1];
					int i = ((i1) * l + j1 + 1) * b2;
					int i3 = ((i1 + 1) * l + j1) * b2;
					int i4 = ((i1 + 1) * l + j1 + 1) * b2;
					double d2 = noiseField[i + k1];
					double d3 = noiseField[i3 + k1];
					double d4 = noiseField[i4 + k1];
					double d5 = (noiseField[((i1) * l + j1) * b2 + k1 + 1] - d1) * d0;
					double d6 = (noiseField[i + k1 + 1] - d2) * d0;
					double d7 = (noiseField[i3 + k1 + 1] - d3) * d0;
					double d8 = (noiseField[i4 + k1 + 1] - d4) * d0;

					//BiomeGenBase biome = worldObj.getBiomeGenForCoords(i1 + par1 * 16, j1 + par2 * 16); // unused variable

					for (int l1 = 0; l1 < 8; ++l1) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i2 = 0; i2 < 4; ++i2) {
							int j2 = i2 + i1 * 4 << 11 | j1 * 4 << 7 | k1 * 8 + l1;
							short short1 = 128;
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k2 = 0; k2 < 4; ++k2) {
								Block l2 = Blocks.air;

								if (k1 * 8 + l1 < b1) {
									l2 = Blocks.lava;
								}

								if (d15 > 0.0D) {
									l2 = Blocks.netherrack;
								}

								if (ModsList.NATURA.isLoaded() && biomegenbase == BiomeGenBase.hell && d15 > 56.0D) {
									l2 = ExternalContent.Blocks.NATURA_TAINTED_SOIL.get();
								}

								blocks[j2] = l2;
								abyte[j2] = 0;
								j2 += short1;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	/**
	 * name based on ChunkProviderGenerate
	 */
	public void replaceBlocksForBiome(int x, int z, Block[] blocks, byte[] metas, BiomeGenBase[] par4ArrayOfBiomeGenBase) {
		@SuppressWarnings("deprecation")
		ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, x, z, blocks, par4ArrayOfBiomeGenBase);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Event.Result.DENY) {
			return;
		}

		byte b0 = 32;
		byte a0 = 64;
		double d0 = 0.03125D;
		slowsandNoise = slowsandGravelNoiseGen.generateNoiseOctaves(slowsandNoise, x * 16, z * 16, 0, 16, 16, 1,
				d0, d0, 1.0D);
		gravelNoise = slowsandGravelNoiseGen.generateNoiseOctaves(gravelNoise, x * 16, 109, z * 16, 16, 1, 16, d0,
				1.0D, d0);
		netherrackExclusivityNoise = netherrackExculsivityNoiseGen.generateNoiseOctaves(netherrackExclusivityNoise,
				x * 16, z * 16, 0, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

		soulSoilNoise = soulSoilNoiseGen.generateNoiseOctaves(soulSoilNoise, x * 16, z * 16,
				16, 16, 0.0625D, 0.0625D, 1.0D);


		for (int k = 0; k < 16; ++k) {
			for (int l = 0; l < 16; ++l) {
				BiomeGenBase biomegenbase = par4ArrayOfBiomeGenBase[l + k * 16];

				boolean soulSandFlag = biomegenbase == BiomeGenBase.hell && slowsandNoise[k + l * 16] + hellRNG.nextDouble() * 0.2D > 0.0D;
				boolean gravelFlag = biomegenbase == BiomeGenBase.hell && gravelNoise[k + l * 16] + hellRNG.nextDouble() * 0.2D > 0.0D;
				boolean soulSoilFlag;
				if (biomegenbase == NetherBiomeManager.soulSandValley && ModBlocks.SOUL_SOIL.isEnabled()) {
					double n = soulSoilNoise[k + l * 16] + hellRNG.nextDouble() * 0.05D;
					soulSoilFlag = n > 1.5D || n < -0.5D;
				} else {
					soulSoilFlag = false;
				}
				int i1 = (int) (netherrackExclusivityNoise[k + l * 16] / 3.0D + 3.0D + hellRNG.nextDouble() * 0.25D);
				int j1 = -1;
				Block topBlock, fillerBlock;
				if (soulSoilFlag) {
					topBlock = fillerBlock = ModBlocks.SOUL_SOIL.get();
				} else {
					topBlock = biomegenbase.topBlock;
					fillerBlock = biomegenbase.fillerBlock;
				}
				byte topMeta = (byte) biomegenbase.field_150604_aj; // topBlockMetadata
				byte fillerMeta = (byte) biomegenbase.field_76754_C; // fillerBlockMetadata

				if (biomegenbase == BiomeGenBase.hell && ModsList.NATURA.isLoaded()) {
					fillerBlock = ExternalContent.Blocks.NATURA_TAINTED_SOIL.get();
					fillerMeta = 0;
				}

				for (int k1 = 127; k1 >= 0; --k1) {
					int l1 = (l * 16 + k) * 128 + k1;

					if (k1 < 127 - hellRNG.nextInt(5) && k1 > hellRNG.nextInt(5)) {
						Block b3 = blocks[l1];

						if (b3 == Blocks.air) {
							j1 = -1;

							if (blocks[l1 + 1] == Blocks.netherrack) {
								blocks[l1 + 1] = soulSoilFlag ? ModBlocks.SOUL_SOIL.get() : biomegenbase.fillerBlock;
								metas[l1 + 1] = (byte) biomegenbase.field_76754_C; // fillerBlockMetadata
							}

							if (blocks[l1 + 2] == Blocks.netherrack) {
								blocks[l1 + 2] = soulSoilFlag ? ModBlocks.SOUL_SOIL.get() : biomegenbase.fillerBlock;
								metas[l1 + 2] = (byte) biomegenbase.field_76754_C; // fillerBlockMetadata
							}

							if (blocks[l1 + 3] == Blocks.netherrack) {
								blocks[l1 + 3] = soulSoilFlag ? ModBlocks.SOUL_SOIL.get() : biomegenbase.fillerBlock;
								metas[l1 + 3] = (byte) biomegenbase.field_76754_C; // fillerBlockMetadata
							}
						}

//                      Biome Terrain Block for Top and Filler Block
						else if (b3 == Blocks.netherrack) {
							if (j1 == -1) {
								if (i1 <= 0) {
									topBlock = Blocks.air;
									fillerBlock = Blocks.netherrack;
									topMeta = fillerMeta = 0;
								} else if (k1 >= a0 - 4 && k1 <= a0 + 1) {
									if (soulSoilFlag) {
										fillerBlock = topBlock = ModBlocks.SOUL_SOIL.get();
									} else {
										topBlock = biomegenbase.topBlock;
										fillerBlock = biomegenbase.fillerBlock;
									}
									topMeta = (byte) biomegenbase.field_150604_aj; // topBlockMetadata

									if (biomegenbase == BiomeGenBase.hell && ModsList.NATURA.isLoaded()) {
										fillerBlock = ExternalContent.Blocks.NATURA_TAINTED_SOIL.get();
										fillerMeta = 0;
									} else {
										fillerMeta = (byte) biomegenbase.field_76754_C; // fillerBlockMetadata
									}

									if (gravelFlag) {
										topBlock = Blocks.gravel;
										topMeta = 0;
									}

									if (gravelFlag) {
										fillerBlock = Blocks.netherrack;
										fillerMeta = 0;
									}

									if (soulSandFlag) {
										topBlock = Blocks.soul_sand;
										topMeta = 0;
									}

									if (soulSandFlag) {
										if (ModsList.NATURA.isLoaded()) {
											fillerBlock = ExternalContent.Blocks.NATURA_HEAT_SAND.get();
										} else {
											fillerBlock = Blocks.soul_sand;
										}
										fillerMeta = 0;
									}
								}

								if (k1 < b0 && topBlock == Blocks.air) {
									topBlock = Blocks.lava;
									topMeta = 0;
								}

								j1 = i1;

								if (k1 >= b0 - 1) {
									blocks[l1] = topBlock;
									metas[l1] = topMeta;
								} else {
									blocks[l1] = fillerBlock;
									metas[l1] = fillerMeta;
								}
							} else if (j1 > 0) {
								--j1;
								blocks[l1] = fillerBlock;
								metas[l1] = fillerMeta;
							}
						}
					} else {
						blocks[l1] = Blocks.bedrock;
						metas[l1] = 0;
					}
				}
			}
		}
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	@Override
	public Chunk loadChunk(int par1, int par2) {
		return this.provideChunk(par1, par2);
	}

	/**
	 * Will return back a chunk, if it doesn't exist and its not a MP client it will
	 * generates all the blocks for the specified chunk from the map seed and chunk
	 * seed
	 */
	@Override
	public Chunk provideChunk(int par1, int par2) {
		hellRNG.setSeed(par1 * 341873128712L + par2 * 132897987541L);
		byte[] abyte = new byte[32768];
		Block[] blocks = new Block[32768];
		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, par1 * 16,
				par2 * 16, 16, 16);
		this.generateNetherTerrain(par1, par2, blocks, abyte, biomesForGeneration);
		this.replaceBlocksForBiome(par1, par2, blocks, abyte, biomesForGeneration);
		netherCaveGenerator.func_151539_a(this, worldObj, par1, par2, blocks); // generate

		netherRavineGenerator.func_151539_a(this, worldObj, par1, par2, blocks); //generate TODO: Make this available for the vanilla Nether as a separate mixin

		genNetherBridge.func_151539_a(this, worldObj, par1, par2, blocks); // generate
		Chunk chunk = new Chunk(worldObj, blocks, abyte, par1, par2);
		BiomeGenBase[] abiomegenbase = worldObj.getWorldChunkManager().loadBlockGeneratorData(null,
				par1 * 16, par2 * 16, 16, 16);
		byte[] abyte1 = chunk.getBiomeArray();

		for (int k = 0; k < abyte1.length; ++k) {
			abyte1[k] = (byte) abiomegenbase[k].biomeID;
		}

		chunk.resetRelightChecks();
		return chunk;
	}

	/**
	 * generates a subset of the level's terrain data. Takes 7 arguments: the
	 * [empty] noise array, the position, and the size.
	 */
	private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5, int par6, int par7) {
		ChunkProviderEvent.InitNoiseField event = new
				ChunkProviderEvent.InitNoiseField(this, par1ArrayOfDouble, par2, par3, par4,
				par5, par6, par7);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.getResult() == Event.Result.DENY) return event.noisefield;

		if (par1ArrayOfDouble == null) {
			par1ArrayOfDouble = new double[par5 * par6 * par7];
		}

		double d0 = 684.412D;
		double d1 = 2053.236D;
		noiseData4 = netherNoiseGen6.generateNoiseOctaves(noiseData4, par2, par3, par4, par5, 1, par7, 1.0D, 0.0D,
				1.0D);
		noiseData5 = netherNoiseGen7.generateNoiseOctaves(noiseData5, par2, par3, par4, par5, 1, par7, 100.0D, 0.0D,
				100.0D);
		noiseData1 = netherNoiseGen3.generateNoiseOctaves(noiseData1, par2, par3, par4, par5, par6, par7, d0 / 80.0D,
				d1 / 60.0D, d0 / 80.0D);
		noiseData2 = netherNoiseGen1.generateNoiseOctaves(noiseData2, par2, par3, par4, par5, par6, par7, d0, d1, d0);
		noiseData3 = netherNoiseGen2.generateNoiseOctaves(noiseData3, par2, par3, par4, par5, par6, par7, d0, d1, d0);
		int k1 = 0;
		int l1 = 0;
		double[] adouble1 = new double[par6];
		int i2;

		for (i2 = 0; i2 < par6; ++i2) {
			adouble1[i2] = Math.cos(i2 * Math.PI * 6.0D / par6) * 2.0D;
			double d2 = i2;

			if (i2 > par6 / 2) {
				d2 = par6 - 1 - i2;
			}

			if (d2 < 4.0D) {
				d2 = 4.0D - d2;
				adouble1[i2] -= d2 * d2 * d2 * 10.0D;
			}
		}

		for (i2 = 0; i2 < par5; ++i2) {
			for (int j2 = 0; j2 < par7; ++j2) {
				double d3 = (noiseData4[l1] + 256.0D) / 512.0D;

				if (d3 > 1.0D) {
					d3 = 1.0D;
				}

				double d5 = noiseData5[l1] / 8000.0D;

				if (d5 < 0.0D) {
					d5 = -d5;
				}

				d5 = d5 * 3.0D - 3.0D;

				if (d5 < 0.0D) {
					d5 /= 2.0D;

					if (d5 < -1.0D) {
						d5 = -1.0D;
					}

					d5 /= 1.4D;
					d5 /= 2.0D;
					d3 = 0.0D;
				} else {
					if (d5 > 1.0D) {
						d5 = 1.0D;
					}

					d5 /= 6.0D;
				}

				d3 += 0.5D;
				d5 = d5 * par6 / 16.0D;
				++l1;

				for (int k2 = 0; k2 < par6; ++k2) {
					double d6 = 0.0D;
					double d7 = adouble1[k2];
					double d8 = noiseData2[k1] / 512.0D;
					double d9 = noiseData3[k1] / 512.0D;
					double d10 = (noiseData1[k1] / 10.0D + 1.0D) / 2.0D;

					if (d10 < 0.0D) {
						d6 = d8;
					} else if (d10 > 1.0D) {
						d6 = d9;
					} else {
						d6 = d8 + (d9 - d8) * d10;
					}

					d6 -= d7;
					double d11;

					if (k2 > par6 - 4) {
						d11 = (k2 - (par6 - 4)) / 3.0F;
						d6 = d6 * (1.0D - d11) + -10.0D * d11;
					}

//					if (k2 < d4) { //This is always false
//						d11 = (d4 - k2) / 4.0D;
//
//						if (d11 < 0.0D) {
//							d11 = 0.0D;
//						}
//
//						if (d11 > 1.0D) {
//							d11 = 1.0D;
//						}
//
//						d6 = d6 * (1.0D - d11) + -10.0D * d11;
//					}

					par1ArrayOfDouble[k1] = d6;
					++k1;
				}
			}
		}

		return par1ArrayOfDouble;
	}

	/**
	 * Checks to see if a chunk exists at x, y
	 */
	@Override
	public boolean chunkExists(int par1, int par2) {
		return true;
	}

	/**
	 * Populates chunk with ores etc etc
	 */
	@Override
	public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
		BlockFalling.fallInstantly = true;
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(par1IChunkProvider, worldObj, hellRNG, par2, par3, false));

		int k = par2 * 16;
		int l = par3 * 16;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords(k + 16, l + 16);
		this.hellRNG.setSeed(this.worldObj.getSeed());
		long rand1 = this.hellRNG.nextLong() / 2L * 2L + 1L;
		long rand2 = this.hellRNG.nextLong() / 2L * 2L + 1L;
		this.hellRNG.setSeed(par2 * rand1 + par3 * rand2 ^ this.worldObj.getSeed());

		genNetherBridge.generateStructuresInChunk(worldObj, hellRNG, par2, par3);
		int i1;
		int j1;
		int k1;
		int l1;

		if (biome instanceof NetherBiomeBase Netherbiome) {
			Netherbiome.populate(worldObj, this.hellRNG, par2 * 16, par3 * 16);
		}

		boolean doGen = TerrainGen.populate(par1IChunkProvider, worldObj, hellRNG, par2, par3, false, NETHER_LAVA);
		if (doGen) {
			for (i1 = 0; i1 < 8; ++i1) {
				j1 = k + this.hellRNG.nextInt(16) + 8;
				k1 = this.hellRNG.nextInt(120) + 4;
				l1 = l + this.hellRNG.nextInt(16) + 8;
				lavaGen.generate(this.worldObj, this.hellRNG, j1, k1, l1);
			}
		}

		i1 = hellRNG.nextInt(hellRNG.nextInt(10) + 1) + 1;
		int i2;

		doGen = TerrainGen.populate(par1IChunkProvider, worldObj, hellRNG, par2, par3, false, FIRE);
		if (doGen) {
			for (j1 = 0; j1 < i1; ++j1) {
				k1 = k + hellRNG.nextInt(16) + 8;
				l1 = hellRNG.nextInt(120) + 4;
				i2 = l + hellRNG.nextInt(16) + 8;
				(biome == NetherBiomeManager.soulSandValley && ConfigMixins.soulFire ? soulFireGen : fireGen).generate(worldObj, hellRNG, k1, l1, i2);
			}
		}

		i1 = hellRNG.nextInt(hellRNG.nextInt(10) + 1);

		doGen = TerrainGen.populate(par1IChunkProvider, worldObj, hellRNG, par2, par3, false, GLOWSTONE);
		if (doGen) {
			for (j1 = 0; j1 < i1; ++j1) {
				k1 = k + this.hellRNG.nextInt(16) + 8;
				l1 = this.hellRNG.nextInt(120) + 4;
				i2 = l + this.hellRNG.nextInt(16) + 8;
				if (biome == NetherBiomeManager.basaltDeltas) {
					basaltGlowstoneGen.generate(this.worldObj, this.hellRNG, k1, l1, i2);
				} else {
					glowstone1Gen.generate(this.worldObj, this.hellRNG, k1, l1, i2);
				}
			}

			//Not sure why this needs two identical glowstone classes, I'll use them in non-deltas instances just to be safe.
			//Probably doesn't make a difference for seed parity but the classes are already there so might as well use them.

			for (j1 = 0; j1 < 10; ++j1) {
				k1 = k + this.hellRNG.nextInt(16) + 8;
				l1 = this.hellRNG.nextInt(128);
				i2 = l + this.hellRNG.nextInt(16) + 8;
				if (biome == NetherBiomeManager.basaltDeltas) {
					basaltGlowstoneGen.generate(this.worldObj, this.hellRNG, k1, l1, i2);
				} else {
					glowstone2Gen.generate(this.worldObj, this.hellRNG, k1, l1, i2);
				}
			}
		}

		if (!ModsList.NATURA.isLoaded()) {
			MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, hellRNG, k, l));
			doGen = TerrainGen.decorate(worldObj, hellRNG, k, l, SHROOM);
			if (doGen) {
				hellRNG.nextInt(1); //I keep this and other random calls in this class the same for seed parity reasons
				j1 = k + hellRNG.nextInt(16) + 8;
				k1 = hellRNG.nextInt(128);
				l1 = l + hellRNG.nextInt(16) + 8;
				if (k1 <= 124) { //Do this to fix mushrooms on the ceiling while keeping the same random calls
					brownMushroomGen.generate(worldObj, hellRNG, j1, k1, l1);
				}

				hellRNG.nextInt(1);
				j1 = k + hellRNG.nextInt(16) + 8;
				k1 = hellRNG.nextInt(128);
				l1 = l + hellRNG.nextInt(16) + 8;
				if (k1 <= 124) {
					redMushroomGen.generate(worldObj, hellRNG, j1, k1, l1);
				}
			}
		}

		int j2;

		for (k1 = 0; k1 < 16; ++k1) {
			l1 = k + hellRNG.nextInt(16);
			i2 = hellRNG.nextInt(108) + 10;
			j2 = l + hellRNG.nextInt(16);
			quartzOreGen.generate(worldObj, hellRNG, l1, i2, j2);
		}

		biome.decorate(worldObj, hellRNG, k, l);

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, hellRNG, k, l));
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(par1IChunkProvider, worldObj, hellRNG, par2, par3, false));

		BlockFalling.fallInstantly = false;
	}


	/**
	 * Two modes of operation: if passed true, save all Chunks in one go. If passed
	 * false, save up to two chunks. Return true if all chunks have been saved.
	 */
	@Override
	public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
		return true;
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to
	 * unload every such chunk.
	 */
	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	/**
	 * Returns if the IChunkProvider supports saving.
	 */
	@Override
	public boolean canSave() {
		return true;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	@Override
	public String makeString() {
		return "HellRandomLevelSource";
	}

	/**
	 * Returns a list of creatures of the specified type that can spawn at the given
	 * location.
	 */
	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
		if (par1EnumCreatureType == EnumCreatureType.monster && genNetherBridge.hasStructureAt(par2, par3, par4))
			return genNetherBridge.getSpawnList();
		BiomeGenBase biomegenbase = worldObj.getBiomeGenForCoords(par2, par4);
		return biomegenbase == null ? null : biomegenbase.getSpawnableList(par1EnumCreatureType);
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public void recreateStructures(int par1, int par2) {
		genNetherBridge.func_151539_a(this, worldObj, par1, par2, null); // generate
	}

	@Override
	public void saveExtraData() {
	}

	/**
	 * MCP name: {@code findClosestStructure}
	 */
	@Override
	public ChunkPosition func_147416_a(World par1World, String par2Str, int par3, int par4, int par5) {
		return null;
	}
}
