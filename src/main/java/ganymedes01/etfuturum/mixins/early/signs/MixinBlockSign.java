package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.blocks.IDegradable;
import ganymedes01.etfuturum.network.WoodSignOpenMessage;
import ganymedes01.etfuturum.network.SignUpdateMessage;
import ganymedes01.etfuturum.ducks.ISign;
import ganymedes01.etfuturum.recipes.ModRecipes;
import cpw.mods.fml.common.network.NetworkRegistry;
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
				System.out.println("[SERVER] Waxing sign at " + x + "," + y + "," + z);
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
			if (dyeId >= 0 && iSign.getDyeId() != dyeId) {
				if (!world.isRemote) {
					iSign.setDyeId(dyeId);
					System.out.println("[SERVER] Dyeing sign at " + x + "," + y + "," + z + " dyeId=" + dyeId);
					tileEntity.markDirty();
					world.markBlockForUpdate(x, y, z);
					syncSignState(world, x, y, z, (TileEntitySign) tileEntity, iSign);
					if (!player.capabilities.isCreativeMode && --heldStack.stackSize <= 0) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}
					player.inventoryContainer.detectAndSendChanges();
				}
				else {
					world.playSound(x + 0.5D, y + 0.5D, z + 0.5D, "random.orb", 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F), false);
				}

				return true;
			}
		}

		// Open edit GUI (server only)
		if (world.isRemote || !(player instanceof EntityPlayerMP)) {
			return true;
		}

		// Route all signs through custom GUI for double-sided editing
		TileEntitySign signTile = (TileEntitySign) tileEntity;
		signTile.func_145912_a(player);
		boolean front = isPlayerOnFrontSide(world, x, y, z, player);
		int blockId = Block.getIdFromBlock(signTile.getBlockType());
		System.out.println("[SERVER] MixinBlockSign sending WoodSignOpenMessage: front=" + front
			+ " pos=" + x + "," + y + "," + z + " blockId=" + blockId
			+ " player=" + player.getCommandSenderName()
			+ " existingText0=\"" + signTile.signText[0] + "\"");
		EtFuturum.networkWrapper.sendTo(new WoodSignOpenMessage(signTile, blockId, front), (EntityPlayerMP) player);
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

	// Send current sign state (text, waxed, dye) to all nearby players
	private static void syncSignState(World world, int x, int y, int z, TileEntitySign signTile, ISign iSign) {
		SignUpdateMessage msg = new SignUpdateMessage(x, y, z,
				signTile.signText, iSign.getSignText(false), iSign.isWaxed(), iSign.getDyeId());
		EtFuturum.networkWrapper.sendToAllAround(msg,
				new NetworkRegistry.TargetPoint(world.provider.dimensionId, x + 0.5, y + 0.5, z + 0.5, 64));
	}
}
