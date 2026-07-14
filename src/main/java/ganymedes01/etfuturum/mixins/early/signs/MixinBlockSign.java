package ganymedes01.etfuturum.mixins.early.signs;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.network.WoodSignOpenMessage;
import ganymedes01.etfuturum.tileentities.TileEntityWoodSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Allows sign editing on right click for all signs, including those added by Et Futurum
 *
 * @author mosesyu1028
 */
@Mixin(BlockSign.class)
public class MixinBlockSign extends Block {

	protected MixinBlockSign(Material materialIn) {
		super(materialIn);
	}

	@Override
	public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return true;
		}

		TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntitySign && player instanceof EntityPlayerMP) {
			// Use custom packet for modded wood signs, otherwise use vanilla path
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

		return false;
	}
}