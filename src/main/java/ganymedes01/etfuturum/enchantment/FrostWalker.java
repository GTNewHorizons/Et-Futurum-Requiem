package ganymedes01.etfuturum.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import ganymedes01.etfuturum.configuration.configs.ConfigEnchantsPotions;

public class FrostWalker extends Enchantment {

    public FrostWalker() {
        super(ConfigEnchantsPotions.frostWalkerID, 1, EnumEnchantmentType.armor_feet);
        Enchantment.addToBookList(this);
        setName("frost_walker");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return this.getMinEnchantability(enchantmentLevel) + 15;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack != null && stack.getItem() == Items.book;
    }
}
