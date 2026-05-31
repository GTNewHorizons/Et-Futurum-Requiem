package ganymedes01.etfuturum.client;

import ganymedes01.etfuturum.lib.EnumColor;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LayeredColorMaskTexture extends AbstractTexture {

	/**
	 * Access to the Logger, for all your logging needs.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * The location of the texture.
	 */
	private final ResourceLocation textureLocation;
	private final List<String> listTextures;
	private final List<EnumColor> listDyeColors;

	public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> p_i46101_2_, List<EnumColor> p_i46101_3_) {
		textureLocation = textureLocationIn;
		listTextures = p_i46101_2_;
		listDyeColors = p_i46101_3_;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		BufferedImage bufferedimage;

		try {
			BufferedImage bufferedimage1 = readBufferedImage(resourceManager.getResource(textureLocation).getInputStream());
			int i = bufferedimage1.getType();

			if (i == 0)
				i = 6;

			bufferedimage = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), i);
			Graphics graphics = bufferedimage.getGraphics();
			graphics.drawImage(bufferedimage1, 0, 0, null);

			for (int j = 0; j < listTextures.size() && j < listDyeColors.size(); ++j) {
				String s = listTextures.get(j);
				MapColor mapcolor = listDyeColors.get(j).getMapColour();

				if (s != null) {
					InputStream inputstream = resourceManager.getResource(new ResourceLocation(s)).getInputStream();
					BufferedImage bufferedimage2 = readBufferedImage(inputstream);

					if (bufferedimage2.getWidth() == bufferedimage.getWidth() && bufferedimage2.getHeight() == bufferedimage.getHeight() && bufferedimage2.getType() == 6) {
						for (int k = 0; k < bufferedimage2.getHeight(); ++k)
							for (int l = 0; l < bufferedimage2.getWidth(); ++l) {
								int i1 = bufferedimage2.getRGB(l, k);

								if ((i1 & -16777216) != 0) {
									int j1 = (i1 & 16711680) << 8 & -16777216;
									int k1 = bufferedimage1.getRGB(l, k);
									int l1 = multiplyColor(k1, mapcolor.colorValue) & 16777215;
									bufferedimage2.setRGB(l, k, j1 | l1);
								}
							}

						bufferedimage.getGraphics().drawImage(bufferedimage2, 0, 0, null);
					}

					inputstream.close();
				}
			}
		} catch (IOException ioexception) {
			LOGGER.error("Couldn't load layered image", ioexception);
			return;
		}

		TextureUtil.uploadTextureImage(getGlTextureId(), bufferedimage);
	}

	private int multiplyColor(int p_180188_0_, int p_180188_1_) {
		int k = (p_180188_0_ & 16711680) >> 16;
		int l = (p_180188_1_ & 16711680) >> 16;
		int i1 = (p_180188_0_ & 65280) >> 8;
		int j1 = (p_180188_1_ & 65280) >> 8;
		int k1 = (p_180188_0_ & 255) >> 0;
		int l1 = (p_180188_1_ & 255) >> 0;
		int i2 = (int) ((float) k * (float) l / 255.0F);
		int j2 = (int) ((float) i1 * (float) j1 / 255.0F);
		int k2 = (int) ((float) k1 * (float) l1 / 255.0F);
		return p_180188_0_ & -16777216 | i2 << 16 | j2 << 8 | k2;
	}

	private BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
		BufferedImage bufferedimage;

		try {
			bufferedimage = ImageIO.read(imageStream);
		} finally {
			IOUtils.closeQuietly(imageStream);
		}

		return bufferedimage;
	}
}