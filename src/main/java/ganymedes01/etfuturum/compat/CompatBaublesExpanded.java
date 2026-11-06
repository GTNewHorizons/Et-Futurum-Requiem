package ganymedes01.etfuturum.compat;

//Do not change from * or it will break soft compat

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CompatBaublesExpanded {

	public static int[] wingSlotIDs;

	public static void preInit() {
		BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BaubleExpandedSlots.wingsType, 1);
	}

	public static void postInit() {
		wingSlotIDs = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BaubleExpandedSlots.wingsType);
	}

	public static ItemStack getElytraFromBaubles(EntityLivingBase entity) {
		return getElytraFromBaubles(entity, false);
	}

	public static ItemStack getElytraFromBaubles(EntityLivingBase entity, boolean forRendering) {
		if (entity instanceof EntityPlayer) {
			IInventory baubles = BaublesApi.getBaubles((EntityPlayer) entity);
			if (baubles == null) return null;
			for (int slotIndex = 0; slotIndex < baubles.getSizeInventory(); slotIndex++) {
				ItemStack wings = forRendering ? BaublesApi.getRenderedBauble(baubles, slotIndex) : baubles.getStackInSlot(slotIndex);
				if (wings != null && wings.getItem() instanceof ItemArmorElytra) {
					return wings;
				}
			}
		}
		return null;
	}

}
