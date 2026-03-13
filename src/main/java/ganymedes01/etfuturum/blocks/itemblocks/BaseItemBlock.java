package ganymedes01.etfuturum.blocks.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ganymedes01.etfuturum.blocks.ISubBlocksBlock;

public class BaseItemBlock extends ItemBlock {

    public BaseItemBlock(Block block) {
        super(block);
        setHasSubtypes(true);
        if (!(block instanceof ISubBlocksBlock)) {
            throw new IllegalArgumentException(
                "ItemBlockGeneric instantiation needs to be given a block instance that implements ISubBlocksBlock! Passed in "
                    + block.getClass()
                        .getCanonicalName());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return addTilePrefix(((ISubBlocksBlock) field_150939_a/* blockInstance */).getNameFor(stack));
    }

    private String addTilePrefix(String name) {
        return name.startsWith("tile.") ? name
            : "tile." + ((ISubBlocksBlock) field_150939_a/* blockInstance */).getNameDomain() + "." + name;
    }

    @Override
    public int getMetadata(int meta) {
        return meta % ((ISubBlocksBlock) field_150939_a/* blockInstance */).getTypes().length;
    }

    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return field_150939_a/* blockInstance */.getIcon(2, p_77617_1_);
    }
}
