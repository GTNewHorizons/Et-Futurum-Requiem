package ganymedes01.etfuturum.world.end.gen;

import ganymedes01.etfuturum.core.utils.helpers.BlockPos;
import ganymedes01.etfuturum.core.utils.structurenbt.BlockStateContainer;
import ganymedes01.etfuturum.core.utils.structurenbt.EFRBlockStateConverter;
import ganymedes01.etfuturum.core.utils.structurenbt.NBTStructure;
import ganymedes01.etfuturum.entities.EntityShulker;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Extends NBTStructure to provide vanilla-correct 4-rotation placement for End city structures.
 * <p>
 * EFR's base NBTStructure uses transpose/mirror transforms for WEST/EAST which are NOT true rotations.
 * This class bypasses the pre-baked buildMaps and applies proper CW rotation to block positions,
 * while reusing the existing palette system for correct block meta rotation.
 * <p>
 * Rotation indices (matching vanilla):
 * 0 = NONE (0°)
 * 1 = CW_90 (90° clockwise when viewed from above)
 * 2 = CW_180 (180°)
 * 3 = CCW_90 (270° clockwise = 90° counterclockwise)
 * <p>
 * Meta rotation uses EFR's ForgeDirection palette system:
 * rot0 → NORTH palette, rot1 → WEST palette, rot2 → SOUTH palette, rot3 → EAST palette
 */
public class EndCityTemplate extends NBTStructure {

	/**
	 * Maps vanilla rotation index (0-3) to EFR palette rotation index (0-3).
	 * EFR palette indices: 0=NORTH, 1=SOUTH, 2=WEST, 3=EAST
	 * Vanilla rotations: 0=NONE(0°), 1=CW90, 2=180°, 3=CCW90
	 * Meta rotation mapping:
	 * NORTH=0° → palette 0
	 * WEST=CW90 → palette 2
	 * SOUTH=180° → palette 1
	 * EAST=CCW90 → palette 3
	 */
	private static final int[] VANILLA_ROT_TO_EFR_PALETTE = {0, 2, 1, 3};

	private final int origSizeX;
	private final int origSizeY;
	private final int origSizeZ;

	public EndCityTemplate(String loc) {
		super(loc, EFRBlockStateConverter.INSTANCE);
		BlockPos size = getSize(ForgeDirection.NORTH);
		this.origSizeX = size.getX();
		this.origSizeY = size.getY();
		this.origSizeZ = size.getZ();
	}

	/**
	 * Get the transformed size for a vanilla rotation.
	 */
	public BlockPos getTransformedSize(int rotation) {
		if (rotation == 1 || rotation == 3) {
			return new BlockPos(origSizeZ, origSizeY, origSizeX);
		}
		return new BlockPos(origSizeX, origSizeY, origSizeZ);
	}

	/**
	 * Transform a local block position by a vanilla rotation.
	 * Matches vanilla's transformedBlockPos with Mirror.NONE.
	 */
	public static BlockPos transformPos(int x, int y, int z, int rotation, int sizeX, int sizeZ) {
		switch (rotation) {
			case 1: // CW_90: (x,y,z) → (-z, y, x) shifted by sizeZ-1
				return new BlockPos(sizeZ - 1 - z, y, x);
			case 2: // CW_180: (x,y,z) → (-x, y, -z) shifted by (sizeX-1, sizeZ-1)
				return new BlockPos(sizeX - 1 - x, y, sizeZ - 1 - z);
			case 3: // CCW_90: (x,y,z) → (z, y, -x) shifted by sizeX-1
				return new BlockPos(z, y, sizeX - 1 - x);
			default: // NONE
				return new BlockPos(x, y, z);
		}
	}

	/**
	 * Rotate an offset/displacement vector by a vanilla rotation.
	 * Used for computing connected positions between pieces.
	 */
	public static BlockPos rotateOffset(BlockPos offset, int rotation) {
		int dx = offset.getX();
		int dy = offset.getY();
		int dz = offset.getZ();
		switch (rotation) {
			case 1: // CW_90
				return new BlockPos(-dz, dy, dx);
			case 2: // CW_180
				return new BlockPos(-dx, dy, -dz);
			case 3: // CCW_90
				return new BlockPos(dz, dy, -dx);
			default: // NONE
				return offset;
		}
	}

	/**
	 * Add two rotation values (mod 4).
	 */
	public static int addRotation(int a, int b) {
		return (a + b) % 4;
	}

	/**
	 * Compute the bounding box for this template placed at the given position with the given rotation.
	 * Matches vanilla's StructureComponentTemplate.setBoundingBoxFromTemplate().
	 */
	public StructureBoundingBox computeBoundingBox(BlockPos pos, int rotation) {
		BlockPos tSize = getTransformedSize(rotation);
		int tsx = tSize.getX();
		int tsy = tSize.getY();
		int tsz = tSize.getZ();

		// Start with BB at origin
		int minX = 0, minZ = 0;
		int maxX = tsx, maxZ = tsz;

		// Apply rotation offset (matching vanilla)
		switch (rotation) {
			case 1: // CW_90
				minX = -tsx;
				maxX = 0;
				break;
			case 2: // CW_180
				minX = -tsx;
				maxX = 0;
				minZ = -tsz;
				maxZ = 0;
				break;
			case 3: // CCW_90
				minZ = -tsz;
				maxZ = 0;
				break;
			default:
				break;
		}

		// Offset by template position
		return new StructureBoundingBox(
				pos.getX() + minX, pos.getY(), pos.getZ() + minZ,
				pos.getX() + maxX, pos.getY() + tsy - 1, pos.getZ() + maxZ
		);
	}

	/**
	 * Place this template in the world with proper vanilla rotation.
	 *
	 * @param world     The world to place in
	 * @param rand      Random instance
	 * @param pos       The template position (vanilla templatePosition)
	 * @param rotation  Vanilla rotation (0-3)
	 * @param overwrite If true, replace all blocks including air. If false, skip air blocks (insert mode).
	 * @param clipBox   Optional bounding box to clip placement to (can be null)
	 */
	public void placeInWorld(World world, Random rand, BlockPos pos, int rotation, boolean overwrite, StructureBoundingBox clipBox) {
		int efrPaletteIdx = VANILLA_ROT_TO_EFR_PALETTE[rotation];
		int paletteIndex = rand.nextInt(getPaletteCount());
		Map<Integer, BlockStateContainer> palette = getBuildPalettes()[paletteIndex][efrPaletteIdx];

		NBTTagList blocksList = getCompound().getTagList("blocks", 10);

		// Separate structure blocks and entities for deferred processing
		Map<BlockPos, String> structureBlocks = new LinkedHashMap<>();
		Map<BlockPos, BlockStateContainer> entitySet = new LinkedHashMap<>();
		Map<BlockPos, BlockStateContainer> normalBlocks = new LinkedHashMap<>();

		// First pass: categorize all blocks
		for (int i = 0; i < blocksList.tagCount(); i++) {
			NBTTagCompound comp = blocksList.getCompoundTagAt(i);
			BlockPos rawPos = getPosFromTagList(comp.getTagList("pos", 3));
			BlockPos transformedPos = transformPos(rawPos.getX(), rawPos.getY(), rawPos.getZ(),
					rotation, origSizeX, origSizeZ);

			int stateIdx = comp.getInteger("state");
			BlockStateContainer state = palette.get(stateIdx);

			if (state == null) continue;

			boolean isStructureBlock = false;
			if (comp.hasKey("nbt", 10)) {
				NBTTagCompound nbt = comp.getCompoundTag("nbt");
				if (nbt.getString("id").equals("minecraft:structure_block")) {
					isStructureBlock = true;
					structureBlocks.put(transformedPos, nbt.getString("metadata"));
				} else if (state.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY) {
					// Handle tile entity NBT via parent's method
					getNBTAction(transformedPos, state, nbt, getFacingFromInt(efrPaletteIdx));
				}
			}

			if (!isStructureBlock) {
				if (state.getType() == BlockStateContainer.BlockStateType.ENTITY) {
					entitySet.put(transformedPos, state);
				} else {
					normalBlocks.put(transformedPos, state);
				}
			}
		}

		// Process structure blocks (data markers)
		for (Map.Entry<BlockPos, String> entry : structureBlocks.entrySet()) {
			BlockPos localPos = entry.getKey();
			String data = entry.getValue();
			BlockStateContainer belowState = localPos.getY() <= 0 ? null : normalBlocks.get(localPos.down());
			BlockStateContainer result = setStructureBlockAction(localPos, belowState, data, getFacingFromInt(efrPaletteIdx));
			if (result != null) {
				if (result.getType() == BlockStateContainer.BlockStateType.ENTITY) {
					entitySet.put(localPos, result);
				} else {
					normalBlocks.put(localPos, result);
				}
			}
		}

		// Place all normal blocks
		for (Map.Entry<BlockPos, BlockStateContainer> entry : normalBlocks.entrySet()) {
			BlockPos localPos = entry.getKey();
			BlockStateContainer state = entry.getValue();

			int wx = pos.getX() + localPos.getX();
			int wy = pos.getY() + localPos.getY();
			int wz = pos.getZ() + localPos.getZ();

			if (clipBox != null && !clipBox.isVecInside(wx, wy, wz)) continue;

			// Skip air blocks in insert mode
			if (!overwrite && state.getBlock() == Blocks.air) continue;

			placeBlock(world, wx, wy, wz, state);
		}

		// Place entities last (so item frames don't pop off, shulkers have blocks to attach to)
		for (Map.Entry<BlockPos, BlockStateContainer> entry : entitySet.entrySet()) {
			BlockPos localPos = entry.getKey();
			BlockStateContainer state = entry.getValue();

			int wx = pos.getX() + localPos.getX();
			int wy = pos.getY() + localPos.getY();
			int wz = pos.getZ() + localPos.getZ();

			if (clipBox != null && !clipBox.isVecInside(wx, wy, wz)) continue;

			Entity entity = state.createNewEntity(world);
			if (entity != null) {
				if (state.getCompound() != null) {
					entity.readFromNBT(state.getCompound());
				}
				entity.setPosition(wx + 0.5D, wy, wz + 0.5D);
				if (entity instanceof EntityHanging) {
					EntityHanging hanging = (EntityHanging) entity;
					ForgeDirection hangingDir = converter.getItemFrameDirFromRotation(hanging.hangingDirection);
					hanging.field_146063_b = MathHelper.floor_double(wx) + hangingDir.offsetX;
					hanging.field_146064_c = MathHelper.floor_double(wy);
					hanging.field_146062_d = MathHelper.floor_double(wz) + hangingDir.offsetZ;
				}
				world.spawnEntityInWorld(entity);
			}
		}
	}

	/**
	 * Place a single block in the world with tile entity handling.
	 */
	private void placeBlock(World world, int x, int y, int z, BlockStateContainer state) {
		Block block = state.getBlock();
		if (block == null) return;

		world.setBlock(x, y, z, block, state.getMeta(), 2);

		if (state.getType() == BlockStateContainer.BlockStateType.BLOCK_ENTITY) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null) {
				NBTTagCompound nbt = state.getCompound() != null ? (NBTTagCompound) state.getCompound().copy() : new NBTTagCompound();
				nbt.setInteger("x", x);
				nbt.setInteger("y", y);
				nbt.setInteger("z", z);
				te.blockType = block;
				te.blockMetadata = state.getMeta();
				te.readFromNBT(nbt);
				te.markDirty();
				if (te instanceof IInventory && state.getLootTable() != null) {
					WeightedRandomChestContent.generateChestContents(
							world.rand, state.getLootTable().getItems(world.rand),
							(IInventory) te, state.getLootTable().getCount(world.rand)
					);
				}
			}
		}
	}

	/**
	 * Handle End city structure block data markers.
	 * "Sentry" → spawn shulker
	 * "Chest" → assign loot table to chest below
	 * "Elytra" → place item frame (with elytra if enabled)
	 */
	@Override
	public BlockStateContainer setStructureBlockAction(BlockPos pos, BlockStateContainer below, String data, ForgeDirection facing) {
		if (data.startsWith("Sentry")) {
			NBTTagCompound comp = new NBTTagCompound();
			comp.setByte("Color", (byte) 16); // Undyed
			return new BlockStateContainer(EntityShulker.class, comp);
		}
		if (data.startsWith("Chest")) {
			if (below != null) {
				below.setLootTable(ChestGenHooks.getInfo(EndCityLoot.END_CITY_TREASURE));
			}
			return new BlockStateContainer(Blocks.air, 0);
		}
		if (data.startsWith("Elytra")) {
			NBTTagCompound comp = new NBTTagCompound();
			comp.setByte("Direction", (byte) converter.getItemFrameRotationFromDir(facing));
			BlockStateContainer frameState = new BlockStateContainer(EntityItemFrame.class, comp);
			return frameState;
		}
		return new BlockStateContainer(Blocks.air, 0);
	}
}
