package ganymedes01.etfuturum.blocks.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockDecorationWorkbench extends ItemBlock {

    public ItemBlockDecorationWorkbench(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> lore, boolean f3h) {
        lore.add("\u00a7o" + StatCollector.translateToLocal("efr.decoration.only"));
    }

}
