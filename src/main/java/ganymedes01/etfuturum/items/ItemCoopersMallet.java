package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.ModBlocks;
import ganymedes01.etfuturum.compat.CompatIronChest;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.configuration.configs.ConfigModCompat;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

/**
 * Converts a chest into a barrel in place, and back, keeping the inventory.
 */
public class ItemCoopersMallet extends BaseItem {

	public ItemCoopersMallet() {
		setNames("coopers_mallet");
		setMaxStackSize(1);
		addTooltip = true;
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}

	/**
	 * Both the chest and the barrel open their GUI on click
	 */
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) {
			return false;
		}

		TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof TileEntityBarrel barrel) {
			if (barrel.numPlayersUsing != 0) {
				return false;
			}
			if (barrel.type == TileEntityBarrel.BarrelType.VANILLA) {
				return barrelToChest(world, x, y, z, barrel, player);
			}
			return ironChestEnabled() && CompatIronChest.barrelToChest(world, x, y, z, barrel);
		}

		if (te instanceof TileEntityChest chest && world.getBlock(x, y, z) == Blocks.chest) {
			return chestToBarrel(world, x, y, z, chest);
		}

		return ironChestEnabled() && CompatIronChest.chestToBarrel(world, x, y, z, te);
	}

	/**
	 * Moves every slot into the replacement tile entity, then swaps the block out for it.
	 * Empty first to prevent dropping items on break
	 * Clearing the block to air first lets a neighboring chest turn back into a single chest.
	 */
	public static <T extends TileEntity & IInventory> boolean convert(World world, int x, int y, int z, IInventory from, T to, Block block, int meta) {
		if (from.getSizeInventory() != to.getSizeInventory()) {
			return false;
		}

		for (int i = 0; i < from.getSizeInventory(); i++) {
			to.setInventorySlotContents(i, from.getStackInSlot(i));
			from.setInventorySlotContents(i, null);
		}

		world.setBlock(x, y, z, Blocks.air, 0, 3);
		world.setBlock(x, y, z, block, meta, 3);
		world.setTileEntity(x, y, z, to);
		to.markDirty();
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	/** Barrel metadata packs the facing into the low bits, the rest is the open state. */
	public static int barrelFacing(int meta) {
		return BlockPistonBase.getPistonOrientation(meta);
	}

	private static boolean ironChestEnabled() {
		return ModsList.IRON_CHEST.isLoaded() && ConfigModCompat.barrelIronChest;
	}

	private static boolean chestToBarrel(World world, int x, int y, int z, TileEntityChest chest) {
		if (!ModBlocks.BARREL.isEnabled() || chest.numPlayersUsing != 0) {
			return false;
		}

		// Only this chest's own slots move; the other half of a double chest keeps its own.
		TileEntityBarrel barrel = new TileEntityBarrel(TileEntityBarrel.BarrelType.VANILLA);
		if (chest.hasCustomInventoryName()) {
			barrel.setCustomName(chest.getInventoryName());
		}

		return convert(world, x, y, z, chest, barrel, ModBlocks.BARREL.get(), world.getBlockMetadata(x, y, z));
	}

	private static boolean barrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel, EntityPlayer player) {
		// Refuses a spot that would make an oversized chest, such as one beside a double chest
		if (!Blocks.chest.canPlaceBlockAt(world, x, y, z)) {
			return false;
		}

		TileEntityChest chest = new TileEntityChest();
		if (barrel.hasCustomInventoryName()) {
			chest.func_145976_a(barrel.getInventoryName());
		}

		int facing = barrelFacing(world.getBlockMetadata(x, y, z));
		if (!convert(world, x, y, z, barrel, chest, Blocks.chest, 0)) {
			return false;
		}

		// call chest specific placed code
		Blocks.chest.onBlockPlacedBy(world, x, y, z, player, new ItemStack(Blocks.chest));

		// lone chest keeps the barrels facing
		if (facing > 1 && !hasAdjacentChest(world, x, y, z)) {
			world.setBlockMetadataWithNotify(x, y, z, facing, 3);
		}
		return true;
	}

	private static boolean hasAdjacentChest(World world, int x, int y, int z) {
		return world.getBlock(x - 1, y, z) == Blocks.chest || world.getBlock(x + 1, y, z) == Blocks.chest
				|| world.getBlock(x, y, z - 1) == Blocks.chest || world.getBlock(x, y, z + 1) == Blocks.chest;
	}
}
