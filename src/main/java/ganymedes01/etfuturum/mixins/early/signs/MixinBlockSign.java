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

		// Route all signs through our custom GUI for double-sided editing
		TileEntitySign signTile = (TileEntitySign) tileEntity;
		signTile.func_145912_a(player);
		boolean front = isPlayerOnFrontSide(world, x, y, z, player);
		int blockId = Block.getIdFromBlock(signTile.getBlockType());
		EtFuturum.networkWrapper.sendTo(new WoodSignOpenMessage(signTile, blockId, front), (EntityPlayerMP) player);
		return true;
	}

	// Check which side of the sign the player should edit when the sign is clicked
	private static boolean isPlayerOnFrontSide(World world, int x, int y, int z, EntityPlayer player) {
		int meta = world.getBlockMetadata(x, y, z);
		IWaxableSign sign = (IWaxableSign) world.getTileEntity(x, y, z);

		if (sign.isWallSign(world, x, y, z)) {
			double signWallOffset = 0.0625F;
			switch (meta) {
				case 2: return player.posZ < z + 1.0 - signWallOffset;
				case 3: return player.posZ > z + signWallOffset;
				case 4: return player.posX < x + 1.0 - signWallOffset;
				case 5: return player.posX > x + signWallOffset;
				default: return true;
			}
		}
		else {
			double dx = player.posX - (x + 0.5D);
			double dz = player.posZ - (z + 0.5D);
			double angle = meta * Math.PI / 8.0;
			double frontX = -Math.sin(angle);
			double frontZ = Math.cos(angle);
			return dx * frontX + dz * frontZ > 0;
		}
	}
}