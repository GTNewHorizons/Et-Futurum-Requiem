package ganymedes01.etfuturum.mixins.early.signs;

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

        // TODO: 146100_a calls 145912_a which is used in ItemBlockSign
        // This seems to now instantly updates the sign text on SP on character typed
        // (this behavior is different from when placing a new sign)
        // not sure about MP? need testing 
		TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
		if (tileEntity instanceof TileEntitySign && player instanceof EntityPlayerMP) {
			((EntityPlayerMP) player).func_146100_a(tileEntity);
			return true;
		}

		return false;
	}
}