package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.compat.ModsList;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel.BarrelType;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ItemStorageinator extends BaseItem {

	private static final String IRON_CHEST_COMPAT = "ganymedes01.etfuturum.items.StorageinatorCompat";

	public ItemStorageinator() {
		setNames("storageinator");
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
		if (tile instanceof TileEntityChest) {
			return convertChestToBarrel((TileEntityChest) tile, world, x, y, z);
		}
		if (ModsList.IRON_CHEST.isLoaded()) {
			return invokeCompat("convertIronChestToBarrel",
					new Class[] { TileEntity.class, EntityPlayer.class, World.class, int.class, int.class, int.class },
					tile, player, world, x, y, z);
		}
		return false;
	}

	public static boolean convertBarrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel) {
		if (barrel.numPlayersUsing > 0) {
			return false;
		}

		if (barrel.type == BarrelType.VANILLA) {
			return convertVanillaBarrelToChest(world, x, y, z, barrel);
		}
		if (ModsList.IRON_CHEST.isLoaded()) {
			return invokeCompat("convertMetalBarrelToChest",
					new Class[] { World.class, int.class, int.class, int.class, TileEntityBarrel.class },
					world, x, y, z, barrel);
		}
		return false;
	}

	private static boolean convertVanillaBarrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel) {
		int facing = clampFacing(BlockPistonBase.getPistonOrientation(barrel.getBlockMetadata()));
		TileEntityChest newChest = new TileEntityChest();

		barrel.upgrading = true;
		int size = Math.min(barrel.getSizeInventory(), newChest.getSizeInventory());
		for (int i = 0; i < size; i++) {
			ItemStack slot = barrel.getStackInSlot(i);
			if (slot != null) {
				newChest.setInventorySlotContents(i, slot);
				barrel.setInventorySlotContents(i, null);
			}
		}
		if (barrel.hasCustomInventoryName()) {
			setChestCustomName(newChest, barrel.getInventoryName());
		}

		world.setBlock(x, y, z, Blocks.chest, facing, 3);
		world.setTileEntity(x, y, z, newChest);
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	private boolean convertChestToBarrel(TileEntityChest chest, World world, int x, int y, int z) {
		if (chest.numPlayersUsing > 0) {
			return false;
		}

		TileEntityBarrel barrel = createBarrel(BarrelType.VANILLA, world);
		int size = Math.min(chest.getSizeInventory(), barrel.getSizeInventory());
		for (int i = 0; i < size; i++) {
			ItemStack slot = chest.getStackInSlot(i);
			if (slot != null) {
				barrel.setInventorySlotContents(i, slot);
				chest.setInventorySlotContents(i, null);
			}
		}
		if (chest.hasCustomInventoryName()) {
			barrel.setCustomName(chest.getInventoryName());
		}

		int facing = clampFacing(chest.getBlockMetadata());
		world.setBlock(x, y, z, Blocks.air, 0, 3);
		chest.updateContainingBlockInfo();
		chest.checkForAdjacentChests();
		world.setBlock(x, y, z, barrel.type.getBlock(), facing, 3);
		world.setTileEntity(x, y, z, barrel);
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	private static boolean invokeCompat(String methodName, Class<?>[] paramTypes, Object... args) {
		try {
			Class<?> compat = Class.forName(IRON_CHEST_COMPAT);
			Method method = compat.getMethod(methodName, paramTypes);
			return method.invoke(null, args) instanceof Boolean result && result;
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| RuntimeException ignored) {
			return false;
		}
	}

	private static void setChestCustomName(TileEntityChest chest, String name) {
		for (Field field : TileEntityChest.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) || field.getType() != String.class) {
				continue;
			}
			field.setAccessible(true);
			try {
				field.set(chest, name);
				return;
			} catch (IllegalAccessException ignored) {
			}
		}
	}

	private static TileEntityBarrel createBarrel(BarrelType type, World world) {
		return (TileEntityBarrel) type.getBlock().createTileEntity(world, 0);
	}

	private static int clampFacing(int facing) {
		return Math.max(2, Math.min(5, facing));
	}
}