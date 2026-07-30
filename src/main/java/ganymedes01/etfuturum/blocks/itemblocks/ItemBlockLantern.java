package ganymedes01.etfuturum.blocks.itemblocks;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import cpw.mods.fml.common.Optional;
import ganymedes01.etfuturum.compat.CompatBaublesExpanded;
import ganymedes01.etfuturum.compat.ModsList;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Optional.Interface(modid = "Baubles|Expanded", iface = "baubles.api.IBauble")
public class ItemBlockLantern extends ItemBlock implements IBauble {

	public ItemBlockLantern(Block block) {
		super(block);
	}

	/**
	 * Returns the lantern stack worn in a Baubles-Expanded belt slot, or null if none is worn (or the mod isn't loaded).
	 */
	public static ItemStack getWornBaubleLantern(EntityLivingBase entity) {
		if (ModsList.BAUBLES_EXPANDED.isLoaded()) {
			return CompatBaublesExpanded.getWornLanternFromBaubles(entity);
		}
		return null;
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

}
