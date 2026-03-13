package ganymedes01.etfuturum.mixins.early.endportal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(value = BlockEndPortal.class)
public class MixinBlockEndPortal extends Block {

    protected MixinBlockEndPortal(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "onBlockAdded", at = @At(value = "HEAD"), cancellable = true)
    public void ignoreOnBlockAdded(World worldIn, int x, int y, int z, CallbackInfo ci) {
        ci.cancel();
    }

    @WrapOperation(
        method = "setBlockBoundsBasedOnState",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockEndPortal;setBlockBounds(FFFFFF)V"))
    public void setNewBlockBoundsBasedOnState(BlockEndPortal instance, float minX, float minY, float minZ, float maxX,
        float maxY, float maxZ, Operation<Void> original) {
        original.call(instance, 0.0F, 0.0F, 0.0F, 1.0F, .75F, 1.0F);
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "getItem", at = @At(value = "HEAD"), cancellable = true)
    public void getActualItem(World worldIn, int x, int y, int z, CallbackInfoReturnable<Item> cir) {
        cir.setReturnValue(Item.getItemFromBlock(this));
    }

    @SideOnly(Side.CLIENT)
    @Redirect(
        method = "registerBlockIcons",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/IIconRegister;registerIcon(Ljava/lang/String;)Lnet/minecraft/util/IIcon;"))
    public IIcon registerObsidianIcon(IIconRegister instance, String s) {
        return Blocks.obsidian.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemIconName() {
        return "end_portal";
    }

}
