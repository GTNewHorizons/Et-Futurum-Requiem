package ganymedes01.etfuturum.blocks.itemblocks;

import baubles.api.BaubleType;
import baubles.api.expanded.BaubleItemHelper;
import baubles.api.expanded.IBaubleExpanded;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.item.IBaubleRender;

import java.util.List;

@Optional.InterfaceList({
		@Optional.Interface(modid = "Baubles|Expanded", iface = "baubles.api.expanded.IBaubleExpanded"),
		@Optional.Interface(modid = "Botania", iface = "vazkii.botania.api.item.IBaubleRender")
})
public class ItemBlockLantern extends ItemBlock implements IBaubleExpanded, IBaubleRender {
    private static final String[] BAUBLE_TYPES = {"belt"};

	public ItemBlockLantern(Block block) {
		super(block);
	}

	@Optional.Method(modid = "Baubles|Expanded")
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.BELT;
	}


    @Optional.Method(modid = "Baubles|Expanded")
    @Override
    public String[] getBaubleTypes(ItemStack itemstack) {
        return BAUBLE_TYPES;
    }

    @Optional.Method(modid = "Baubles|Expanded")
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advancedItemTooltips) {
        super.addInformation(stack, player, tooltip, advancedItemTooltips);
        BaubleItemHelper.addSlotInformation(tooltip, BAUBLE_TYPES);
    }

    @Optional.Method(modid = "Baubles|Expanded")
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
	}

	@Optional.Method(modid = "Baubles|Expanded")
	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
	}

	@Optional.Method(modid = "Baubles|Expanded")
	@Override
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
	}

	@Optional.Method(modid = "Baubles|Expanded")
	@Override
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	@Optional.Method(modid = "Baubles|Expanded")
	@Override
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
		return true;
	}

	/*
	 * Draws the stitched item icon at the player's hip, following Botania's own
	 * BaubleRenderHandler#renderManaTablet. The lightmap coordd is reset on the next entity render.
	 */
	private static final float SIZE = 0.45F;

	@Optional.Method(modid = "Botania")
	@SideOnly(Side.CLIENT)
	@Override
	public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, RenderType type) {
		if (type != RenderType.BODY) {
			return;
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
		Helper.rotateIfSneaking(event.entityPlayer);

		// Undo the -1,-1,1 mirror the entity renderer left on the stack.
		// No Y rotation: the sprite stays in the XY plane so it faces forward.
		GL11.glRotatef(180F, 1F, 0F, 0F);
		// Belt height, one hip over, just clear of the body (and of leg armour)
		boolean armor = event.entityPlayer.getCurrentArmor(1) != null;
		GL11.glTranslatef(0.14F, -0.78F, armor ? 0.19F : 0.15F);
		GL11.glScalef(SIZE, SIZE, SIZE);
		// Icon draws from (0,0) to (1,1); recentre so the offsets above are its middle
		GL11.glTranslatef(-0.5F, -0.5F, 0F);

		// Lantern is a light source, so draw it fullbright
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
		GL11.glColor3f(1F, 1F, 1F);

		IIcon icon = stack.getIconIndex();
		ItemRenderer.renderItemIn2D(Tessellator.instance,
				icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(),
				icon.getIconWidth(), icon.getIconHeight(), 1F / 16F);
	}

}
