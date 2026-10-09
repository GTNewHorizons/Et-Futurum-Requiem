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
import java.util.ArrayList;
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
			int layers = Math.min(listTextures.size(), listDyeColors.size());
			List<BufferedImage> layerImages = new ArrayList<>(layers);
			int width = bufferedimage1.getWidth();
			int height = bufferedimage1.getHeight();

			// Size the canvas to the largest layer so higher-resolution masks keep their detail
			for (int j = 0; j < layers; ++j) {
				String s = listTextures.get(j);
				BufferedImage layer = null;

				if (s != null) {
					layer = readBufferedImage(resourceManager.getResource(new ResourceLocation(s)).getInputStream());
					width = Math.max(width, layer.getWidth());
					height = Math.max(height, layer.getHeight());
				}

				layerImages.add(layer);
			}

			// Always ARGB: an optimized base texture may load as palette or grayscale and would lose the dye colours
			BufferedImage base = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			base.getGraphics().drawImage(bufferedimage1, 0, 0, width, height, null);
			bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics graphics = bufferedimage.getGraphics();
			graphics.drawImage(base, 0, 0, null);

			for (int j = 0; j < layers; ++j) {
				BufferedImage layer = layerImages.get(j);

				if (layer != null) {
					MapColor mapcolor = listDyeColors.get(j).getMapColour();

					// Resource packs may ship masks with a different size or colour type than the base image,
					// so normalize them instead of dropping the layer
					BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					mask.getGraphics().drawImage(layer, 0, 0, width, height, null);

					for (int k = 0; k < height; ++k)
						for (int l = 0; l < width; ++l) {
							int i1 = mask.getRGB(l, k);

							if ((i1 & -16777216) != 0) {
								int j1 = (i1 & 16711680) << 8 & -16777216;
								int k1 = base.getRGB(l, k);
								int l1 = multiplyColor(k1, mapcolor.colorValue) & 16777215;
								mask.setRGB(l, k, j1 | l1);
							}
						}

					graphics.drawImage(mask, 0, 0, null);
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