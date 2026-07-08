package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.entities.EntityCushion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCushion extends BaseItem {

	public ItemCushion() {
		super();
		setNames("cushion");
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (side != 1) return false;

		EntityCushion cushion = new EntityCushion(world, x, y, z, subY);

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
