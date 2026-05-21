package ganymedes01.etfuturum.blocks.itemblocks;

import ganymedes01.etfuturum.blocks.BlockBarrel;
import ganymedes01.etfuturum.core.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemBlockBarrel extends ItemBlock {

	public ItemBlockBarrel(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List lore, boolean f3h) {
		if (field_150939_a instanceof BlockBarrel barrel) {
			lore.add(StatCollector.translateToLocalFormatted(Utils.getUnlocalisedName("barrel.tooltip.slots"), barrel.getType().getSize()));
		}
	}
}
