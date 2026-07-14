package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.network.WoodSignOpenMessage;
import ganymedes01.etfuturum.ducks.IWaxableSign;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Backports modern sign features - editing, waxing
 * Much of waxing code is taken from copper code
 *
 * @author mosesyu1028
 */
@Mixin(BlockSign.class)
public class MixinBlockSign extends Block {

	protected MixinBlockSign(Material materialIn) {
		super(materialIn);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntitySign)) {
			return false;
		}

		// Waxed signs
		IWaxableSign waxableSign = (IWaxableSign) tileEntity;

		if (waxableSign.isWaxed()) {
			waxableSign.playWaxOnSound(world, x, y, z);
			return true;
		}

		ItemStack heldStack = player.getCurrentEquippedItem();
		if (heldStack != null && IDegradable.isWaxableMaterial(heldStack)) {
			// Wax the sign
			if (!world.isRemote) {
				waxableSign.setWaxed(true);
				tileEntity.markDirty();
				world.markBlockForUpdate(x, y, z);
				if (!player.capabilities.isCreativeMode && --heldStack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
				player.inventoryContainer.detectAndSendChanges();
			}
			waxableSign.spawnWaxOnEffects(world, x, y, z);
			return true;
		}

		// Open edit GUI (server only)
		if (world.isRemote || !(player instanceof EntityPlayerMP)) {
			return true;
		}

		// Use custom packet if the sign is modded, otherwise use vanilla
		if (tileEntity instanceof TileEntityWoodSign) {
			TileEntityWoodSign woodSign = (TileEntityWoodSign) tileEntity;
			woodSign.func_145912_a(player);
			int blockId = Block.getIdFromBlock(woodSign.getBlockType());
			EtFuturum.networkWrapper.sendTo(new WoodSignOpenMessage(woodSign, blockId), (EntityPlayerMP) player);
		} else {
			player.func_146100_a(tileEntity);
		}
		return true;
	}
}