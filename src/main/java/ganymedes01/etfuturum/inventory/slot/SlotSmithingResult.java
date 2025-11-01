package ganymedes01.etfuturum.inventory.slot;

import ganymedes01.etfuturum.Tags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class SlotSmithingResult extends SlotCrafting {
	public SlotSmithingResult(EntityPlayer player, IInventory inv, IInventory result, int index, int x, int y) {
		super(player, inv, result, index, x, y);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
		super.onPickupFromSlot(player, stack);
		player.playSound(Tags.MC_ASSET_VER + ":block.smithing_table.use", 1F, 1F);
	}
}
