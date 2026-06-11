package ganymedes01.etfuturum.client.particle;

import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class DeferredBubbleFX extends EntityBubbleFX {

	// Weak refs so particles GC cleanly when EffectRenderer.clearEffects() drops them
	public static final List<WeakReference<DeferredBubbleFX>> DEFERRED_BUBBLES = new ArrayList<>();
	public static boolean isRenderingDeferred = false;

	public DeferredBubbleFX(World world, double x, double y, double z, double mx, double my, double mz) {
		super(world, x, y, z, mx, my, mz);
		DEFERRED_BUBBLES.add(new WeakReference<>(this));
	}

	@Override
	public void renderParticle(Tessellator tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		if (!isRenderingDeferred) return; // skip normal particle pass
		super.renderParticle(tessellator, partialTicks, rx, rxz, rz, ryz, rxy);
	}
}
