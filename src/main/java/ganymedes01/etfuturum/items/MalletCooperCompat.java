package ganymedes01.etfuturum.items;

import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel.BarrelType;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class MalletCooperCompat {

	public static boolean convertIronChestToBarrel(TileEntity tile, EntityPlayer player, World world, int x, int y,
			int z) {
		if (!(tile instanceof TileEntityIronChest chest)) {
			return false;
		}
		if (chest.getNumUsingPlayers() > 0) {
			return false;
		}

		BarrelType target = getBarrelType(chest.getType());
		if (target == null) {
			player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("util.malletcooper.no_barrel")));
			return false;
		}

		TileEntityBarrel barrel = (TileEntityBarrel) target.getBlock().createTileEntity(world, 0);
		int size = Math.min(chest.getSizeInventory(), barrel.getSizeInventory());
		System.arraycopy(chest.chestContents, 0, barrel.chestContents, 0, size);
		for (int i = 0; i < size; i++) {
			chest.chestContents[i] = null;
		}

		world.setBlock(x, y, z, barrel.type.getBlock(), clampFacing(chest.getFacing()), 3);
		world.setTileEntity(x, y, z, barrel);
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	public static boolean convertMetalBarrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel) {
		if (barrel.numPlayersUsing > 0) {
			return false;
		}

		int facing = clampFacing(BlockPistonBase.getPistonOrientation(barrel.getBlockMetadata()));
		IronChestType chestType = IronChestType.valueOf(barrel.type.name());
		TileEntityIronChest newChest = IronChestType.makeEntity(chestType.ordinal());
		newChest.setFacing(facing);

		barrel.upgrading = true;
		int size = Math.min(barrel.getSizeInventory(), newChest.getSizeInventory());
		System.arraycopy(barrel.chestContents, 0, newChest.chestContents, 0, size);
		for (int i = 0; i < size; i++) {
			barrel.chestContents[i] = null;
		}

		world.setBlock(x, y, z, IronChest.ironChestBlock, newChest.getType().ordinal(), 3);
		world.setTileEntity(x, y, z, newChest);
		world.markBlockForUpdate(x, y, z);
		return true;
	}

	private static BarrelType getBarrelType(IronChestType chestType) {
		return switch (chestType) {
			case WOOD -> BarrelType.VANILLA;
			case COPPER -> BarrelType.COPPER;
			case IRON -> BarrelType.IRON;
			case GOLD -> BarrelType.GOLD;
			case DIAMOND -> BarrelType.DIAMOND;
			case STEEL -> BarrelType.STEEL;
			case OBSIDIAN -> BarrelType.OBSIDIAN;
			case DARKSTEEL -> BarrelType.DARKSTEEL;
			case NETHERITE -> BarrelType.NETHERITE;
			default -> null;
		};
	}

	private static int clampFacing(int facing) {
		return Math.max(2, Math.min(5, facing));
	}
}