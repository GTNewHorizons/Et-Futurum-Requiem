package ganymedes01.etfuturum.mixins.early.unicodepages.client;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ganymedes01.etfuturum.client.font.EtfrGlyph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Brings modern's character set to here.
 */
@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

	private static final String ETFR_ASCII =
            "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000";

	private static final ResourceLocation ETFR_HI_WIDTHS = new ResourceLocation("font/glyph_sizes_hi.bin");
	private static final ResourceLocation ETFR_HANDDRAWN = new ResourceLocation("etfuturum", "font/handdrawn.json");
	private static final Logger ETFR_LOG = LogManager.getLogger("EtFuturum");

	@Shadow protected float posX;
	@Shadow protected float posY;
	@Shadow protected byte[] glyphWidth;
	@Shadow protected int[] charWidth;
	@Shadow public Random fontRandom;
	@Shadow private int[] colorCode;
	@Shadow private boolean unicodeFlag;
	@Shadow private float red;
	@Shadow private float blue;
	@Shadow private float green;
	@Shadow private float alpha;
	@Shadow private boolean randomStyle;
	@Shadow private boolean boldStyle;
	@Shadow private boolean italicStyle;
	@Shadow private boolean underlineStyle;
	@Shadow private boolean strikethroughStyle;

	@Shadow @Final protected ResourceLocation locationFontTexture;

	@Shadow(remap = false) protected abstract void setColor(float r, float g, float b, float a);
	@Shadow(remap = false) protected abstract void bindTexture(ResourceLocation location);
	@Shadow(remap = false) protected abstract void doDraw(float f);
	@Shadow protected abstract float renderCharAtPos(int index, char c, boolean italic);
	@Shadow public abstract int getCharWidth(char c);

	private byte[] etfr$hiWidths;
	private boolean etfr$hiLoaded;
	private Map<Integer, ResourceLocation> etfr$hiPages;

	private EtfrGlyph[] etfr$bmpGlyphs;
	private Map<Integer, EtfrGlyph> etfr$supplementaryGlyphs;
	private boolean etfr$glyphsLoaded;
	private Map<Integer, EtfrGlyph[]> etfr$glyphsByAdvance;


	@Inject(method = "renderStringAtPos", at = @At("HEAD"), cancellable = true)
	private void etfr$renderStringAtPos(String text, boolean isShadow, CallbackInfo ci) {
		if (this.etfr$isSplash()) {
			// FML's SplashFontRenderer.bindTexture only accepts its own font texture and throws
			return;
		}
		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);
			int j;
			int k;

			if (c0 == 167 && i + 1 < text.length()) {
				j = "0123456789abcdefklmnor".indexOf(text.toLowerCase().charAt(i + 1));

				if (j < 16) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					if (j < 0) {
						j = 15;
					}

					if (isShadow) {
						j += 16;
					}

					k = this.colorCode[j];
					setColor((float) (k >> 16) / 255.0F, (float) (k >> 8 & 255) / 255.0F, (float) (k & 255) / 255.0F, this.alpha);
				} else if (j == 16) {
					this.randomStyle = true;
				} else if (j == 17) {
					this.boldStyle = true;
				} else if (j == 18) {
					this.strikethroughStyle = true;
				} else if (j == 19) {
					this.underlineStyle = true;
				} else if (j == 20) {
					this.italicStyle = true;
				} else {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;
					setColor(this.red, this.blue, this.green, this.alpha);
				}

				++i;
			} else {
				int cp = c0;
				boolean supplementary = false;
				if (Character.isHighSurrogate(c0) && i + 1 < text.length()
						&& Character.isLowSurrogate(text.charAt(i + 1))) {
					cp = Character.toCodePoint(c0, text.charAt(i + 1));
					supplementary = true;
				}

				EtfrGlyph glyph = this.etfr$bitmapGlyph(cp);
				if (glyph != null) {
					if (this.randomStyle) {
						glyph = this.etfr$obfuscateGlyph(glyph);
					}
					float f = this.etfr$renderBitmapChar(glyph, this.italicStyle);
					if (this.boldStyle) {
						this.posX += 1.0F;
						this.etfr$renderBitmapChar(glyph, this.italicStyle);
						this.posX -= 1.0F;
						++f;
					}
					this.doDraw(f);
					if (supplementary) {
						++i;
					}
					continue;
				}

				if (supplementary) {
					++i;
					float f1 = this.unicodeFlag ? 0.5F : 1.0F;

                    if (isShadow) {
						this.posX -= f1;
						this.posY -= f1;
					}

					float f = this.etfr$renderCodepoint(cp, this.italicStyle);

					if (isShadow) {
						this.posX += f1;
						this.posY += f1;
					}

					if (this.boldStyle) {
						this.posX += f1;

						if (isShadow) {
							this.posX -= f1;
							this.posY -= f1;
						}

						this.etfr$renderCodepoint(cp, this.italicStyle);
						this.posX -= f1;

						if (isShadow) {
							this.posX += f1;
							this.posY += f1;
						}

						++f;
					}

					this.doDraw(f);
					continue;
				}

				j = ETFR_ASCII.indexOf(c0);

				if (this.randomStyle && j != -1) {
					do {
						k = this.fontRandom.nextInt(this.charWidth.length);
					}
					while (this.charWidth[j] != this.charWidth[k]);

					j = k;
				}

				float f1 = this.unicodeFlag ? 0.5F : 1.0F;
				boolean flag1 = (c0 == 0 || j == -1 || this.unicodeFlag) && isShadow;

				if (flag1) {
					this.posX -= f1;
					this.posY -= f1;
				}

				float f = this.renderCharAtPos(j, c0, this.italicStyle);

				if (flag1) {
					this.posX += f1;
					this.posY += f1;
				}

				if (this.boldStyle) {
					this.posX += f1;

					if (flag1) {
						this.posX -= f1;
						this.posY -= f1;
					}

					this.renderCharAtPos(j, c0, this.italicStyle);
					this.posX -= f1;

					if (flag1) {
						this.posX += f1;
						this.posY += f1;
					}

					++f;
				}

				this.doDraw(f);
			}
		}

		ci.cancel();
	}

	@Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
	private void etfr$getStringWidth(String text, CallbackInfoReturnable<Integer> cir) {
		if (text == null) {
			cir.setReturnValue(0);
			return;
		}

		int i = 0;
		boolean flag = false;

		for (int j = 0; j < text.length(); ++j) {
			char c0 = text.charAt(j);
			int k;

			int cp = c0;
			boolean supplementary = false;
			if (Character.isHighSurrogate(c0) && j + 1 < text.length()
					&& Character.isLowSurrogate(text.charAt(j + 1))) {
				cp = Character.toCodePoint(c0, text.charAt(j + 1));
				supplementary = true;
			}

			// '§' has a glyph (found in nonlatin_european.png), but renderStringAtPos consumes it as a
			// formatting code before glyph lookup; measure it the same way or formatted text over-measures
			boolean formatCode = c0 == 167 && j + 1 < text.length();
			EtfrGlyph glyph = formatCode ? null : this.etfr$bitmapGlyph(cp);
			if (glyph != null) {
				k = glyph.advance();
				if (supplementary) {
					++j;
				}
			} else if (supplementary) {
				k = this.etfr$codepointWidth(cp);
				++j;
			} else {
				k = this.getCharWidth(c0);

				if (k < 0 && j < text.length() - 1) {
					++j;
					c0 = text.charAt(j);

					if (c0 != 108 && c0 != 76) {
						if (c0 == 114 || c0 == 82) {
							flag = false;
						}
					} else {
						flag = true;
					}

					k = 0;
				}
			}

			i += k;

			if (flag && k > 0) {
				++i;
			}
		}

		cir.setReturnValue(i);
	}


	@Inject(method = "getCharWidth", at = @At("HEAD"), cancellable = true)
	private void etfr$getCharWidth(char c, CallbackInfoReturnable<Integer> cir) {
		if (c == 167 || c == ' ') {
			return;
		}

		EtfrGlyph glyph = this.etfr$bitmapGlyph(c);
		if (glyph != null) {
			cir.setReturnValue(glyph.advance());
		}
	}

	@Inject(method = "sizeStringToWidth", at = @At("HEAD"), cancellable = true)
	private void etfr$sizeStringToWidth(String str, int wrapWidth, CallbackInfoReturnable<Integer> cir) {
		int j = str.length();
		int k = 0;
		int l = 0;
		int i1 = -1;

		for (boolean flag = false; l < j; ++l) {
			char c0 = str.charAt(l);

			switch (c0) {
				case 10:
					--l;
					break;
				case 167:
					if (l < j - 1) {
						++l;
						char c1 = str.charAt(l);

						if (c1 != 108 && c1 != 76) {
							if (c1 == 114 || c1 == 82 || etfr$isFormatColor(c1)) {
								flag = false;
							}
						} else {
							flag = true;
						}
					}

					break;
				case 32:
					i1 = l;
				default:
					boolean pair = Character.isHighSurrogate(c0) && l + 1 < j
							&& Character.isLowSurrogate(str.charAt(l + 1));

					if (pair) {
						int cp = Character.toCodePoint(c0, str.charAt(l + 1));
						EtfrGlyph glyph = this.etfr$bitmapGlyph(cp);
						k += glyph != null ? glyph.advance() : this.etfr$codepointWidth(cp);
					} else {
						k += this.getCharWidth(c0);
					}

					if (flag) {
						++k;
					}

					if (pair) {
						if (k <= wrapWidth) {
							++l;
						} else if (l == 0) {
							cir.setReturnValue(2);
							return;
						}
					}
			}

			if (c0 == 10) {
				++l;
				i1 = l;
				break;
			}

			if (k > wrapWidth) {
				break;
			}
		}

		cir.setReturnValue(l != j && i1 != -1 && i1 < l ? i1 : l);
	}

	private static boolean etfr$isFormatColor(char c) {
		return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
	}

	@Inject(method = "trimStringToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
	private void etfr$trimWholeCodepoints(String text, int width, boolean reverse, CallbackInfoReturnable<String> cir) {
		String s = cir.getReturnValue();

		if (s == null || s.isEmpty() || s.length() >= text.length()) {
			return;
		}

		if (!reverse) {
			if (Character.isHighSurrogate(s.charAt(s.length() - 1))
					&& Character.isLowSurrogate(text.charAt(s.length()))) {
				cir.setReturnValue(s.substring(0, s.length() - 1));
			}
		} else {
			int start = text.length() - s.length();
			if (Character.isLowSurrogate(s.charAt(0))
					&& Character.isHighSurrogate(text.charAt(start - 1))) {
				cir.setReturnValue(s.substring(1));
			}
		}
	}

	@Inject(method = "onResourceManagerReload", at = @At("TAIL"))
	private void etfr$onResourceManagerReload(IResourceManager manager, CallbackInfo ci) {
		if (manager == null) {
			// FML's SplashFontRenderer constructor calls onResourceManagerReload(null) on the splash thread
			return;
		}
		try (InputStream in = manager.getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream()) {
			byte[] fresh = ByteStreams.toByteArray(in);
			int n = Math.min(fresh.length, this.glyphWidth.length);
			System.arraycopy(fresh, 0, this.glyphWidth, 0, n);
		} catch (IOException ignored) {
		}

		this.etfr$hiLoaded = false;
		this.etfr$hiWidths = null;
		this.etfr$glyphsLoaded = false;
		this.etfr$bmpGlyphs = null;
		this.etfr$supplementaryGlyphs = null;
		this.etfr$glyphsByAdvance = null;
	}

	private float etfr$renderCodepoint(int cp, boolean italic) {
		int w = this.etfr$hiWidth(cp) & 255;

		if (w == 0) {
			return 0.0F;
		}

		this.bindTexture(this.etfr$hiPageLocation(cp >>> 8));
		int j = w >>> 4;
		int k = w & 15;
		float f = (float) j;
		float f1 = (float) (k + 1);
		float f2 = (float) ((cp & 15) * 16) + f;
		float f3 = (float) (((cp & 255) >> 4) * 16);
		float f4 = f1 - f - 0.02F;
		float f5 = italic ? 1.0F : 0.0F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
		GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
		GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
		GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
		GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
		GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
		GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
		GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
		GL11.glEnd();
		return (f1 - f) / 2.0F + 1.0F;
	}

	private int etfr$codepointWidth(int cp) {
		int w = this.etfr$hiWidth(cp) & 255;

		if (w == 0) {
			return 0;
		}

		int j = w >>> 4;
		int k = w & 15;

		if (k > 7) {
			k = 15;
			j = 0;
		}

		++k;
		return (k - j) / 2 + 1;
	}

	private int etfr$hiWidth(int cp) {
		if (cp < 0x10000) {
			return 0;
		}

		this.etfr$ensureHiWidths();
		byte[] arr = this.etfr$hiWidths;
		int idx = cp - 0x10000;
		return (arr != null && idx < arr.length) ? arr[idx] : 0;
	}

	private void etfr$ensureHiWidths() {
		if (this.etfr$hiLoaded) {
			return;
		}

		this.etfr$hiLoaded = true;

		try (InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(ETFR_HI_WIDTHS).getInputStream()) {
			this.etfr$hiWidths = ByteStreams.toByteArray(in);
		} catch (IOException e) {
			this.etfr$hiWidths = new byte[0];
		}
	}

	private ResourceLocation etfr$hiPageLocation(int page) {
		Map<Integer, ResourceLocation> pages = this.etfr$hiPages;

		if (pages == null) {
			this.etfr$hiPages = pages = new HashMap<>();
		}

		ResourceLocation rl = pages.get(page);

		if (rl == null) {
			rl = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
			pages.put(page, rl);
		}

		return rl;
	}

	private Boolean etfr$splash;

	private boolean etfr$isSplash() {
		Boolean splash = this.etfr$splash;
		if (splash == null) {
			String cls = this.getClass().getName();
			splash = "cpw.mods.fml.client.SplashProgress$SplashFontRenderer".equals(cls)
					|| "gkappa.modernsplash.CustomSplash$SplashFontRenderer".equals(cls);
			this.etfr$splash = splash;
		}
		return splash;
	}

	private Boolean etfr$sga;

	private boolean etfr$isSga() {
		Boolean sga = this.etfr$sga;
		if (sga == null) {
			sga = "textures/font/ascii_sga.png".equals(this.locationFontTexture.getResourcePath());
			this.etfr$sga = sga;
		}
		return sga;
	}

	private boolean etfr$unifontForced() {
		final Minecraft mc = Minecraft.getMinecraft();
		return mc == null || mc.gameSettings == null || mc.gameSettings.forceUnicodeFont;
	}

	private EtfrGlyph etfr$bitmapGlyph(int cp) {
		if (this.etfr$isSplash() || this.etfr$isSga() || this.etfr$unifontForced()) {
			return null;
		}
		this.etfr$ensureBitmapGlyphs();
		if (cp < 0x10000) {
			return this.etfr$bmpGlyphs[cp];
		}
		return this.etfr$supplementaryGlyphs.get(cp);
	}

	/**
	 * Picks a random glyph with the same advance, like modern's obfuscated (§k) style.
	 */
	private EtfrGlyph etfr$obfuscateGlyph(EtfrGlyph glyph) {
		Map<Integer, EtfrGlyph[]> byAdvance = this.etfr$glyphsByAdvance;

		if (byAdvance == null) {
			Map<Integer, List<EtfrGlyph>> grouped = new HashMap<>();
			for (EtfrGlyph g : this.etfr$bmpGlyphs) {
				if (g == null) {
					continue;
				}
				List<EtfrGlyph> group = grouped.computeIfAbsent(g.advance(), k -> new ArrayList<>());
				group.add(g);
			}
			for (EtfrGlyph g : this.etfr$supplementaryGlyphs.values()) {
				List<EtfrGlyph> group = grouped.computeIfAbsent(g.advance(), k -> new ArrayList<>());
				group.add(g);
			}
			byAdvance = new HashMap<>();
			for (Map.Entry<Integer, List<EtfrGlyph>> e : grouped.entrySet()) {
				byAdvance.put(e.getKey(), e.getValue().toArray(new EtfrGlyph[0]));
			}
			this.etfr$glyphsByAdvance = byAdvance;
		}

		EtfrGlyph[] candidates = byAdvance.get(glyph.advance());
		return candidates == null || candidates.length < 2 ? glyph : candidates[this.fontRandom.nextInt(candidates.length)];
	}

	private float etfr$renderBitmapChar(EtfrGlyph g, boolean italic) {
		this.bindTexture(g.texture());
		float u0 = (float) g.cellX() / g.texWidth();
		float u1 = (float) (g.cellX() + g.cellW()) / g.texWidth();
		float v0 = (float) g.cellY() / g.texHeight();
		float v1 = (float) (g.cellY() + g.cellH()) / g.texHeight();
		float w = g.cellW() * g.scale();
		float h = g.cellH() * g.scale();
		float top = this.posY + 7.0F - g.ascent(); // baseline sits at posY + 7 (ASCII ascent)
		float bottom = top + h;
		float left = this.posX;
		float right = this.posX + w;
		float shear = italic ? 1.0F : 0.0F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(u0, v0);
		GL11.glVertex3f(left + shear, top, 0.0F);
		GL11.glTexCoord2f(u0, v1);
		GL11.glVertex3f(left - shear, bottom, 0.0F);
		GL11.glTexCoord2f(u1, v0);
		GL11.glVertex3f(right + shear, top, 0.0F);
		GL11.glTexCoord2f(u1, v1);
		GL11.glVertex3f(right - shear, bottom, 0.0F);
		GL11.glEnd();
		return g.advance();
	}

	private void etfr$ensureBitmapGlyphs() {
		if (this.etfr$glyphsLoaded) {
			return;
		}

		this.etfr$glyphsLoaded = true;
		EtfrGlyph[] bmp = new EtfrGlyph[0x10000];
		Map<Integer, EtfrGlyph> supplementary = new HashMap<>();
		int count = 0;
		IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
		try (InputStreamReader reader = new InputStreamReader(rm.getResource(ETFR_HANDDRAWN).getInputStream(), "UTF-8")) {
			JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
			JsonArray providers = root.getAsJsonArray("providers");
			for (int p = 0; p < providers.size(); p++) {
				count += this.etfr$loadBitmapProvider(providers.get(p).getAsJsonObject(), bmp, supplementary, rm);
			}
		} catch (Exception e) {
			ETFR_LOG.error("Failed to load hand-drawn font glyphs; falling back to unifont", e);
		}

		this.etfr$bmpGlyphs = bmp;
		this.etfr$supplementaryGlyphs = supplementary;
	}

	private int etfr$loadBitmapProvider(JsonObject provider, EtfrGlyph[] bmp, Map<Integer, EtfrGlyph> supplementary, IResourceManager rm) throws IOException {
		String file = provider.get("file").getAsString();
		int height = provider.has("height") ? provider.get("height").getAsInt() : 8;
		int ascent = provider.get("ascent").getAsInt();
		JsonArray charsJson = provider.getAsJsonArray("chars");

		// resolve "namespace:font/x.png" -> "namespace:textures/font/x.png"
		int colon = file.indexOf(':');
		String namespace = colon >= 0 ? file.substring(0, colon) : "minecraft";
		String path = colon >= 0 ? file.substring(colon + 1) : file;
		ResourceLocation texture = new ResourceLocation(namespace, "textures/" + path);

		BufferedImage image = ImageIO.read(rm.getResource(texture).getInputStream());
		int texWidth = image.getWidth();
		int texHeight = image.getHeight();
		int rows = charsJson.size();
		String firstRow = charsJson.get(0).getAsString();
		int cols = firstRow.codePointCount(0, firstRow.length());
		int cellW = texWidth / cols;
		int cellH = texHeight / rows;
		float scale = (float) height / cellH;

		int count = 0;
		for (int row = 0; row < rows; row++) {
			String line = charsJson.get(row).getAsString();
			int col = 0;
			for (int offset = 0; offset < line.length(); ) {
				int cp = line.codePointAt(offset);
				offset += Character.charCount(cp);
				boolean present = cp < 0x10000 ? bmp[cp] != null : supplementary.containsKey(cp);
				if (cp != 0 && !present) {
					int ink = etfr$inkWidth(image, col * cellW, row * cellH, cellW, cellH);
					int advance = (int) (0.5F + ink * scale) + 1;
					EtfrGlyph glyph = new EtfrGlyph(texture, texWidth, texHeight,
							col * cellW, row * cellH, cellW, cellH, scale, advance, ascent);
					if (cp < 0x10000) {
						bmp[cp] = glyph;
					} else {
						supplementary.put(cp, glyph);
					}
					count++;
				}
				col++;
			}
		}
		return count;
	}

	private static int etfr$inkWidth(BufferedImage image, int cellX, int cellY, int cellW, int cellH) {
		for (int x = cellW - 1; x >= 0; x--) {
			for (int y = 0; y < cellH; y++) {
				if ((image.getRGB(cellX + x, cellY + y) >>> 24) != 0) {
					return x + 1;
				}
			}
		}
		return 0;
	}
}
