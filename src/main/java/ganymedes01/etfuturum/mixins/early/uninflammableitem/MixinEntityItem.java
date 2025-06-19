package ganymedes01.etfuturum.mixins.early.uninflammableitem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import ganymedes01.etfuturum.EtFuturum;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EntityItem.class, priority = 1001)
public abstract class MixinEntityItem extends Entity {

	@Shadow
	public abstract ItemStack getEntityItem();

	public MixinEntityItem(World worldIn) {
		super(worldIn);
	}

	private void makeImmuneToFire(ItemStack stack) {
		if (stack != null) {
			List<String> tags = EtFuturum.getOreStrings(stack);
			if (getEntityItem().getUnlocalizedName().contains("netherite")
					|| tags.contains("oreDebris") || tags.contains("scrapDebris")
					|| tags.contains("ingotNetherite") || tags.contains("blockNetherite")) {
				this.isImmuneToFire = true;
				this.fireResistance = Integer.MAX_VALUE;
			}
		}
	}

	@Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onUpdate()V"))
	private void applyFireImmunity(CallbackInfo ci) {
		if (ticksExisted == 1) {
			makeImmuneToFire(getEntityItem());
		}
	}

	/**
	 * TODO: This should be changed to a WrapOperation
	 * I've tried rearranging all of the motion values in every way I could think of.
	 * Yet no matter what I do changing this to WrapOperation either causes no float or the item to sling out of and bounce on lava.
	 * So this stays as a redirect for now until I can find some in-between
	 */
	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void floatLava(CallbackInfo ci) {
		Block state = this.worldObj.getBlock((int) this.posX, (int) this.posY, (int) this.posZ);
		
		if (isImmuneToFire && state.getMaterial().isLiquid() && state.getMaterial() == Material.lava) {
			// Is item still falling?
			if (this.motionY < -0.15D || Math.abs(this.motionX) > 0.15D || Math.abs(this.motionZ) > 0.15D) {
				this.motionX *= 0.4D;
				this.motionY *= 0.75D;
				this.motionZ *= 0.4D;

			} else {
        		// Find top lava level
        		int blockY = MathHelper.floor_double(this.posY);
        		while (this.worldObj.getBlock((int)this.posX, blockY + 1, (int)this.posZ).getMaterial() == Material.lava) {
        		    blockY++;
        		}
				
				// Liquid height is reversed for some reason
        		float liquidHeight = 0.9f - BlockLiquid.getLiquidHeightPercent(this.worldObj.getBlockMetadata(
        		    MathHelper.floor_double(this.posX),
        		    blockY,
        		    MathHelper.floor_double(this.posZ))
        		);
				
        		double surfaceY = blockY + liquidHeight;
				// Logger.debug("y: " + this.posY +" target: " + surfaceY);
				if (this.posY < surfaceY) {
					this.motionX *= 0.5D;
					this.motionY = 0.05D;
					this.motionZ *= 0.5D;
				} else {
					// Once we reach the target height, float
					this.motionY = 0.04D;
				}
			}
		}
	}

	
	@WrapOperation(method = "onUpdate",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
	private Block noFizzBounce(World instance, int x, int y, int z, Operation<Block> original) { //Returns AIR so the check for lava is false; we do this to remove the fizzing and bouncing
		return isImmuneToFire ? Blocks.air : original.call(instance, x, y, z);
	}

	@Override
	public boolean isBurning() {
		return !isImmuneToFire && super.isBurning();
	}

	@Inject(method = "attackEntityFrom", at = @At(value = "HEAD"), cancellable = true)
	public void attackEntityFrom(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (isImmuneToFire && source.isFireDamage()) {
			cir.setReturnValue(false);
		}
	}
}