package ganymedes01.etfuturum.blocks;

import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.core.utils.IInitAction;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.lib.RenderIDs;
import ganymedes01.etfuturum.recipes.ModRecipes;
import ganymedes01.etfuturum.world.EtFuturumWorldListener;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public class BlockBubbleColumn extends BaseBlock implements IInitAction {

	private static final int[][] HORIZONTAL_OFFSETS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	private final Map<Block, Integer> supportBlockMeta = new Object2IntOpenHashMap<>();
	private int lastColumnSoundTick = -1;

	/**
	 * @param upBlocks   blocks that produce upward columns (meta 0), e.g. soul sand
	 * @param downBlocks blocks that produce downward/whirlpool columns (meta 1), e.g. magma
	 */
	public BlockBubbleColumn(Block[] upBlocks, Block[] downBlocks) {
		super(Material.water);
		for (Block b : upBlocks) supportBlockMeta.put(b, 0);
		for (Block b : downBlocks) supportBlockMeta.put(b, 1);
		setLightOpacity(Blocks.water.getLightOpacity());
		setBlockName("bubble_column");
		setBlockBounds(0, 0, 0, 0, 0, 0);
	}

	public boolean isUp(int meta) {
		return meta == 0;
	}

	@Override
	public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
		super.randomDisplayTick(worldIn, x, y, z, random);
		boolean up = isUp(worldIn.getBlockMetadata(x, y, z));
		if (up) {
			// One centered + one random
			CustomParticles.spawnBubbleColumnUp(worldIn, x + 0.5, y, z + 0.5, 0, 0.04, 0);
			CustomParticles.spawnBubbleColumnUp(worldIn,
				x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(), 0, 0.04, 0);
		} else if (random.nextInt(2) == 0) {
			// One particle at top-center
			CustomParticles.spawnWaterCurrentDown(worldIn, x + 0.5, y + 0.8, z + 0.5);
		}
		if (random.nextInt(200) == 0) {
			worldIn.playSound(x + random.nextFloat(), y + random.nextFloat(), z + random.nextFloat(),
					getBubblingNoise(worldIn, x, y, z, up ? 0 : 1, random), 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
		// dont have particles trigger this; gets a little too crazy
		if (worldIn.isRemote && entityIn instanceof EntityFX) return;

		int meta = worldIn.getBlockMetadata(x, y, z);
		boolean isUp = isUp(meta);

		Block blockAbove = worldIn.getBlock(x, y + 1, z);
		if (blockAbove == Blocks.air) {
			if (isUp) {
				entityIn.motionY = Math.min(1.8D, entityIn.motionY + 0.1D);
				entityIn.fallDistance = 0;
			} else {
				entityIn.motionY = Math.max(-0.9D, entityIn.motionY - 0.03D);
			}

			// handle splash effects
			if (worldIn.isRemote) {
				for (int i = 0; i < 2; i++) {
					worldIn.spawnParticle("splash",
							x + worldIn.rand.nextDouble(),
						y + 1,
							z + worldIn.rand.nextDouble(),
						0,
						0,
						0
					);
					worldIn.spawnParticle("bubble",
							x + worldIn.rand.nextDouble(),
							y + 1,
							z + worldIn.rand.nextDouble(),
							0,
							0,
							0
					);
				}
			}
		} else {
			if (isUp) {
				entityIn.motionY = Math.min(0.7D, entityIn.motionY + 0.6D);
			} else {
				entityIn.motionY = Math.max(-0.3D, entityIn.motionY - 0.3D);
			}
			entityIn.fallDistance = 0;

			if (worldIn.isRemote && entityIn instanceof EntityPlayerSP player) {
				if (lastColumnSoundTick == -1 || entityIn.ticksExisted - lastColumnSoundTick > 1) {
					player.playSound(Reference.MCAssetVer + ":" + "block.bubble_column." + (isUp ? "upwards" : "whirlpool") + "_inside",
							1, 1);
				}
				lastColumnSoundTick = entityIn.ticksExisted;
			}
		}

		if (entityIn instanceof EntityLivingBase) {
			entityIn.setAir(300);
		}
	}

	protected String getBubblingNoise(World world, int x, int y, int z, int meta, Random random) {
		return Reference.MCAssetVer + ":" + "block.bubble_column." + (isUp(meta) ? "upwards" : "whirlpool") + "_ambient";
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
		return null;
	}

	@Override
	public void onBlockAdded(World worldIn, int x, int y, int z) {
		manageColumn(worldIn, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
		manageColumn(worldIn, x, y, z);
	}

	protected void manageColumn(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		Block below = world.getBlock(x, y - 1, z);
		if (below == this) {
			// Inherit metadata from column below
			int belowMeta = world.getBlockMetadata(x, y - 1, z);
			if (belowMeta != meta) {
				world.setBlockMetadataWithNotify(x, y, z, belowMeta, 3);
				meta = belowMeta;
			}
		} else if (supportBlockMeta.containsKey(below)) {
			// Validate metadata matches the support block
			int expectedMeta = supportBlockMeta.get(below);
			if (expectedMeta != meta) {
				world.setBlockMetadataWithNotify(x, y, z, expectedMeta, 3);
				meta = expectedMeta;
			}
		} else {
			// No valid support below
			world.setBlock(x, y, z, Blocks.water);
			return;
		}
		if (isFullVanillaWater(world.getBlock(x, y + 1, z), world.getBlockMetadata(x, y + 1, z))) {
			world.setBlock(x, y + 1, z, this, meta, 3);
		}

		// Spread water to adjacent air blocks
		for (int[] offset : HORIZONTAL_OFFSETS) {
			Block adjacent = world.getBlock(x + offset[0], y, z + offset[1]);
			if (adjacent == Blocks.air) {
				world.setBlock(x + offset[0], y, z + offset[1], Blocks.flowing_water, 1, 3);
			}
		}
	}

	public static boolean isFullVanillaWater(Block block, int meta) {
		return meta == 0 && (block == Blocks.water || block == Blocks.flowing_water);
	}

	@Override
	public boolean canRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public int getRenderType() {
		return RenderIDs.BUBBLE_COLUMN;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void postInitAction() {
		if (ModRecipes.validateItems(this)) {
			supportBlockMeta.forEach((block, meta) -> {
				if (ModRecipes.validateItems(block)) {
					EtFuturumWorldListener.bubbleColumnMap.put(block, meta);
				}
			});
		}
	}

	@Override
	public Item getItem(World worldIn, int x, int y, int z) {
		return Item.getItemFromBlock(Blocks.water);
	}
}
