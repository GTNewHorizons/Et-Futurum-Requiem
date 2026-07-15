package ganymedes01.etfuturum.client.font;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.util.ResourceLocation;

@Desugar
public record EtfrGlyph(ResourceLocation texture, int texWidth, int texHeight, int cellX, int cellY, int cellW,
                        int cellH, float scale, int advance, int ascent) {
}
