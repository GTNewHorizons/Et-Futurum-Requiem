package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.compat.CoopersMalletCompat;
import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel.BarrelType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class ItemCoopersMallet extends BaseItem {

	public ItemCoopersMallet() {
		setNames("coopers_mallet");
		setMaxStackSize(1);
	}

	@Override
	public String getTextureDomain() {
		return Reference.MOD_ID;
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}

		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityChest chest) {
			return chestToBarrel(world, x, y, z, chest);
		}
		if (tile instanceof TileEntityBarrel barrel) {
			if (barrel.type == BarrelType.VANILLA) {
				return vanillaBarrelToChest(world, x, y, z, barrel);
			}
			if (ModsList.IRON_CHEST.isLoaded()) {
				return CoopersMalletCompat.metalBarrelToChest(world, x, y, z, barrel, player);
			}
			return true;
		}
		if (ModsList.IRON_CHEST.isLoaded()) {
			return CoopersMalletCompat.ironChestToBarrel(tile, player, world, x, y, z);
		}
		return false;
	}

	public static <T extends TileEntity & IInventory> boolean convert(World world, int x, int y, int z, IInventory from,
			T to, Block block, int meta) {
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

	private static boolean chestToBarrel(World world, int x, int y, int z, TileEntityChest chest) {
		if (chest.numPlayersUsing > 0) {
			return false;
		}

		TileEntityBarrel barrel = createBarrel(BarrelType.VANILLA, world);
		if (chest.hasCustomInventoryName()) {
			barrel.setCustomName(chest.getInventoryName());
		}
		return convert(world, x, y, z, chest, barrel, barrel.type.getBlock(), clampFacing(chest.getBlockMetadata()));
	}

	private static boolean vanillaBarrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel) {
		if (barrel.numPlayersUsing > 0) {
			return false;
		}

		TileEntityChest chest = new TileEntityChest();
		if (barrel.hasCustomInventoryName()) {
			chest.func_145976_a(barrel.getInventoryName());
		}
		barrel.upgrading = true;
		int facing = clampFacing(BlockPistonBase.getPistonOrientation(barrel.getBlockMetadata()));
		return convert(world, x, y, z, barrel, chest, Blocks.chest, facing);
	}

	private static TileEntityBarrel createBarrel(BarrelType type, World world) {
		return (TileEntityBarrel) type.getBlock().createTileEntity(world, 0);
	}

	private static int clampFacing(int facing) {
		return Math.max(2, Math.min(5, facing));
	}
}
