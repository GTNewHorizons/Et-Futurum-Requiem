package ganymedes01.etfuturum.blocks;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockStatePool;
import com.gtnewhorizon.gtnhlib.blockstate.core.MetaBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty.BooleanMetaBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import com.gtnewhorizon.gtnhlib.color.RGBColor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.api.DripOperationRegistry;
import ganymedes01.etfuturum.client.particle.CustomParticles;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.EntityFallingDripstone;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.Nullable;

public class BlockPointedDripstone extends Block {

	public final BooleanMetaBlockProperty up = new BooleanMetaBlockProperty() {

		@Override
		public String getName() {
			return "up";
		}

		@Override
		public boolean hasTrait(BlockPropertyTrait trait) {
			return switch (trait) {
				case SupportsWorld, OnlyNeedsMeta, WorldMutable, Primitive, MetaPrimitive, SupportsStacks, StackMutable -> true;
				default -> false;
			};
		}

		@Override
		public int getMetaPrimitive(boolean value, int existing) {
			return (existing % 5) + (value ? 5 : 0);
		}

		@Override
		public boolean getValuePrimitive(int meta) {
			return meta >= 5;
		}
	};

	public final MetaBlockProperty<DripstoneState> state = new MetaBlockProperty<>() {

		@Override
		public int getMeta(DripstoneState state, int existing) {
			return (existing >= 5 ? 5 : 0) + state.ordinal();
		}

		@Override
		public DripstoneState getValue(int meta) {
			return DripstoneState.STATES[meta % 5];
		}

		@Override
		public String getName() {
			return "state";
		}

		@Override
		public Type getType() {
			return DripstoneState.class;
		}

		@Override
		public boolean hasTrait(BlockPropertyTrait trait) {
			return switch (trait) {
				case SupportsWorld, OnlyNeedsMeta, WorldMutable, SupportsStacks, StackMutable -> true;
				default -> false;
			};
		}
	};

	public enum DripstoneState {
		Base,
		Middle,
		Frustum,
		Tip,
		TipMerge;

		public IIcon upIcon, downIcon;
		private static final DripstoneState[] STATES = values();
	}

	public static final DamageSource STALACTITE_DAMAGE = new DamageSource("stalactite");

	public BlockPointedDripstone() {
		super(Material.rock);
		Utils.setBlockSound(this, ModSounds.soundPointedDripstone);
		this.setHardness(1.5F);
		this.setResistance(3F);
		this.setHarvestLevel("pickaxe", 0);
		this.setBlockName(Utils.getUnlocalisedName("pointed_dripstone"));
		this.setBlockTextureName("pointed_dripstone");
		this.setCreativeTab(EtFuturum.creativeTabBlocks);
		this.setTickRandomly(true);

		BlockPropertyRegistry.registerProperty(this, up);
		BlockPropertyRegistry.registerProperty(this, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		DripstoneState.Base.downIcon = reg.registerIcon(getTextureName() + "_down_base");
		DripstoneState.Middle.downIcon = reg.registerIcon(getTextureName() + "_down_middle");
		DripstoneState.Frustum.downIcon = reg.registerIcon(getTextureName() + "_down_frustum");
		DripstoneState.Tip.downIcon = reg.registerIcon(getTextureName() + "_down_tip");
		DripstoneState.TipMerge.downIcon = reg.registerIcon(getTextureName() + "_down_tip_merge");

		DripstoneState.Base.upIcon = reg.registerIcon(getTextureName() + "_up_base");
		DripstoneState.Middle.upIcon = reg.registerIcon(getTextureName() + "_up_middle");
		DripstoneState.Frustum.upIcon = reg.registerIcon(getTextureName() + "_up_frustum");
		DripstoneState.Tip.upIcon = reg.registerIcon(getTextureName() + "_up_tip");
		DripstoneState.TipMerge.upIcon = reg.registerIcon(getTextureName() + "_up_tip_merge");
	}

	private int countDripstone(World world, int x, int y, int z, boolean pointingUp, boolean scanUp, boolean skipFirst) {
		int count = 0;

		int deltaY = scanUp ? 1 : -1;

		if (skipFirst) {
			count++;
			y += deltaY;
		}

		for (int i = 0; i < 3; i++) {
			if (world.getBlock(x, y, z) != this) break;

			boolean isUp = this.up.getBooleanValue(world, x, y, z);

			if (isUp != pointingUp) break;

			y += deltaY;
			count++;
		}

		return count;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);

		boolean pointingUp = dir == ForgeDirection.UP;

		int count = countDripstone(world, x, y, z, pointingUp, pointingUp, true);

		DripstoneState state = switch (count) {
			case 1 -> DripstoneState.Tip;
			case 2 -> DripstoneState.Frustum;
			default -> DripstoneState.Middle;
		};

		if (state == DripstoneState.Middle && countDripstone(world, x, y, z, pointingUp, !pointingUp, true) == 1) {
			state = DripstoneState.Base;
		}

		int dy = pointingUp ? 1 : -1;

		if (state == DripstoneState.Tip && countDripstone(world, x, y + dy, z, !pointingUp, pointingUp, false) > 0) {
			state = DripstoneState.TipMerge;
		}

		meta = this.up.getMetaPrimitive(pointingUp, 0);
		meta = this.state.getMeta(state, meta);

		return meta;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);

		if (dir == ForgeDirection.DOWN) {
			return world.isSideSolid(x, y + 1, z, ForgeDirection.DOWN) || world.getBlock(x, y + 1, z) == this;
		}

		if (dir == ForgeDirection.UP) {
			return world.isSideSolid(x, y - 1, z, ForgeDirection.UP) || world.getBlock(x, y - 1, z) == this;
		}

		return false;
	}

	private boolean collapsing = false;

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (canBlockStay(world, x, y, z)) {
			updateState(world, x, y, z);
			return;
		}

		if (collapsing) return;

		if (up.getBooleanValue(world, x, y, z)) {
			world.setBlockToAir(x, y, z);
			this.dropBlockAsItem(world, x, y, z, 0, 0);
		} else {
			collapseHangingColumn(world, x, y, z);
		}
	}

	/**
	 * Clears the whole contiguous run of down-pointing dripstone hanging below (x,y,z) in one
	 * synchronous pass and spawns a single falling entity for it, rather than one entity per block.
	 * Doing this atomically (instead of relying on onNeighborBlockChange to cascade block-by-block
	 * across ticks) avoids a race where a redundant notification for a still-standing block spawns a
	 * competing entity before the first one gets a chance to remove it.
	 */
	private void collapseHangingColumn(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);

		int count = 0;
		int y2 = y;

		this.collapsing = true;

		while (!up.getBooleanValue(world, x, y2, z) && world.getBlock(x, y2, z) == this) {
			world.setBlock(x, y2, z, Blocks.air, 0, 2);
			y2--;
			count++;
		}

		this.collapsing = false;

		if (count > 0 && !world.isRemote) {
			world.spawnEntityInWorld(new EntityFallingDripstone(world, x + 0.5D, y + 0.5D, z + 0.5D, meta, count));
		}
	}

	private void updateState(World world, int x, int y, int z) {
		boolean pointingUp = up.getBooleanValue(world, x, y, z);

		int count = countDripstone(world, x, y, z, pointingUp, pointingUp, false);

		DripstoneState state = switch (count) {
			case 1 -> DripstoneState.Tip;
			case 2 -> DripstoneState.Frustum;
			default -> DripstoneState.Middle;
		};

		if (state == DripstoneState.Middle && countDripstone(world, x, y, z, pointingUp, !pointingUp, false) == 1) {
			state = DripstoneState.Base;
		}

		int dy = pointingUp ? 1 : -1;

		if (state == DripstoneState.Tip && countDripstone(world, x, y + dy, z, !pointingUp, pointingUp, false) > 0) {
			state = DripstoneState.TipMerge;
		}

		int meta = this.up.getMetaPrimitive(pointingUp, 0);
		meta = this.state.getMeta(state, meta);

		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

	@Override
	public void addCollisionBoxesToList(
		World worldIn, int x, int y, int z, AxisAlignedBB mask,
		List<AxisAlignedBB> list, Entity collider
	) {
		this.setBlockBoundsBasedOnState(worldIn, x, y, z);
		super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		DripstoneState state = this.state.getValue(world, x, y, z);

		int shrinkage = switch (state) {
			case Base -> 0;
			case Middle -> 1;
			case Frustum -> 2;
			case Tip, TipMerge -> 3;
		};

		float pixel = 0.0625f;
		float offset = (float) (shrinkage + 2) * pixel;

		if (state == DripstoneState.Tip) {
			boolean pointingUp = this.up.getBooleanValue(world, x, y, z);

			return AxisAlignedBB.getBoundingBox(
				x + offset, y + (pointingUp ? 0F : pixel * 5), z + offset,
				x + (1 - offset), y + (pointingUp ? pixel * 11 : 1.0F), z + (1 - offset)
			);
		} else {
			return AxisAlignedBB.getBoundingBox(x + offset, y, z + offset, x + (1 - offset), y + 1.0F, z + (1 - offset));
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
		DripstoneState state = this.state.getValue(access, x, y, z);

		int shrinkage = switch (state) {
			case Base -> 0;
			case Middle -> 1;
			case Frustum -> 2;
			case Tip, TipMerge -> 3;
		};

		float pixel = 0.0625f;

		float offset = (float) (shrinkage + 2) * pixel;

		if (state == DripstoneState.Tip) {
			boolean pointingUp = this.up.getBooleanValue(access, x, y, z);

			this.setBlockBounds(offset, pointingUp ? 0F : pixel * 5, offset, 1 - offset, pointingUp ? pixel * 11 : 1.0F, 1 - offset);
		} else {
			this.setBlockBounds(offset, 0, offset, 1 - offset, 1.0F, 1 - offset);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		boolean pointingUp = up.getBooleanValue(world, x, y, z);

		int baseY = y - (pointingUp ? 1 : -1);

		if (world.isSideSolid(x, baseY, z, pointingUp ? ForgeDirection.UP : ForgeDirection.DOWN)) return true;

		return countDripstone(world, x, y, z, pointingUp, !pointingUp, false) > 1;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		DripstoneState state = this.state.getValue(meta);
		boolean pointingUp = up.getValuePrimitive(meta);

		return pointingUp ? state.upIcon : state.downIcon;
	}

	@Override
	public int getRenderType() {
		return RenderIDs.POINTED_DRIPSTONE;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public String getItemIconName() {
		return "pointed_dripstone";
	}

	/**
	 * Returns the number of contiguous down-pointing dripstone blocks starting from
	 * the base and scanning downward. Returns at most 12 to bound the search.
	 */
	private int stalactiteLength(World world, int x, int baseY, int z) {
		int count = 0;
		int scanY = baseY;

		while (count <= 11) {
			if (world.getBlock(x, scanY, z) != this) break;
			if (up.getBooleanValue(world, x, scanY, z)) break;
			count++;
			scanY--;
		}

		return count;
	}

	/**
	 * Scans downward from the base to find the Y coordinate of the tip.
	 * Returns -1 if no tip is found within 11 blocks or if a non-stalactite block is encountered.
	 */
	private int findStalactiteTipY(World world, int x, int baseY, int z) {
		int scanY = baseY;

		for (int i = 0; i <= 11; i++) {
			if (world.getBlock(x, scanY, z) != this) return -1;
			if (up.getBooleanValue(world, x, scanY, z)) return -1;

			DripstoneState s = state.getValue(world, x, scanY, z);
			if (s == DripstoneState.Tip || s == DripstoneState.TipMerge) return scanY;

			scanY--;
		}

		return -1;
	}

	/**
	 * Scans upward from the tip to find the Y coordinate of the base.
	 * Returns -1 if no base is found within 11 blocks or if a non-stalactite block is encountered.
	 */
	private int findStalactiteTopOffset(World world, int x, int tipY, int z) {
		int scanY = tipY;

		int i;

		for (i = 0; i <= 11; i++) {
			if (world.getBlock(x, scanY, z) != this) break;
			if (up.getBooleanValue(world, x, scanY, z)) break;

			scanY++;
		}

		return i - 1;
	}

	/**
	 * Returns the Forge {@link Fluid} at the given position if it is a fluid source block
	 * (meta == 0), or {@code null} if the block is not a fluid or is a flowing fluid.
	 */
	@Nullable
	private Fluid fluidSourceAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		if (block.getMaterial() != Material.water && block.getMaterial() != Material.lava) return null;
		if (world.getBlockMetadata(x, y, z) != 0) return null;

		return FluidRegistry.lookupFluidForBlock(block);
	}

	private final BlockStatePool pool = new BlockStatePool(4);

	/**
	 * Returns true if a block allows drip to pass through it.
	 * Covers air, ladders, open trapdoors, signs, torches, and any block with no collision box.
	 */
	private boolean isPassable(World world, int x, int y, int z, Block block) {
		if (block instanceof BlockTrapDoor) {
			try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
				return state.getPropertyValue("open");
			}
		}

		return block.getCollisionBoundingBoxFromPool(world, x, y, z) == null;
	}

	/**
	 * Scans downward from just below the tip to find a cauldron within 10 blocks.
	 * Stops early if a non-passable, non-target block is encountered.
	 * Returns the Y coordinate of the cauldron, or -1 if none is reachable.
	 */
	private int findCauldronY(World world, int x, int tipY, int z) {
		for (int y = tipY - 1; y >= tipY - 10; y--) {
			Block b = world.getBlock(x, y, z);

			if (b instanceof BlockCauldron || b instanceof BlockCauldronTileEntity) return y;

			if (!isPassable(world, x, y, z, b)) return -1;
		}

		return -1;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isRemote) return;
		if (up.getBooleanValue(world, x, y, z)) return;
		if (countDripstone(world, x, y, z, false, true, false) > 1) return;

		// Mud→clay: the mud sits above the solid support block (y+1), so at y+2.
		// Not affected by length restriction and runs independently of fluid presence.
		if (!world.provider.isHellWorld && ModBlocks.MUD.isEnabled()) {
			if (world.getBlock(x, y + 2, z) == ModBlocks.MUD.get() && rand.nextInt(256) < 45) {
				world.setBlock(x, y + 2, z, Blocks.clay, 0, 3);
			}
		}

		if (stalactiteLength(world, x, y, z) > 10) return;

		// The fluid source must be 2 blocks above the base (1 block above the solid support).
		Fluid fluid = fluidSourceAt(world, x, y + 2, z);
		if (fluid == null) return;

		int tipY = findStalactiteTipY(world, x, y, z);
		if (tipY < 0) return;

		int cauldronY = findCauldronY(world, x, tipY, z);
		if (cauldronY < 0) return;

		DripOperationRegistry.runOperations(world, x, tipY, z, x, cauldronY, z, fluid);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (rand.nextInt(40) != 0) return;

		if (up.getBooleanValue(world, x, y, z)) return;

		DripstoneState currentState = state.getValue(world, x, y, z);
		if (currentState != DripstoneState.Tip && currentState != DripstoneState.TipMerge) return;

		// TipMerge is the analog of a waterlogged tip — no drip particles
		if (currentState == DripstoneState.TipMerge) return;

		int topOffset = findStalactiteTopOffset(world, x, y, z);
		if (topOffset < 0 || topOffset >= 11) return;

		// The fluid source must be 2 blocks above the base (1 block above the solid support).
		Fluid fluid = fluidSourceAt(world, x, y + topOffset + 2, z);
		if (fluid == null) {
			// Fluid-less = 3x slower drips
			if (rand.nextInt(3) != 0) return;

			fluid = world.provider.isHellWorld ? FluidRegistry.LAVA : FluidRegistry.WATER;
		}

		int color;

		if (fluid == FluidRegistry.WATER) {
			RGBColor mult = RGBColor.fromRGB(Blocks.water.colorMultiplier(world, x, y + topOffset + 2, z));
			RGBColor base = RGBColor.fromRGB(MapColor.waterColor.colorValue);

			base.red = base.red * mult.red / 255;
			base.green = base.green * mult.green / 255;
			base.blue = base.blue * mult.blue / 255;

			color = base.toIntRGB();
		} else if (fluid == FluidRegistry.LAVA) {
			color = 0xff7813;
		} else {
			color = fluid.getColor(world, x, y + topOffset + 2, z);
		}

		setBlockBoundsBasedOnState(world, x, y, z);

		double px = x + minX + (maxX - minX) * 0.5;
		double pz = z + minZ + (maxZ - minZ) * 0.5;

		CustomParticles.spawnDrippingParticle(world, px, y + minY - 0.05f, pz, color | 0xFF000000);
	}
}
