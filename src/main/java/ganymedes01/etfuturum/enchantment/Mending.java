package ganymedes01.etfuturum.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.configuration.configs.ConfigEnchantsPotions;

public class Mending extends Enchantment {

    public Mending() {
        super(ConfigEnchantsPotions.mendingID, 1, EnumEnchantmentType.breakable);
        Enchantment.addToBookList(this);
        setName("mending");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return enchantmentLevel * 25;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 50;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack != null && stack.getItem() == Items.book;
    }
}
