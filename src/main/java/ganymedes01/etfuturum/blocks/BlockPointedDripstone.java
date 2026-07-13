package ganymedes01.etfuturum.blocks;

import java.lang.reflect.Type;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockPropertyTrait;
import com.gtnewhorizon.gtnhlib.blockstate.core.MetaBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.properties.BooleanBlockProperty.BooleanMetaBlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.client.sound.ModSounds;
import ganymedes01.etfuturum.core.utils.Utils;
import ganymedes01.etfuturum.entities.EntityFallingDripstone;
import ganymedes01.etfuturum.lib.RenderIDs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		DripstoneState state = this.state.getValue(world, x, y, z);

		int shrinkage = switch (state) {
            case Base -> 0;
            case Middle -> 1;
            case Frustum -> 2;
            case Tip, TipMerge -> 3;
        };

		float offset = 0.125F + (float) shrinkage * 0.0625F;

		return AxisAlignedBB.getBoundingBox(x + offset, y, z + offset, x + (1 - offset), y + 1.0F, z + (1 - offset));
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

		float offset = 0.125F + (float) shrinkage * 0.0625F;

		this.setBlockBounds(offset, 0, offset, 1 - offset, 1.0F, 1 - offset);
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

}
