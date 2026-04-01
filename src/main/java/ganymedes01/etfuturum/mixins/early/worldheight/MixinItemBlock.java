package ganymedes01.etfuturum.mixins.early.worldheight;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemBlock.class)
public class MixinItemBlock {

    @ModifyConstant(method = "onItemUse", constant = @Constant(intValue = 255))
    private int getIncreasedWorldHeightMinusOne(int original, ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

        return world.provider.getHeight() - 1;
    }
}
