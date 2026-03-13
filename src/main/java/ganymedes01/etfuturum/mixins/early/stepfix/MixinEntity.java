package ganymedes01.etfuturum.mixins.early.stepfix;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow
    @Final
    public AxisAlignedBB boundingBox;
    @Unique
    private AxisAlignedBB etfuturum$savedBB;

    @Inject(
        method = "moveEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/AxisAlignedBB;setBB(Lnet/minecraft/util/AxisAlignedBB;)V",
            ordinal = 0,
            shift = At.Shift.AFTER),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                opcode = Opcodes.GETFIELD,
                target = "Lnet/minecraft/entity/Entity;stepHeight:F",
                ordinal = 1)))
    private void saveOldBoundingBox(double x, double y, double z, CallbackInfo ci) {
        if (boundingBox != null) {
            etfuturum$savedBB = this.boundingBox.copy();
            this.boundingBox.setBB(this.boundingBox.addCoord(x + 0.01D, 0, z - 0.01D));
        }
    }

    @Inject(
        method = "moveEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/AxisAlignedBB;",
            ordinal = 0,
            shift = At.Shift.BEFORE),
        slice = @Slice(
            from = @At(
                value = "FIELD",
                opcode = Opcodes.GETFIELD,
                target = "Lnet/minecraft/entity/Entity;stepHeight:F",
                ordinal = 1)))
    private void restoreOldBoundingBox(double x, double y, double z, CallbackInfo ci) {
        if (boundingBox != null) {
            if (etfuturum$savedBB == null)
                throw new IllegalStateException("A conflict has occured with another mod's transformer");
            this.boundingBox.setBB(etfuturum$savedBB);
            etfuturum$savedBB = null;
        }
    }
}
