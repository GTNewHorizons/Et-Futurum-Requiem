package ganymedes01.etfuturum.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.core.utils.Utils;

public class ItemBlockRedSandstone extends ItemBlock {

    private static final String[] names = new String[] { "", "chiseled", "cut" };

    public ItemBlockRedSandstone(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getItemDamage() % 3;
        switch (i) {
            case 1:
            case 2:
                return "tile." + Utils.getUnlocalisedName(names[i] + "_red_sandstone");
            default:
                return getUnlocalizedName();
        }
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }
}
