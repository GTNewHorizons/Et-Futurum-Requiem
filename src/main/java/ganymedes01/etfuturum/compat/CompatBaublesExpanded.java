package ganymedes01.etfuturum.compat;

//Do not change from * or it will break soft compat

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
		if (entity instanceof EntityPlayer) {
			for (int slotIndex : wingSlotIDs) {
				ItemStack wings = BaublesApi.getBaubles((EntityPlayer) entity).getStackInSlot(slotIndex);
				if (wings != null && wings.getItem() instanceof ItemArmorElytra) {
					return wings;
				}
			}
		}
		return null;
	}

}
