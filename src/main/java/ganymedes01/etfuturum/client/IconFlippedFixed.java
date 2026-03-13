package ganymedes01.etfuturum.client;

import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.util.IIcon;

/**
 * IconFlipped is broken, toggling FlipV (second boolean) doesn't work at all.
 * I override the broken function, replacing it with the correct code.
 * This bug is not exposed in vanilla since it only uses FlipU
 * Currently unused, was originally made for bubble columns before I realized that BE animates some of them in reverse.
 */
public class IconFlippedFixed extends IconFlipped {

    // Too lazy to AT the super fields atm
    private final IIcon baseIcon;
    private final boolean flipV;

    public IconFlippedFixed(IIcon p_i1560_1_, boolean p_i1560_2_, boolean p_i1560_3_) {
        super(p_i1560_1_, p_i1560_2_, p_i1560_3_);
        this.baseIcon = p_i1560_1_;
        this.flipV = p_i1560_3_;
    }

    @Override
    public float getMinV() {
        return this.flipV ? this.baseIcon.getMaxV() : this.baseIcon.getMinV();
    }
}
