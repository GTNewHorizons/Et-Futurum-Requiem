package ganymedes01.etfuturum;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;

public class ModMaterials {

    public static final ItemArmor.ArmorMaterial NETHERITE_ARMOUR = EnumHelper.addArmorMaterial(
        "Netherite_Armour",
        ConfigBlocksItems.netheriteArmourDurabilityFactor,
        new int[] { ConfigBlocksItems.netheriteHelmetProtection, ConfigBlocksItems.netheriteChestplateProtection,
            ConfigBlocksItems.netheriteLeggingsProtection, ConfigBlocksItems.netheriteBootsProtection },
        ConfigBlocksItems.netheriteEnchantability);
    public static final Item.ToolMaterial NETHERITE_TOOL = EnumHelper.addToolMaterial(
        "Netherite_Tool",
        ConfigBlocksItems.netheriteHarvestLevel,
        ConfigBlocksItems.netheriteToolDurability,
        ConfigBlocksItems.netheriteSpeed,
        ConfigBlocksItems.netheriteDamageBase,
        ConfigBlocksItems.netheriteEnchantability);

    static {
        ModMaterials.NETHERITE_TOOL.setRepairItem(new ItemStack(ModItems.NETHERITE_INGOT.get()));
        ModMaterials.NETHERITE_ARMOUR.customCraftingMaterial = ModItems.NETHERITE_INGOT.get();
    }
}
