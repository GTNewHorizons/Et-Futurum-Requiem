package ganymedes01.etfuturum.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class SoulFlameFX extends EtFuturumFXParticle {

    /**
     * the scale of the flame FX
     */
    private final float flameScale;

    public SoulFlameFX(World world, double x, double y, double z, double mX, double mY, double mZ) {
        super(
            world,
            x,
            y,
            z,
            mX,
            mY,
            mZ,
            (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4,
            (particleRand.nextFloat() * 0.5F + 0.5F) * 2.0F,
            0xFFFFFFFF,
            "textures/particle/soul_fire_flame.png",
            1);
        this.motionX = this.motionX * 0.009999999776482582D + mX;
        this.motionY = this.motionY * 0.009999999776482582D + mY;
        this.motionZ = this.motionZ * 0.009999999776482582D + mZ;
        this.flameScale = this.particleScale;
        this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
        this.particleMaxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
        this.noClip = true;
    }

    public SoulFlameFX(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0, 0);
    }

    @Override
    public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz,
        float rxy) {
        float f6 = ((float) this.particleAge + partialTicks) / (float) this.particleMaxAge;
        this.particleScale = this.flameScale * (1.0F - f6 * f6 * 0.5F);
        super.renderParticle(tessellator, partialTicks, rx, rxz, rz, ryz, rxy);
    }

    @Override
    public int getBrightnessForRender(float p_70070_1_) {
        float f1 = ((float) this.particleAge + p_70070_1_) / (float) this.particleMaxAge;

        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        int i = super.getBrightnessForRender(p_70070_1_);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int) (f1 * 15.0F * 16.0F);

        if (j > 240) {
            j = 240;
        }

        return j | k << 16;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
    public float getBrightness(float p_70013_1_) {
        float f1 = ((float) this.particleAge + p_70013_1_) / (float) this.particleMaxAge;

        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        float f2 = super.getBrightness(p_70013_1_);
        return f2 * f1 + (1.0F - f1);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9599999785423279D;
        this.motionY *= 0.9599999785423279D;
        this.motionZ *= 0.9599999785423279D;

        if (this.onGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }
}
