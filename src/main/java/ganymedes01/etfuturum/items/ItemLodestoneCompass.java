package ganymedes01.etfuturum.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLodestoneCompass extends Item {

    private IIcon[] frames;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        frames = new IIcon[32];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = reg.registerIcon(getIconString() + "_" + String.format("%02d", i));
        }
        itemIcon = frames[0];
    }

    @Override
    public boolean hasEffect(ItemStack p_77636_1_) {
        return true;
    }

    /**
     * Return an item rarity from EnumRarity
     */
    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.common;
    }
}
