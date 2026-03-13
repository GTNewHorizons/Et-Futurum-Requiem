package ganymedes01.etfuturum.blocks;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ganymedes01.etfuturum.lib.Reference;

public interface ISubBlocksBlock {

    IIcon[] getIcons();

    String[] getTypes();

    String getNameFor(ItemStack stack);

    default String getTextureDomain() {
        return "";
    }

    default String getTextureSubfolder() {
        return "";
    }

    default String getNameDomain() {
        return Reference.MOD_ID;
    }
}
