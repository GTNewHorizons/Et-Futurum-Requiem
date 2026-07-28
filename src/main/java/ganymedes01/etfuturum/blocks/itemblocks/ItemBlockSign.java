package ganymedes01.etfuturum.blocks.itemblocks;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.BlockWoodSign;
import ganymedes01.etfuturum.configuration.configs.ConfigSounds;
import ganymedes01.etfuturum.network.WoodSignOpenMessage;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBlockSign extends ItemBlock {
	public ItemBlockSign(Block sign) {
		super(sign);
		this.maxStackSize = Items.sign.getItemStackLimit(new ItemStack(Items.sign));
		if (!(sign instanceof BlockWoodSign)) {
			throw new IllegalArgumentException("ItemBlockSign block must be instance of BlockWoodSign!");
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

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

		if (side == 1 && !World.doesBlockHaveSolidTopSurface(world, x, y - 1, z)) {
			return false;
		}

		if (!player.canPlayerEdit(x, y, z, side, itemStack)) {
			return false;
		} else if (!Blocks.standing_sign.canPlaceBlockAt(world, x, y, z)) {
			return false;
		} else if (world.isRemote) {
			return true;
		} else {
			Block block;
			if (side == 1) {
				int i1 = MathHelper.floor_double((player.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5D) & 15;
				block = field_150939_a; // blockInstance
				world.setBlock(x, y, z, block, i1, 3);
			} else {
				block = ((BlockWoodSign) field_150939_a/*blockInstance*/).getWallSign();
				world.setBlock(x, y, z, block, side, 3);
			}

			//Disable the sound for continuity, so it doesn't play when the event-based player would not
			if (ConfigSounds.fixSilentPlacing)
				world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, block.stepSound.func_150496_b()/*getPlaceSound*/, (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);

			--itemStack.stackSize;
			TileEntityWoodSign tileentitysign = (TileEntityWoodSign) world.getTileEntity(x, y, z);

			if (tileentitysign != null) {
				tileentitysign.func_145912_a(player);
				EtFuturum.networkWrapper.sendTo(new WoodSignOpenMessage(tileentitysign, Block.getIdFromBlock(block), true), (EntityPlayerMP) player);
			}
			return true;
		}
	}
}
