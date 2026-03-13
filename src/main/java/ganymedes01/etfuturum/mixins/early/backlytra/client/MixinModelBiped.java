package ganymedes01.etfuturum.mixins.early.backlytra.client;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.client.renderer.entity.elytra.LayerBetterElytra;
import ganymedes01.etfuturum.elytra.IElytraPlayer;

@Mixin(ModelBiped.class)
public class MixinModelBiped {

    @Shadow
    public ModelRenderer bipedHead;
    @Shadow
    public ModelRenderer bipedHeadwear;
    @Shadow
    public ModelRenderer bipedBody;
    @Shadow
    public ModelRenderer bipedRightArm;
    @Shadow
    public ModelRenderer bipedLeftArm;
    @Shadow
    public ModelRenderer bipedRightLeg;
    @Shadow
    public ModelRenderer bipedLeftLeg;

    @Inject(
        method = "setRotationAngles",
        at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;isRiding:Z", ordinal = 0))
    private void setElytraAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scaleFactor, Entity entityIn, CallbackInfo ci) {
        boolean flag = entityIn instanceof IElytraPlayer && ((IElytraPlayer) entityIn).etfu$getTicksElytraFlying() > 4;

        if (flag) {
            limbSwing = ageInTicks;

            this.bipedHead.rotateAngleY = this.bipedHeadwear.rotateAngleY = netHeadYaw * 0.017453292F;
            this.bipedHead.rotateAngleX = this.bipedHeadwear.rotateAngleX = -((float) Math.PI / 4F);

            this.bipedBody.rotateAngleY = 0.0F;
            this.bipedRightArm.rotationPointZ = 0.0F;
            this.bipedRightArm.rotationPointX = -5.0F;
            this.bipedLeftArm.rotationPointZ = 0.0F;
            this.bipedLeftArm.rotationPointX = 5.0F;
            float f = 1.0F;

            f = (float) (entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY
                + entityIn.motionZ * entityIn.motionZ);
            f = f / 0.2F;
            f = f * f * f;

            if (f < 1.0F) {
                f = 1.0F;
            }

            this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F
                * limbSwingAmount
                * 0.5F
                / f;
            this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
            this.bipedRightArm.rotateAngleZ = 0.0F;
            this.bipedLeftArm.rotateAngleZ = 0.0F;
            this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
            this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
                * limbSwingAmount
                / f;
            this.bipedRightLeg.rotateAngleY = 0.0F;
            this.bipedLeftLeg.rotateAngleY = 0.0F;
            this.bipedRightLeg.rotateAngleZ = 0.0F;
            this.bipedLeftLeg.rotateAngleZ = 0.0F;
        }
    }

    @Inject(method = "setRotationAngles", at = @At("RETURN"))
    private void hookVars(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch,
        float scaleFactor, Entity entityIn, CallbackInfo ci) {
        LayerBetterElytra.netHeadYaw = netHeadYaw;
        LayerBetterElytra.headPitch = headPitch;
    }
}
