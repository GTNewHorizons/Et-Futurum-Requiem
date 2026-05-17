package ganymedes01.etfuturum.client.skins;

import ganymedes01.etfuturum.client.model.ModelPlayer;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.lib.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class NewRenderPlayer extends RenderPlayer {

	private boolean cachedAlex;

	public static final ResourceLocation STEVE_SKIN = new ResourceLocation(Reference.MOD_ID, "textures/steve.png");
	private static final ModelPlayer STEVE = new ModelPlayer(0.0F, false),
			STEVE_CHESTPLATE = new ModelPlayer(1.0F, false),
			STEVE_ARMOR = new ModelPlayer(0.5F, false),
			ALEX = new ModelPlayer(0.0F, true),
			ALEX_CHESTPLATE = new ModelPlayer(1.0F, true),
			ALEX_ARMOR = new ModelPlayer(0.5F, true);

	public NewRenderPlayer() {
		renderManager = RenderManager.instance;
		mainModel = modelBipedMain = STEVE;
	}

	private void setModel(EntityPlayer player) {
		if (cachedAlex != PlayerModelManager.isPlayerModelAlex(player)) {
			cachedAlex = PlayerModelManager.isPlayerModelAlex(player);
			mainModel = modelBipedMain = cachedAlex ? ALEX : STEVE;
			modelArmorChestplate = cachedAlex ? ALEX_CHESTPLATE : STEVE_CHESTPLATE;
			modelArmor = cachedAlex ? ALEX_ARMOR : STEVE_ARMOR;

			if (PlayerModelManager.shouldRegenerateModelDelegates((ModelPlayer) mainModel)) {
				((ModelPlayer) mainModel).regenerateDelegates();
				((ModelPlayer) modelArmorChestplate).regenerateDelegates();
				((ModelPlayer) modelArmor).regenerateDelegates();
			}
		}
	}

	@Override
	protected int shouldRenderPass(AbstractClientPlayer player, int pass, float partialTickTime) {
		setModel(player);
		return super.shouldRenderPass(player, pass, partialTickTime);
	}

	@Override
	public void doRender(AbstractClientPlayer player, double x, double y, double z, float someFloat, float partialTickTime) {
		setModel(player);
//		bindEntityTexture(player);
		super.doRender(player, x, y, z, someFloat, partialTickTime);
	}

	@Override
	protected void renderEquippedItems(AbstractClientPlayer player, float partialTickTime) {
		setModel(player);
		super.renderEquippedItems(player, partialTickTime);
	}

	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayer player) {
		if (!ConfigFunctions.enablePlayerSkinOverlay || player.getLocationSkin() == null)
			return super.getEntityTexture(player);
		return new ResourceLocation(Reference.MOD_ID, player.getLocationSkin().getResourcePath());
	}

	/**
	 * Test if the entity name must be rendered
	 * <p>
	 * MCP name: {@code canRenderName}
	 */
	@Override
	protected boolean func_110813_b(EntityLivingBase entity) {
		boolean isGUiEnabled = Minecraft.isGuiEnabled();
		boolean isPlayer = entity != renderManager.livingPlayer;
		boolean isInvisible = !entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
		boolean isBeingRidden = entity.riddenByEntity == null;

		return isGUiEnabled && isPlayer && isInvisible && isBeingRidden;
	}

	@Override
	public void renderFirstPersonArm(EntityPlayer player) {
		setModel(player);
		Minecraft.getMinecraft().getTextureManager().bindTexture(getEntityTexture(player));

		super.renderFirstPersonArm(player);

		// This call is not needed at all due to bipedRightArmwear being a Child of bipedRightArm, therefore moving and rendering with it automatically already.
		//((ModelPlayer) modelBipedMain).bipedRightArmwear.render(0.0625F);
	}
}