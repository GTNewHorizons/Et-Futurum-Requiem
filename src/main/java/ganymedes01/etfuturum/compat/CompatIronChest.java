package ganymedes01.etfuturum.compat;

import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import ganymedes01.etfuturum.items.ItemCoopersMallet;
import ganymedes01.etfuturum.tileentities.TileEntityBarrel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public final class CompatIronChest {

	private CompatIronChest() {
	}

	public static boolean chestToBarrel(World world, int x, int y, int z, TileEntity te) {
		if (!(te instanceof TileEntityIronChest chest) || chest.getNumUsingPlayers() > 0) {
			return false;
		}

		TileEntityBarrel.BarrelType type = matchingBarrel(chest.getType());
		if (type == null || type.getBlock() == null) {
			return false;
		}

		return ItemCoopersMallet.convert(world, x, y, z, chest, new TileEntityBarrel(type), type.getBlock(), chest.getFacing());
	}

	public static boolean barrelToChest(World world, int x, int y, int z, TileEntityBarrel barrel) {
		IronChestType type = matchingChest(barrel.type);
		if (type == null) {
			return false;
		}

		TileEntityIronChest chest = IronChestType.makeEntity(type.ordinal());
		if (chest == null) {
			return false;
		}
		chest.setFacing(ItemCoopersMallet.barrelFacing(world.getBlockMetadata(x, y, z)));

		// Iron chests dont have custom names, so its dropped here 
		return ItemCoopersMallet.convert(world, x, y, z, barrel, chest, IronChest.ironChestBlock, type.ordinal());
	}

	/** Types are matched by name, so anything without a counterpart on both sides is refused. */
	private static TileEntityBarrel.BarrelType matchingBarrel(IronChestType type) {
		for (TileEntityBarrel.BarrelType barrel : TileEntityBarrel.BarrelType.VALUES) {
			if (barrel != TileEntityBarrel.BarrelType.VANILLA && barrel.name().equals(type.name())) {
				return barrel;
			}
		}
		return null;
	}

	private static IronChestType matchingChest(TileEntityBarrel.BarrelType type) {
		if (type == TileEntityBarrel.BarrelType.VANILLA) {
			return null;
		}
		for (IronChestType chest : IronChestType.values()) {
			if (chest.name().equals(type.name())) {
				return chest;
			}
		}
		return null;
	}
}
