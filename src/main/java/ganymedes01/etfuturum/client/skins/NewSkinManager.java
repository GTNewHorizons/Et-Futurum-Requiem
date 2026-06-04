package ganymedes01.etfuturum.client.skins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import ganymedes01.etfuturum.Tags;
import ganymedes01.etfuturum.api.client.ISkinDownloadCallback;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Stolen from 1.8 and modified to work with 1.7.10
 */
public class NewSkinManager extends SkinManager {

	private final TextureManager textureManager;
	private final File skinFolder;

	public NewSkinManager(SkinManager oldManager, TextureManager textureManager, File skinFolder, MinecraftSessionService sessionService) {
		super(textureManager, skinFolder, sessionService);

		this.textureManager = textureManager;
		this.skinFolder = skinFolder;
	}

	/**
	 * May download the skin if its not in the cache, can be passed a {@link SkinManager.SkinAvailableCallback} for handling
	 * <p>
	 * MCP name: {@code loadSkin}
	 */
	@Override
	public ResourceLocation func_152789_a(final MinecraftProfileTexture texture, final Type type, final SkinManager.SkinAvailableCallback callBack) {
		if (type != Type.SKIN)
			return super.func_152789_a/*loadSkin*/(texture, type, callBack);

		final boolean isSpecialCallBack = callBack instanceof ISkinDownloadCallback;
		final ResourceLocation resLocationOld = new ResourceLocation("skins/" + texture.getHash());
		final ResourceLocation resLocation = new ResourceLocation(Tags.MOD_ID, resLocationOld.getResourcePath());
		ITextureObject itextureobject = textureManager.getTexture(resLocation);

		if (itextureobject != null) {
			if (callBack != null)
				callBack.func_152121_a/*onSkinAvailable*/(type, resLocation);
		} else {
			File file1 = new File(skinFolder, texture.getHash().substring(0, 2));
			File file2 = new File(file1, texture.getHash());
			final NewImageBufferDownload imgDownload = new NewImageBufferDownload();
			ITextureObject imgData = new NewThreadDownloadImageData(file2, texture.getUrl(), field_152793_a/*DEFAULT_SKIN*/, imgDownload, resLocationOld, new IImageBuffer() {

				@Override
				public BufferedImage parseUserSkin(BufferedImage buffImg) {
//                    if (buffImg != null)
//                        PlayerModelManager.analyseTexture(buffImg, resLocation);
					return imgDownload.parseUserSkin(buffImg);
				}

				@Override
				public void func_152634_a() {
					imgDownload.func_152634_a();
					if (callBack != null)
						callBack.func_152121_a/*onSkinAvailable*/(type, isSpecialCallBack ? resLocation : resLocationOld);
				}
			});
			textureManager.loadTexture(resLocation, imgData);
			textureManager.loadTexture(resLocationOld, imgData); // Avoid thrown exception if the image is requested before the download is done
		}

		return isSpecialCallBack ? resLocation : resLocationOld;
	}
}