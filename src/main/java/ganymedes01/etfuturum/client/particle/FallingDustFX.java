package ganymedes01.etfuturum.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class FallingDustFX extends EtFuturumFXParticle {

    float oSize;
    final float rotSpeed;

    public FallingDustFX(World world, double x, double y, double z, int color) {
        super(
            world,
            x,
            y,
            z,
            0,
            0,
            0,
            60 + CustomParticles.rand.nextInt(12),
            1,
            color,
            "textures/particle/particles.png",
            8);
        this.particleScale *= 0.75F;
        this.particleScale *= 0.9F;
        this.oSize = this.particleScale;
        this.particleMaxAge = (int) (32.0D / (Math.random() * 0.8D + 0.2D));
        this.particleMaxAge = (int) ((float) this.particleMaxAge * 0.9F);
        this.rotSpeed = ((float) Math.random() - 0.5F) * 0.1F;
        this.particleAngle = (float) Math.random() * ((float) Math.PI * 2F);
        noClip = false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);

        this.particleAngle += (float) Math.PI * this.rotSpeed * 2.0F;

        if (this.isCollided) {
            this.prevParticleAngle = this.particleAngle = 0.0F;
        }
        this.motionY -= 0.003000000026077032D;
        this.motionY = Math.max(this.motionY, -0.14000000059604645D);
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz,
        float rxy) {
        float f = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge * 32.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        this.particleScale = this.oSize * f;
        super.renderParticle(tessellator, partialTicks, rx, rxz, rz, ryz, rxy);
    }
}
