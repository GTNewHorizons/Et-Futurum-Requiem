package ganymedes01.etfuturum.client.particle;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SoulSpeedFX extends EtFuturumFXParticle {

    public SoulSpeedFX(World world, double px, double py, double pz, double mX, double mY, double mZ) {
        super(world, px, py, pz, mX, mY, mZ, 60, 8.0F, 0xFFFFFF, "textures/particle/soul.png", 11);
        noClip = true;
    }

    @Override
    public void onUpdate() {
        this.particleAge++;
        if (this.particleAge >= particleMaxAge) {
            this.isDead = true;
        }
    }
}
