package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.lib.Reference;
import ganymedes01.etfuturum.network.WoodSignOpenMessage;
import ganymedes01.etfuturum.network.SignUpdateMessage;
import ganymedes01.etfuturum.ducks.ISign;
import ganymedes01.etfuturum.recipes.ModRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
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
		ISign iSign = (ISign) tileEntity;

		if (iSign.isWaxed()) {
			iSign.playWaxOnSound(world, x, y, z);
			return true;
		}

		ItemStack heldStack = player.getCurrentEquippedItem();
		if (heldStack != null && IDegradable.isWaxableMaterial(heldStack)) {
			// Wax the sign
			if (!world.isRemote) {
				iSign.setWaxed(true);
				tileEntity.markDirty();
				world.markBlockForUpdate(x, y, z);
				syncSignState(world, x, y, z, (TileEntitySign) tileEntity, iSign);
				if (!player.capabilities.isCreativeMode && --heldStack.stackSize <= 0) {
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}
				player.inventoryContainer.detectAndSendChanges();
			}
			iSign.spawnWaxOnEffects(world, x, y, z);
			return true;
		}

		// Dye the sign
		if (heldStack != null) {
			int dyeId = getDyeColorFromStack(heldStack);
			// Undyed signs are already black (15)
			int currentDyeId = iSign.getDyeId() < 0 ? 15 : iSign.getDyeId();
			if (dyeId >= 0 && currentDyeId != dyeId) {
				if (!world.isRemote) {
					iSign.setDyeId(dyeId);
					tileEntity.markDirty();
					world.markBlockForUpdate(x, y, z);
					syncSignState(world, x, y, z, (TileEntitySign) tileEntity, iSign);
					if (!player.capabilities.isCreativeMode && --heldStack.stackSize <= 0) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}
					player.inventoryContainer.detectAndSendChanges();
				}
				else {
					world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, Reference.MCAssetVer + ":item.dye.use", 1.0F, 1.0F, false);
				}

				return true;
			}
		}

		// Open edit GUI (server only)
		if (world.isRemote || !(player instanceof EntityPlayerMP) || player instanceof FakePlayer) {
			return true;
		}

		// Route all signs through custom GUI for double-sided editing
		WoodSignOpenMessage.openEditor((EntityPlayerMP) player, (TileEntitySign) tileEntity, isPlayerOnFrontSide(world, x, y, z, player));
		return true;
	}

	// Check which side of the sign the player should edit when the sign is clicked
	private static boolean isPlayerOnFrontSide(World world, int x, int y, int z, EntityPlayer player) {
		int meta = world.getBlockMetadata(x, y, z);
		ISign sign = (ISign) world.getTileEntity(x, y, z);

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

	private static int getDyeColorFromStack(ItemStack stack) {
		for (int i = 0; i < ModRecipes.ore_dyes.length; i++) {
			if (EtFuturum.hasDictTag(stack, ModRecipes.ore_dyes[i])) {
				// ore_dyes is indexed in reverse: 0=dyeBlack(15), 1=dyeRed(14), ..., 15=dyeWhite(0)
				return 15 - i;
			}
		}
		return -1;
	}

	// Send current sign state (text, waxed, dye) to all players tracking the chunk
	private static void syncSignState(World world, int x, int y, int z, TileEntitySign signTile, ISign iSign) {
		SignUpdateMessage msg = new SignUpdateMessage(x, y, z,
				signTile.signText, iSign.getSignText(false), iSign.isWaxed(), iSign.getDyeId());
		msg.sendToChunkWatchers((WorldServer) world);
	}
}
