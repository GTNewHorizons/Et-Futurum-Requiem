package ganymedes01.etfuturum.compat;

import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import ganymedes01.etfuturum.items.ItemCoopersMallet;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel.BarrelType;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class CoopersMalletCompat {

	public static boolean ironChestToBarrel(TileEntity tile, EntityPlayer player, World world, int x, int y, int z) {
		if (!(tile instanceof TileEntityIronChest chest)) {
			return false;
		}
		if (chest.getNumUsingPlayers() > 0) {
			return false;
		}

		BarrelType target = getBarrelType(chest.getType());
		if (target == null) {
			player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("util.coopers_mallet.no_barrel")));
			return true;
		}

		TileEntityBarrel barrel = (TileEntityBarrel) target.getBlock().createTileEntity(world, 0);
		return ItemCoopersMallet.convert(world, x, y, z, chest, barrel, barrel.type.getBlock(), clampFacing(chest.getFacing()));
	}

	public static boolean metalBarrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel,
			EntityPlayer player) {
		if (barrel.numPlayersUsing > 0) {
			return false;
		}

		IronChestType chestType = getChestType(barrel.type);
		if (chestType == null) {
			player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("util.coopers_mallet.no_chest")));
			return true;
		}

		int facing = clampFacing(BlockPistonBase.getPistonOrientation(barrel.getBlockMetadata()));
		TileEntityIronChest chest = IronChestType.makeEntity(chestType.ordinal());
		chest.setFacing(facing);
		barrel.upgrading = true;
		return ItemCoopersMallet.convert(world, x, y, z, barrel, chest, IronChest.ironChestBlock, chestType.ordinal());
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

	private static IronChestType getChestType(BarrelType barrelType) {
		return switch (barrelType) {
			case IRON -> IronChestType.IRON;
			case GOLD -> IronChestType.GOLD;
			case DIAMOND -> IronChestType.DIAMOND;
			case COPPER -> IronChestType.COPPER;
			case STEEL -> IronChestType.STEEL;
			case OBSIDIAN -> IronChestType.OBSIDIAN;
			case DARKSTEEL -> IronChestType.DARKSTEEL;
			case NETHERITE -> IronChestType.NETHERITE;
			default -> null; 
		};
	}

	private static int clampFacing(int facing) {
		return Math.max(2, Math.min(5, facing));
	}
}
