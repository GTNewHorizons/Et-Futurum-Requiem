package ganymedes01.etfuturum.mixins.early.backlytra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.elytra.IElytraPlayer;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity {

    @Shadow
    public abstract boolean isClientWorld();

    private static final DamageSource flyIntoWall = (new DamageSource("flyIntoWall")).setDamageBypassesArmor();

    public MixinEntityLivingBase(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "moveEntityWithHeading", at = @At("HEAD"), cancellable = true)
    private void moveElytra(float p_70612_1_, float p_70612_2_, CallbackInfo ci) {
        /* method is named incorrectly in these older mappings, it's really isServerWorld */
        if (this.isClientWorld() && !this.isInWater() && !this.handleLavaMovement()) {
            if (this instanceof IElytraPlayer && ((IElytraPlayer) this).etfu$isElytraFlying()) {
                if (this.motionY > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3 vec3d = this.getLookVec();
                float f = this.rotationPitch * 0.017453292F;
                double d6 = Math.sqrt(vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord);
                double d8 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                double d1 = vec3d.lengthVector();
                float f4 = MathHelper.cos(f);
                f4 = (float) ((double) f4 * (double) f4 * Math.min(1.0D, d1 / 0.4D));
                this.motionY += -0.08D + f4 * 0.06D;

                if (this.motionY < 0.0D && d6 > 0.0D) {
                    double d2 = this.motionY * -0.1D * f4;
                    this.motionY += d2;
                    this.motionX += vec3d.xCoord * d2 / d6;
                    this.motionZ += vec3d.zCoord * d2 / d6;
                }

                if (f < 0.0F) {
                    double d9 = d8 * (-MathHelper.sin(f)) * 0.04D;
                    this.motionY += d9 * 3.2D;
                    this.motionX -= vec3d.xCoord * d9 / d6;
                    this.motionZ -= vec3d.zCoord * d9 / d6;
                }

                if (d6 > 0.0D) {
                    this.motionX += (vec3d.xCoord / d6 * d8 - this.motionX) * 0.1D;
                    this.motionZ += (vec3d.zCoord / d6 * d8 - this.motionZ) * 0.1D;
                }

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.9800000190734863D;
                this.motionZ *= 0.9900000095367432D;
                this.moveEntity(this.motionX, this.motionY, this.motionZ);

                if (this.isCollidedHorizontally && !this.worldObj.isRemote) {
                    double d10 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    double d3 = d8 - d10;
                    float f5 = (float) (d3 * 10.0D - 3.0D);

                    if (f5 > 0.0F) {
                        this.playSound(
                            (int) f5 > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small",
                            1.0F,
                            1.0F);
                        this.attackEntityFrom(flyIntoWall, f5);
                    }
                }

                if (this.onGround && !this.worldObj.isRemote) {
                    ((IElytraPlayer) this).etfu$setElytraFlying(false);
                }
                ci.cancel();
            }
        }
    }
}
