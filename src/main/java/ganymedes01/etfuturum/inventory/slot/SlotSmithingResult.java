package ganymedes01.etfuturum.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.lib.Reference;

public class SlotSmithingResult extends SlotCrafting {

    public SlotSmithingResult(EntityPlayer player, IInventory inv, IInventory result, int index, int x, int y) {
        super(player, inv, result, index, x, y);
    }

    @Override
    public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        super.onPickupFromSlot(player, stack);
        player.playSound(Reference.MCAssetVer + ":block.smithing_table.use", 1F, 1F);
    }
}
