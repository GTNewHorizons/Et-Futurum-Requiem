package ganymedes01.etfuturum.mixins.early.backlytra.client;

import net.minecraft.client.entity.AbstractClientPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ganymedes01.etfuturum.elytra.IClientElytraPlayer;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer implements IClientElytraPlayer {

    @Override
    public float getRotateElytraX() {
        return etfuturum$rotateElytraX;
    }

    @Override
    public void setRotateElytraX(float rotateElytraX) {
        this.etfuturum$rotateElytraX = rotateElytraX;
    }

    @Override
    public float getRotateElytraY() {
        return etfuturum$rotateElytraY;
    }

    @Override
    public void setRotateElytraY(float rotateElytraY) {
        this.etfuturum$rotateElytraY = rotateElytraY;
    }

    @Override
    public float getRotateElytraZ() {
        return etfuturum$rotateElytraZ;
    }

    @Override
    public void setRotateElytraZ(float rotateElytraZ) {
        this.etfuturum$rotateElytraZ = rotateElytraZ;
    }

    @Unique
    float etfuturum$rotateElytraX, etfuturum$rotateElytraY, etfuturum$rotateElytraZ;

}
