package ganymedes01.etfuturum.api.elytra;

import net.minecraft.entity.player.EntityPlayer;

/**
 * A duck for {@link EntityPlayer} that can be used to access Et Futurum Requiem's elytra extensions.
 *
 * <p>
 * Usage:
 * </p>
 * <blockquote><tt>((IElytraPlayer)entityPlayer).etfu$isElytraFlying()</tt></blockquote>
 */
public interface IElytraPlayer {

    /**
     * Returns true if the player is gliding with an elytra. (Equivalent to isFallFlying in 1.12.)
     */
    boolean etfu$isElytraFlying();
}
