package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.entities.EntityCushion;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCushion extends BaseItem {

	public ItemCushion() {
		super();
		setNames("cushion");
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		Block block = world.getBlock(x, y, z);

		if (block == Blocks.snow_layer || block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)) {
			if (side == 0) --y;
			if (side == 1) ++y;
			if (side == 2) --z;
			if (side == 3) ++z;
			if (side == 4) --x;
			if (side == 5) ++x;
		}

		EntityCushion cushion = new EntityCushion(world, x, y, z);

		if (!player.canPlayerEdit(x, y, z, side, stack)) {
			return false;
		} else {
			if (cushion != null && cushion.onValidSurface()) {
				if (!world.isRemote) {
					world.spawnEntityInWorld(cushion);
				}

				--stack.stackSize;
			}

			return true;
		}
	}

}
