package ganymedes01.etfuturum.mixins.early.projectilecollidefix;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityThrowable.class)
public abstract class MixinEntityThrowable extends Entity {

    public MixinEntityThrowable(World worldIn) {
        super(worldIn);
    }

    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    public void checkCollide(CallbackInfo ci) {
        func_145775_I(); // doBlockCollisions
    }
}
