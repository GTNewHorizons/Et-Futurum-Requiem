package ganymedes01.etfuturum.items.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import ganymedes01.etfuturum.EtFuturum;
import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.core.utils.Utils;

public class ItemEFRSword extends ItemSword {

    public ItemEFRSword(ToolMaterial material, int durabilityOverride) {
        super(material);
        this.setMaxDamage(durabilityOverride > -1 ? durabilityOverride : material.getMaxUses());
        this.setUnlocalizedName(Utils.getUnlocalisedName("netherite_sword"));
        this.setTextureName("netherite_sword");
        this.setCreativeTab(EtFuturum.creativeTabItems);
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return ModItems.NETHERITE_INGOT.get() == par2ItemStack.getItem()
            || super.getIsRepairable(par1ItemStack, par2ItemStack);
    }
}
