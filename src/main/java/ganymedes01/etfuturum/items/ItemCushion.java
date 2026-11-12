package ganymedes01.etfuturum.items;

import ganymedes01.etfuturum.entities.EntityCushion;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemCushion extends BaseSubtypesItem {

	public ItemCushion() {
		super(getNames());
		setNames("cushion");
	}

	private static String[] getNames() {
		String[] names = new String[ModRecipes.dye_names.length];
		for (int i = 0; i < ModRecipes.dye_names.length; i++) {
			names[i] = ModRecipes.dye_names[i] + "_cushion";
		}
		return names;
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (side != 1) return false;

		EntityCushion cushion = new EntityCushion(world, x, y, z, subY);
		cushion.setDyeColor(stack.getItemDamage() % types.length);

		if (!player.canPlayerEdit(x, y, z, side, stack)) {
			return false;
		} else {
			if (cushion != null && cushion.onValidSurface()) {
				if (!world.isRemote) {
					world.spawnEntityInWorld(cushion);
				}

				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, Blocks.wool.stepSound.func_150496_b(), (Blocks.wool.stepSound.getVolume() + 1.0F) / 2.0F, Blocks.wool.stepSound.getPitch() * 0.8F);
				--stack.stackSize;
			}

			return true;
		}
	}

}
