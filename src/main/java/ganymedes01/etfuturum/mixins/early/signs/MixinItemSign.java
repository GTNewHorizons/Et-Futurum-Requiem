package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemSign.class)
public class MixinItemSign {
	/**
	 * @author mosesyu1028
	 * @reason Fixes vanilla sign placement to allow replacing replaceable blocks
	 * (tall grass, vines, dead bushes, etc.) matching ItemBlock behavior.
	 */

	@Overwrite
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (side == 0) {
			return false;
		}

		Block clickedBlock = world.getBlock(x, y, z);

		if (clickedBlock != Blocks.vine && clickedBlock != Blocks.tallgrass && clickedBlock != Blocks.deadbush && !clickedBlock.isReplaceable(world, x, y, z)) {
			if (!clickedBlock.getMaterial().isSolid()) {
				return false;
			}

			switch (side) {
				case 1: ++y; break;
				case 2: --z; break;
				case 3: ++z; break;
				case 4: --x; break;
				case 5: ++x; break;
			}
		}
		else {
			// Standing sign when overwriting a replaceable block
			side = 1;
		}

		// When placing a standing sign, verify the block below has a solid top surface
		if (side == 1 && !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
			return false;
		}

		if (!player.canPlayerEdit(x, y, z, side, stack)) {
			return false;
		}
		else if (!Blocks.standing_sign.canPlaceBlockAt(world, x, y, z)) {
			return false;
		}
		else if (world.isRemote) {
			return true;
		}
		else {
			Block block;
			if (side == 1) {
				int rotation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
				block = Blocks.standing_sign;
				world.setBlock(x, y, z, block, rotation, 3);
			}
			else {
				block = Blocks.wall_sign;
				world.setBlock(x, y, z, block, side, 3);
			}

			//Disable the sound for continuity, so it doesn't play when the event-based player would not
			if (ConfigSounds.fixSilentPlacing)
				world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, block.stepSound.func_150496_b()/*getPlaceSound*/, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

			--stack.stackSize;
			TileEntitySign tileentitysign = (TileEntitySign) world.getTileEntity(x, y, z);

			if (tileentitysign != null) {
				player.func_146100_a(tileentitysign);
			}
			return true;
		}
	}
}
