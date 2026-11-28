package ganymedes01.etfuturum.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemBlockWithTooltip extends ItemBlock {

	public ItemBlockWithTooltip(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> lore, boolean f3h) {
		lore.add(StatCollector.translateToLocal(field_150939_a.getUnlocalizedName() + ".tooltip"));
	}

}
