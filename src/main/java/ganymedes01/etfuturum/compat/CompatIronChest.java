package ganymedes01.etfuturum.compat;

import cpw.mods.ironchest.IronChest;
import cpw.mods.ironchest.IronChestType;
import cpw.mods.ironchest.TileEntityIronChest;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public final class CompatIronChest {

	private CompatIronChest() {
	}

	/** The facing of an iron chest, or null if this is not a free iron chest. */
	public static Integer readFacing(TileEntity te) {
		if (!(te instanceof TileEntityIronChest chest) || chest.getNumUsingPlayers() > 0) {
			return null;
		}
		return (int) chest.getFacing();
	}

	/** A fresh iron chest of the item's type, or null if the block is not an iron chest. */
	public static TileEntity newChest(Block block, int damage, int facing) {
		if (block != IronChest.ironChestBlock) {
			return null;
		}

		TileEntityIronChest chest = IronChestType.makeEntity(damage);
		if (chest == null) {
			return null;
		}
		chest.setFacing(facing);
		return chest;
	}
}
