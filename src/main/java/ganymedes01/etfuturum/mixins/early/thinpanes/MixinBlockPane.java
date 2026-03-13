package ganymedes01.etfuturum.mixins.early.thinpanes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

@Mixin(BlockPane.class)
public abstract class MixinBlockPane extends Block {

    protected MixinBlockPane(Material materialIn) {
        super(materialIn);
    }

    @Inject(
        method = "addCollisionBoxesToList",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/block/BlockPane;canPaneConnectTo(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z",
            ordinal = 3,
            remap = false,
            shift = At.Shift.AFTER),
        cancellable = true)
    private void remapCollisionsBoxes(World worldIn, int posX, int posY, int posZ, AxisAlignedBB bb,
        List<AxisAlignedBB> boxList, Entity entity, CallbackInfo ci, @Local(name = "flag") boolean flag,
        @Local(name = "flag1") boolean flag1, @Local(name = "flag2") boolean flag2,
        @Local(name = "flag3") boolean flag3) {
        if (!flag && !flag1 && !flag2 && !flag3) {
            this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);
            super.addCollisionBoxesToList(worldIn, posX, posY, posZ, bb, boxList, entity);
            ci.cancel();
        }
    }

    @Inject(
        method = "setBlockBoundsBasedOnState",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/block/BlockPane;canPaneConnectTo(Lnet/minecraft/world/IBlockAccess;IIILnet/minecraftforge/common/util/ForgeDirection;)Z",
            ordinal = 3,
            remap = false,
            shift = At.Shift.AFTER),
        cancellable = true)
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int posX, int posY, int posZ, CallbackInfo ci,
        @Local(name = "flag") boolean flag, @Local(name = "flag1") boolean flag1, @Local(name = "flag2") boolean flag2,
        @Local(name = "flag3") boolean flag3, @Local(name = "f") float f, @Local(name = "f1") float f1,
        @Local(name = "f2") float f2, @Local(name = "f3") float f3) {
        if (!flag2 && !flag3 && !flag && !flag1) {
            this.setBlockBounds(f, 0.0F, f2, f1, 1.0F, f3);
            ci.cancel();
        }
    }
}
