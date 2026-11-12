package ganymedes01.etfuturum.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SoulSpeedFX extends EtFuturumFXParticle {

    public SoulSpeedFX(World world, double px, double py, double pz, double mX, double mY, double mZ) {
        super(world, px, py, pz, mX, mY, mZ, 30 + CustomParticles.rand.nextInt(10), 1.4F + CustomParticles.rand.nextFloat(), 0xFFFFFFFF, "textures/particle/soul.png", 11);
        noClip = true;
        currentTexture = 0;
        fadeAway = true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        motionX *= 0.025D;
        motionZ *= 0.025D;
        if (this.particleAge % Math.round(particleMaxAge / 11.0F) == 0) {
            if (currentTexture < 10) {
                this.currentTexture++;
            }
        }
    }
}
