package ganymedes01.etfuturum.blocks.itemblocks;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.item.IBaubleRender;

@Optional.InterfaceList({
		@Optional.Interface(modid = "Baubles|Expanded", iface = "baubles.api.IBauble"),
		@Optional.Interface(modid = "Botania", iface = "vazkii.botania.api.item.IBaubleRender")
})
public class ItemBlockLantern extends ItemBlock implements IBauble, IBaubleRender {

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
	 * The ItemTravelBelt-style plain translate turned out not to hold up in-game (same mirrored/misoriented
	 * look as the old SetArmorModel hook), so this reuses the offset/rotation/scale already tuned empirically
	 * against the actual game render for that hook.
	 */
	private static final float OFFSET_X = 0.0F;
	private static final float OFFSET_Y = 0.7F;
	private static final float OFFSET_Z = -0.15F;
	private static final float ROTATION_X = -26.7F;
	private static final float ROTATION_Y = 44.0F;
	private static final float ROTATION_Z = -144.1F;
	private static final float SIZE = 0.3F;

	@Optional.Method(modid = "Botania")
	@Override
	public void onPlayerBaubleRender(ItemStack stack, RenderPlayerEvent event, RenderType type) {
		if (type != RenderType.BODY) {
			return;
		}
		GL11.glTranslatef(OFFSET_X, OFFSET_Y, OFFSET_Z);
		GL11.glRotatef(ROTATION_X, 1F, 0F, 0F);
		GL11.glRotatef(ROTATION_Y, 0F, 1F, 0F);
		GL11.glRotatef(ROTATION_Z, 0F, 0F, 1F);
		GL11.glScalef(SIZE, SIZE, SIZE);
		RenderManager.instance.itemRenderer.renderItem(event.entityPlayer, stack, 0);
	}

}
